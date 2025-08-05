package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.BotService
import com.github.hummel.prokhor.service.DataService
import com.github.hummel.prokhor.utils.I18n
import com.github.hummel.prokhor.utils.decode
import com.github.hummel.prokhor.utils.encode
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import kotlin.random.Random

class BotServiceImpl : BotService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun saveMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)

		val channelId = event.channel.idLong

		if (guildData.excludedChannelIds.any { it == channelId }) {
			return
		}

		val message = event.message.contentRaw

		val guildBank = dataService.loadGuildBank(guild)

		val innerMap = guildBank.channelsToBanks.getOrPut(channelId) { linkedMapOf() }
		innerMap[event.message.idLong] = message.encode()
		if (innerMap.size > 100) {
			innerMap.remove(innerMap.keys.first())
		}

		dataService.saveGuildBank(guild, guildBank)
	}

	override fun reportMessageEdited(event: MessageUpdateEvent) {
		val guildData = dataService.loadGuildData(event.guild)

		if (guildData.excludedChannelIds.any { it == event.channel.idLong }) {
			return
		}

		val logsChannel = event.guild.getTextChannelById(
			guildData.logChannelId
		) ?: event.guild.getThreadChannelById(
			guildData.logChannelId
		) ?: throw Exception()

		val message = event.message.contentRaw

		val messageId = event.message.idLong

		val guildBank = dataService.loadGuildBank(event.guild)
		val channelArchived = guildBank.channelsToBanks[event.channel.idLong] ?: return
		val messageArchived = channelArchived[messageId]?.decode() ?: return

		(guildBank.channelsToBanks[event.channel.idLong] ?: return)[messageId] = message.encode()

		logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
			setTitle(I18n.of("title_msg_edited", guildData))
			setDescription("> $messageArchived\r\n\r\n> $message")
			setColor(0xFFFF00)
		}.build()).queue()

		dataService.saveGuildBank(event.guild, guildBank)
	}

	override fun reportMessageDeleted(event: MessageDeleteEvent) {
		val guildData = dataService.loadGuildData(event.guild)

		if (guildData.excludedChannelIds.any { it == event.channel.idLong }) {
			return
		}

		val logsChannel = event.guild.getTextChannelById(
			guildData.logChannelId
		) ?: event.guild.getThreadChannelById(
			guildData.logChannelId
		) ?: throw Exception()

		val messageId = event.messageIdLong

		val guildBank = dataService.loadGuildBank(event.guild)
		val channelArchived = guildBank.channelsToBanks[event.channel.idLong] ?: return
		val messageArchived = channelArchived[messageId]?.decode() ?: return

		logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
			setTitle(I18n.of("title_msg_deleted", guildData))
			setDescription("> $messageArchived")
			setColor(0xFF0000)
		}.build()).queue()
	}

	override fun reportUserJoined(event: GuildMemberJoinEvent) {
		val guildData = dataService.loadGuildData(event.guild)

		val logsChannel = event.guild.getTextChannelById(
			guildData.logChannelId
		) ?: event.guild.getThreadChannelById(
			guildData.logChannelId
		) ?: throw Exception()

		val user = event.user

		logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
			setAuthor(user.effectiveName, null, user.effectiveAvatarUrl)
			setTitle(I18n.of("title_user_joined", guildData))
			setDescription(I18n.of("desc_user_joined_${Random.nextInt(4)}", guildData))
			setColor(0x00FF00)
		}.build()).queue()
	}

	override fun reportUserLeft(event: GuildMemberRemoveEvent) {
		val guildData = dataService.loadGuildData(event.guild)

		val logsChannel = event.guild.getTextChannelById(
			guildData.logChannelId
		) ?: event.guild.getThreadChannelById(
			guildData.logChannelId
		) ?: throw Exception()

		val user = event.user

		logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
			setAuthor(user.effectiveName, null, user.effectiveAvatarUrl)
			setTitle(I18n.of("title_user_left", guildData))
			setDescription(I18n.of("desc_user_left_${Random.nextInt(4)}", guildData))
			setColor(0xFF0000)
		}.build()).queue()
	}
}