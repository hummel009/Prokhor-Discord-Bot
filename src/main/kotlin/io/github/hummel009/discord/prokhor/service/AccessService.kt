package io.github.hummel009.discord.prokhor.service

import io.github.hummel009.discord.prokhor.bean.GuildData
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface AccessService {
	fun managerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed?
	fun ownerAccessRestricted(event: SlashCommandInteractionEvent, guildData: GuildData): MessageEmbed?
}