package com.github.hummel.prokhor.handler

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.*
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object EventHandler : ListenerAdapter() {
	private val memberService: MemberService = ServiceFactory.memberService
	private val managerService: ManagerService = ServiceFactory.managerService
	private val ownerService: OwnerService = ServiceFactory.ownerService
	private val botService: BotService = ServiceFactory.botService
	private val dataService: DataService = ServiceFactory.dataService

	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
		memberService.info(event)

		managerService.addManagerRole(event)
		managerService.clearManagerRoles(event)

		managerService.setLanguage(event)
		managerService.setLogChannel(event)

		managerService.addExcludedChannel(event)
		managerService.clearExcludedChannels(event)

		managerService.wipeData(event)

		ownerService.import(event)
		ownerService.export(event)
		ownerService.exit(event)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		botService.saveMessage(event)
	}

	override fun onMessageUpdate(event: MessageUpdateEvent) {
		val logsChannel = getChannelOrSkip(event) ?: return

		logsChannel.sendMessage("").queue()
	}

	private fun getChannelOrSkip(event: GenericMessageEvent): GuildMessageChannel? {
		val guildData = dataService.loadGuildData(event.guild)

		if (guildData.excludedChannelIds.any { it == event.channel.idLong }) {
			return null
		}

		return event.guild.getTextChannelById(
			guildData.logChannelId
		) ?: event.guild.getThreadChannelById(
			guildData.logChannelId
		) ?: throw Exception()
	}
}