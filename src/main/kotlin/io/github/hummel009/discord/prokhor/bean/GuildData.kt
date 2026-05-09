package io.github.hummel009.discord.prokhor.bean

import io.github.hummel009.discord.prokhor.utils.Lang

data class GuildData(
	val guildName: String,
	var lang: Lang,
	var logChannelId: Long,
	val managerRoleIds: MutableSet<Long>,
	var excludedChannelIds: MutableSet<Long>
)