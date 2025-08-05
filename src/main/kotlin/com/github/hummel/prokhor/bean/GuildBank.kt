package com.github.hummel.prokhor.bean

data class GuildBank(
	val guildName: String,
	var channelsToBanks: MutableMap<Long, MutableMap<Long, Long>>
)