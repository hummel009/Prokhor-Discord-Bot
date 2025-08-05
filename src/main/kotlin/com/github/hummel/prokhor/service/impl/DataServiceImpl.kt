package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.bean.GuildData
import com.github.hummel.prokhor.dao.FileDao
import com.github.hummel.prokhor.dao.JsonDao
import com.github.hummel.prokhor.dao.ZipDao
import com.github.hummel.prokhor.factory.DaoFactory
import com.github.hummel.prokhor.service.DataService
import net.dv8tion.jda.api.entities.Guild

class DataServiceImpl : DataService {
	private val fileDao: FileDao = DaoFactory.fileDao
	private val jsonDao: JsonDao = DaoFactory.jsonDao
	private val zipDao: ZipDao = DaoFactory.zipDao

	override fun loadGuildData(guild: Guild): GuildData {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		return jsonDao.readFromFile(filePath, GuildData::class.java) ?: initAndGet(guild)
	}

	override fun saveGuildData(guild: Guild, guildData: GuildData) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		jsonDao.writeToFile(filePath, guildData)
	}

	override fun wipeGuildData(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		fileDao.removeFile(filePath)
		fileDao.createEmptyFile(filePath)
	}

	override fun importBotData(byteArray: ByteArray) {
		val targetFolderPath = "guilds"
		val importFolderPath = "import"
		val importFilePath = "import/bot.zip"

		fileDao.createEmptyFolder(importFolderPath)
		fileDao.createEmptyFile(importFilePath)
		fileDao.writeToFile(importFilePath, byteArray)

		fileDao.removeFolder(targetFolderPath)
		fileDao.createEmptyFolder(targetFolderPath)

		zipDao.unzipFileToFolder(importFilePath, targetFolderPath)

		fileDao.removeFile(importFilePath)
		fileDao.removeFolder(importFolderPath)
	}

	override fun exportBotData(): ByteArray {
		val targetFolderPath = "guilds"
		val exportFolderPath = "export"
		val exportFilePath = "export/bot.zip"

		fileDao.createEmptyFolder(exportFolderPath)
		zipDao.zipFolderToFile(targetFolderPath, exportFilePath)

		val file = fileDao.readFromFile(exportFilePath)

		fileDao.removeFile(exportFilePath)
		fileDao.removeFolder(exportFolderPath)

		return file
	}

	private fun initAndGet(guild: Guild): GuildData {
		val folderName = guild.id
		val serverPath = "guilds/$folderName"
		val dataPath = "guilds/$folderName/data.json"

		fileDao.createEmptyFolder(serverPath)
		fileDao.createEmptyFile(dataPath)

		val guildData = GuildData(
			guildId = guild.idLong,
			guildName = guild.name,
			lang = "ru",
			managerRoleIds = mutableSetOf(),
			excludedChannelIds = mutableSetOf(),
			monitoringChannelId = 0,
		)

		jsonDao.writeToFile(dataPath, guildData)

		return guildData
	}
}