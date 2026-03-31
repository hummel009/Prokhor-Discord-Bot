package io.github.hummel009.discord.prokhor.bean

data class GuildBank(
	val guildName: String,
	var channelsToBanks: MutableMap<Long, LinkedHashMap<Long, Message>>
)