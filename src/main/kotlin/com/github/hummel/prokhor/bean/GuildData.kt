package com.github.hummel.prokhor.bean

data class GuildData(
	val guildId: Long,
	val guildName: String,
	var lang: String,
	val managerRoleIds: MutableSet<Long>,
	var monitoredChannelIds: MutableSet<Long>
)