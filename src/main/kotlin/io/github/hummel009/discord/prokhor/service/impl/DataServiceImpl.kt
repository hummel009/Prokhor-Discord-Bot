package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.bean.GuildBank
import io.github.hummel009.discord.prokhor.bean.GuildData
import io.github.hummel009.discord.prokhor.dao.FileDao
import io.github.hummel009.discord.prokhor.dao.JsonDao
import io.github.hummel009.discord.prokhor.dao.ZipDao
import io.github.hummel009.discord.prokhor.factory.DaoFactory
import io.github.hummel009.discord.prokhor.service.DataService
import net.dv8tion.jda.api.entities.Guild

class DataServiceImpl : DataService {
	private val fileDao: FileDao = DaoFactory.fileDao
	private val jsonDao: JsonDao = DaoFactory.jsonDao
	private val zipDao: ZipDao = DaoFactory.zipDao

	override fun loadGuildData(guild: Guild): GuildData {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		return jsonDao.readFromFile(filePath, GuildData::class.java) ?: initAndGetGuildData(guild)
	}

	override fun saveGuildData(guild: Guild, guildData: GuildData) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		jsonDao.writeToFile(filePath, guildData)
	}

	override fun loadGuildBank(guild: Guild): GuildBank {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.json"

		return jsonDao.readFromFile(filePath, GuildBank::class.java) ?: initAndGetGuildBank(guild)
	}

	override fun saveGuildBank(guild: Guild, guildBank: GuildBank) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.json"

		jsonDao.writeToFile(filePath, guildBank)
	}

	override fun wipeGuildData(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/data.json"

		fileDao.removeFile(filePath)
		fileDao.createEmptyFile(filePath)
	}

	override fun wipeGuildBank(guild: Guild) {
		val folderName = guild.id
		val filePath = "guilds/$folderName/bank.json"

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

	private fun initAndGetGuildData(guild: Guild): GuildData = GuildData(
		guildName = guild.name,
		lang = "ru",
		logChannelId = 0,
		managerRoleIds = mutableSetOf(),
		excludedChannelIds = mutableSetOf(),
	)

	private fun initAndGetGuildBank(guild: Guild): GuildBank = GuildBank(
		guild.name,
		mutableMapOf()
	)
}