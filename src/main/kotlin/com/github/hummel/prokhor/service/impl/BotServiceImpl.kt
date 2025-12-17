package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.bean.Message
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
import kotlin.collections.set
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

		val guildBank = dataService.loadGuildBank(guild)

		val messageId = event.message.idLong
		val messageAuthorId = event.message.author.idLong
		var messageContent = event.message.contentStripped

		if (messageContent.isEmpty()) {
			messageContent = "N/A"
		}

		val channelBank = guildBank.channelsToBanks.getOrPut(channelId) { linkedMapOf() }
		channelBank[messageId] = Message(messageAuthorId, messageContent.encode())

		if (channelBank.size > 1000) {
			channelBank.remove(channelBank.keys.first())
		}

		dataService.saveGuildBank(guild, guildBank)
	}

	override fun reportMessageEdited(event: MessageUpdateEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

			val channelId = event.channel.idLong

			if (guildData.excludedChannelIds.any { it == channelId }) {
				return
			}

			val logsChannel = event.guild.getTextChannelById(
				guildData.logChannelId
			) ?: event.guild.getThreadChannelById(
				guildData.logChannelId
			) ?: throw Exception()

			val guildBank = dataService.loadGuildBank(event.guild)

			val messageId = event.message.idLong
			val messageAuthorId = event.message.author.idLong
			var messageContent = event.message.contentStripped

			if (messageContent.isEmpty()) {
				messageContent = "N/A"
			}

			val channelBank = guildBank.channelsToBanks[channelId] ?: return
			val (cachedAuthorId, encodedContent) = channelBank[messageId] ?: return
			val cachedContent = encodedContent.decode()

			if (messageContent.trim() == cachedContent.trim()) {
				return
			}

			channelBank[messageId] = Message(messageAuthorId, messageContent.encode())

			val user = event.jda.getUserById(cachedAuthorId)
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user?.effectiveName, null, user?.effectiveAvatarUrl)
				setTitle(I18n.of("title_msg_edited", guildData))
				setDescription("${wrap(cachedContent)}${wrap(messageContent)}")
				setColor(0xFFFF00)
			}.build()).queue()

			dataService.saveGuildBank(event.guild, guildBank)
		} catch (_: Exception) {
		}
	}

	override fun reportMessageDeleted(event: MessageDeleteEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

			val channelId = event.channel.idLong

			if (guildData.excludedChannelIds.any { it == channelId }) {
				return
			}

			val logsChannel = event.guild.getTextChannelById(
				guildData.logChannelId
			) ?: event.guild.getThreadChannelById(
				guildData.logChannelId
			) ?: throw Exception()

			val guildBank = dataService.loadGuildBank(event.guild)

			val messageId = event.messageIdLong

			val channelBank = guildBank.channelsToBanks[channelId] ?: return
			val (cachedAuthorId, encodedContent) = channelBank[messageId] ?: return
			val cachedContent = encodedContent.decode()

			if (cachedContent.trim().isEmpty()) {
				return
			}

			channelBank.remove(messageId)

			val user = event.jda.getUserById(cachedAuthorId)
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user?.effectiveName, null, user?.effectiveAvatarUrl)
				setTitle(I18n.of("title_msg_deleted", guildData))
				setDescription(wrap(cachedContent))
				setColor(0xFF0000)
			}.build()).queue()
		} catch (_: Exception) {
		}
	}

	override fun reportUserJoined(event: GuildMemberJoinEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

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
		} catch (_: Exception) {
		}
	}

	override fun reportUserLeft(event: GuildMemberRemoveEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

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
		} catch (_: Exception) {
		}
	}

	private fun wrap(text: String): String =
		"```" + text.replace("`", "") + "```"
}
