package com.github.hummel.prokhor.handler

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.BotService
import com.github.hummel.prokhor.service.ManagerService
import com.github.hummel.prokhor.service.MemberService
import com.github.hummel.prokhor.service.OwnerService
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object EventHandler : ListenerAdapter() {
	private val memberService: MemberService = ServiceFactory.memberService
	private val managerService: ManagerService = ServiceFactory.managerService
	private val ownerService: OwnerService = ServiceFactory.ownerService
	private val botService: BotService = ServiceFactory.botService

	override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
		memberService.info(event)

		managerService.addManagerRole(event)
		managerService.clearManagerRoles(event)

		managerService.setLanguage(event)
		managerService.setLogChannel(event)

		managerService.addExcludedChannel(event)
		managerService.clearExcludedChannels(event)

		managerService.wipeData(event)
		managerService.wipeBank(event)

		ownerService.import(event)
		ownerService.export(event)
		ownerService.exit(event)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		botService.saveMessage(event)
	}

	override fun onMessageUpdate(event: MessageUpdateEvent) {
		botService.reportMessageEdited(event)
	}

	override fun onMessageDelete(event: MessageDeleteEvent) {
		botService.reportMessageDeleted(event)
	}

	override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
		botService.reportUserJoined(event)
	}

	override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
		botService.reportUserLeft(event)
	}
}