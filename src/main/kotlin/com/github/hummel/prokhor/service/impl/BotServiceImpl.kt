package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.BotService
import com.github.hummel.prokhor.service.DataService
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class BotServiceImpl : BotService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun saveMessage(event: MessageReceivedEvent) {
		val guild = event.guild
		val guildData = dataService.loadGuildData(guild)
		val channelId = event.channel.idLong

		if (guildData.excludedChannelIds.any { it == channelId }) {
			return
		}
	}
}