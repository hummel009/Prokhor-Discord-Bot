package com.github.hummel.prokhor.bean

data class GuildBank(
	var channelsToBanks: MutableMap<Long, MutableMap<Long, Long>>
)