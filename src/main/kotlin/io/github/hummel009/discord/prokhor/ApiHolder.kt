package io.github.hummel009.discord.prokhor

import io.github.hummel009.discord.prokhor.factory.ServiceFactory
import io.github.hummel009.discord.prokhor.handler.EventHandler
import io.github.hummel009.discord.prokhor.utils.config
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

object ApiHolder {
	val discord: JDA by lazy {
		JDABuilder.createDefault(config.discordToken).apply {
			enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
			enableCache(CacheFlag.entries)
			setMemberCachePolicy(MemberCachePolicy.ALL)
			addEventListeners(EventHandler)
		}.build().awaitReady()
	}

	fun establishDiscordConnection() {
		discord

		val startService = ServiceFactory.startService
		startService.recreateCommands()
	}
}