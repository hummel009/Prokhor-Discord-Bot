package com.github.hummel.prokhor.bean

data class GuildData(
	val guildName: String,
	var lang: String,
	var logChannelId: Long,
	val managerRoleIds: MutableSet<Long>,
	var excludedChannelIds: MutableSet<Long>
)