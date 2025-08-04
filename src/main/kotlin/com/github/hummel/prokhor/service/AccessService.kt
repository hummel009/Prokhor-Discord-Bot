package com.github.hummel.prokhor.service

import com.github.hummel.prokhor.bean.GuildData
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface AccessService {
	fun fromManagerAtLeast(event: SlashCommandInteractionEvent, guildData: GuildData): Boolean
	fun fromOwnerAtLeast(event: SlashCommandInteractionEvent): Boolean
}