package com.github.hummel.prokhor.utils

import com.github.hummel.prokhor.bean.GuildData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import java.util.*

fun EmbedBuilder.success(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_success", guildData))
	setDescription(desc)
	setColor(0x00FF00)
}.build()

fun EmbedBuilder.access(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_access", guildData))
	setDescription(desc)
	setColor(0xFFFF00)
}.build()

fun EmbedBuilder.error(member: Member?, guildData: GuildData, desc: String): MessageEmbed = apply {
	member ?: return@apply

	setAuthor(member.effectiveName, null, member.effectiveAvatarUrl)
	setTitle(I18n.of("title_error", guildData))
	setDescription(desc)
	setColor(0xFF0000)
}.build()

fun String.encode(): String {
	val bytes = toByteArray(Charsets.UTF_8)
	return Base64.getEncoder().encodeToString(bytes).reversed()
}

fun String.decode(): String {
	val bytes = Base64.getDecoder().decode(reversed())
	return bytes.toString(Charsets.UTF_8)
}