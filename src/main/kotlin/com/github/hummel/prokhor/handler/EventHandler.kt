package com.github.hummel.prokhor.handler

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.ManagerService
import com.github.hummel.prokhor.service.MemberService
import com.github.hummel.prokhor.service.OwnerService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object EventHandler : ListenerAdapter() {
	private val memberService: MemberService = ServiceFactory.memberService
	private val managerService: ManagerService = ServiceFactory.managerService
	private val ownerService: OwnerService = ServiceFactory.ownerService

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
}