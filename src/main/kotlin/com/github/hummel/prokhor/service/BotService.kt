package com.github.hummel.prokhor.service

import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent

interface BotService {
	fun saveMessage(event: MessageReceivedEvent)
	fun reportEdited(event: MessageUpdateEvent)
	fun reportDeleted(event: MessageDeleteEvent)
}