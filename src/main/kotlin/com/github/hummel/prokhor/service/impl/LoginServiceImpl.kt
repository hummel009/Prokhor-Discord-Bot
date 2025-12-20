package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.ApiHolder
import com.github.hummel.prokhor.bean.BotData
import com.github.hummel.prokhor.handler.EventHandler
import com.github.hummel.prokhor.service.LoginService
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

class LoginServiceImpl : LoginService {
	override fun loginBot(reinit: Boolean) {
		ApiHolder.discord = JDABuilder.createDefault(BotData.token).apply {
			enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
			enableCache(CacheFlag.entries)
			setMemberCachePolicy(MemberCachePolicy.ALL)
			addEventListeners(EventHandler)
		}.build().awaitReady()

		if (reinit) {
			recreateCommands()
		}
	}

	private fun recreateCommands() {
		fun String.cmd(description: String, options: List<OptionData>) =
			Commands.slash(this, description).addOptions(options)

		val commands = listOf(
			"info".cmd("/info", empty()),

			"set_language".cmd("/set_language [ru/be/uk/en]", string()),
			"set_log_channel".cmd("/set_log_channel [channel_id]", string()),

			"add_manager_role".cmd("/add_manager_role [role_id]", string()),
			"clear_manager_roles".cmd("/clear_manager_roles {role_id}", string(false)),

			"add_excluded_channel".cmd("/add_excluded_channel [channel_id]", string()),
			"clear_excluded_channels".cmd("/clear_excluded_channels {channel_id}", string(false)),

			"wipe_data".cmd("/wipe_data", empty()),
			"wipe_bank".cmd("/wipe_bank", empty()),

			"import".cmd("/import", attachment()),
			"export".cmd("/export", empty()),
			"exit".cmd("/exit", empty())
		)
		ApiHolder.discord.updateCommands().addCommands(commands).complete()
	}

	private fun empty(): List<OptionData> = emptyList()

	private fun string(obligatory: Boolean = true): List<OptionData> = listOf(
		OptionData(OptionType.STRING, "arguments", "The list of arguments", obligatory)
	)

	private fun attachment(): List<OptionData> = listOf(
		OptionData(OptionType.ATTACHMENT, "arguments", "The list of arguments", true)
	)
}