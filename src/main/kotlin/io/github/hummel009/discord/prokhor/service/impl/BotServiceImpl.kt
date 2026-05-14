package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.bean.Message
import io.github.hummel009.discord.prokhor.factory.ServiceFactory
import io.github.hummel009.discord.prokhor.service.BotService
import io.github.hummel009.discord.prokhor.service.DataService
import io.github.hummel009.discord.prokhor.utils.I18n
import io.github.hummel009.discord.prokhor.utils.getMessageChannelById
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import java.util.*
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
		channelBank[messageId] = Message(messageAuthorId, messageContent.encrypt())

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

			val logsChannel = event.guild.getMessageChannelById(guildData.logChannelId) ?: throw Exception()

			val guildBank = dataService.loadGuildBank(event.guild)

			val messageId = event.message.idLong
			val messageAuthorId = event.message.author.idLong
			var messageContent = event.message.contentStripped

			if (messageContent.isEmpty()) {
				messageContent = "N/A"
			}

			val channelBank = guildBank.channelsToBanks[channelId] ?: return
			val (cachedAuthorId, encodedContent) = channelBank[messageId] ?: return
			val cachedContent = encodedContent.decrypt()

			if (messageContent.trim() == cachedContent.trim()) {
				return
			}

			channelBank[messageId] = Message(messageAuthorId, messageContent.encrypt())

			val user = event.jda.getUserById(cachedAuthorId)
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user?.effectiveName, null, user?.effectiveAvatarUrl)
				setTitle(I18n.of("title_msg_edited", guildData).s())
				setDescription("${cachedContent.wrap()}${messageContent.wrap()}")
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

			val logsChannel = event.guild.getMessageChannelById(guildData.logChannelId) ?: throw Exception()

			val guildBank = dataService.loadGuildBank(event.guild)

			val messageId = event.messageIdLong

			val channelBank = guildBank.channelsToBanks[channelId] ?: return
			val (cachedAuthorId, encodedContent) = channelBank[messageId] ?: return
			val cachedContent = encodedContent.decrypt()

			if (cachedContent.trim().isEmpty()) {
				return
			}

			channelBank.remove(messageId)

			val user = event.jda.getUserById(cachedAuthorId)
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user?.effectiveName, null, user?.effectiveAvatarUrl)
				setTitle(I18n.of("title_msg_deleted", guildData).s())
				setDescription(cachedContent.wrap())
				setColor(0xFF0000)
			}.build()).queue()
		} catch (_: Exception) {
		}
	}

	override fun reportUserJoined(event: GuildMemberJoinEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

			val logsChannel = event.guild.getMessageChannelById(guildData.logChannelId) ?: throw Exception()

			val user = event.user
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user.effectiveName, null, user.effectiveAvatarUrl)
				setTitle(I18n.of("title_user_joined", guildData).s())
				setDescription(I18n.of("desc_user_joined_${Random.nextInt(4)}", guildData).s())
				setColor(0x00FF00)
			}.build()).queue()
		} catch (_: Exception) {
		}
	}

	override fun reportUserLeft(event: GuildMemberRemoveEvent) {
		try {
			val guild = event.guild
			val guildData = dataService.loadGuildData(guild)

			val logsChannel = event.guild.getMessageChannelById(guildData.logChannelId) ?: throw Exception()

			val user = event.user
			logsChannel.sendMessageEmbeds(EmbedBuilder().apply {
				setAuthor(user.effectiveName, null, user.effectiveAvatarUrl)
				setTitle(I18n.of("title_user_left", guildData).s())
				setDescription(I18n.of("desc_user_left_${Random.nextInt(4)}", guildData).s())
				setColor(0xFF0000)
			}.build()).queue()
		} catch (_: Exception) {
		}
	}

	private fun String.wrap(): String =
		"```" + replace("`", "") + "```"

	private fun String.encrypt(): String {
		val bytes = toByteArray(Charsets.UTF_8)
		return Base64.getEncoder().encodeToString(bytes).reversed()
	}

	private fun String.decrypt(): String {
		val bytes = Base64.getDecoder().decode(reversed())
		return bytes.toString(Charsets.UTF_8)
	}
}
