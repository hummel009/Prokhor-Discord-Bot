package com.github.hummel.prokhor.service

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent

interface BotService {
	fun saveMessage(event: MessageReceivedEvent)
	fun reportMessageEdited(event: MessageUpdateEvent)
	fun reportMessageDeleted(event: MessageDeleteEvent)
	fun reportUserJoined(event: GuildMemberJoinEvent)
	fun reportUserLeft(event: GuildMemberRemoveEvent)
}