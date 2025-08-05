package com.github.hummel.prokhor.service

import com.github.hummel.prokhor.bean.GuildBank
import com.github.hummel.prokhor.bean.GuildData
import net.dv8tion.jda.api.entities.Guild

interface DataService {
	fun loadGuildData(guild: Guild): GuildData
	fun saveGuildData(guild: Guild, guildData: GuildData)

	fun loadGuildBank(guild: Guild): GuildBank
	fun saveGuildBank(guild: Guild, guildBank: GuildBank)

	fun wipeGuildData(guild: Guild)

	fun exportBotData(): ByteArray
	fun importBotData(byteArray: ByteArray)
}