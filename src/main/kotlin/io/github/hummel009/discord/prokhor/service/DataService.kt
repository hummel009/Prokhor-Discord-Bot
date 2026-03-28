package io.github.hummel009.discord.prokhor.service

import io.github.hummel009.discord.prokhor.bean.GuildBank
import io.github.hummel009.discord.prokhor.bean.GuildData
import net.dv8tion.jda.api.entities.Guild

interface DataService {
	fun loadGuildData(guild: Guild): GuildData
	fun saveGuildData(guild: Guild, guildData: GuildData)

	fun loadGuildBank(guild: Guild): GuildBank
	fun saveGuildBank(guild: Guild, guildBank: GuildBank)

	fun wipeGuildData(guild: Guild)
	fun wipeGuildBank(guild: Guild)

	fun exportBotData(): ByteArray
	fun importBotData(byteArray: ByteArray)
}