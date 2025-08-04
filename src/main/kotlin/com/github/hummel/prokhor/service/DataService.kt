package com.github.hummel.prokhor.service

import com.github.hummel.prokhor.bean.GuildData
import net.dv8tion.jda.api.entities.Guild

interface DataService {
	fun loadGuildData(guild: Guild): GuildData
	fun saveGuildData(guild: Guild, guildData: GuildData)

	fun wipeGuildData(guild: Guild)

	fun exportBotData(): ByteArray
	fun importBotData(byteArray: ByteArray)
}