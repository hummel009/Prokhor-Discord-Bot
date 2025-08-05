package com.github.hummel.prokhor.service

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface ManagerService {
	fun setLanguage(event: SlashCommandInteractionEvent)
	fun addManagerRole(event: SlashCommandInteractionEvent)
	fun clearManagerRoles(event: SlashCommandInteractionEvent)
	fun addExcludedChannel(event: SlashCommandInteractionEvent)
	fun clearExcludedChannels(event: SlashCommandInteractionEvent)
	fun setMonitoringChannel(event: SlashCommandInteractionEvent)
	fun wipeData(event: SlashCommandInteractionEvent)
}