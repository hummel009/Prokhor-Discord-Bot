package com.github.hummel.prokhor.service

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface BotService {
	fun saveMessage(event: MessageReceivedEvent)
}