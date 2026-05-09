package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.ApiHolder
import io.github.hummel009.discord.prokhor.service.StartService
import io.github.hummel009.discord.prokhor.utils.config
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class StartServiceImpl : StartService {
	override fun recreateCommands() {
		if (!config.reinit) {
			return
		}

		val commands = listOf(
			withoutOptions("info", "/info"),
			withoutOptions("wipe_data", "/wipe_data"),
			withoutOptions("wipe_bank", "/wipe_bank"),
			withoutOptions("export", "/export"),
			withoutOptions("exit", "/exit"),

			withStringOption("set_language", "/set_language [ru/be/uk/en]"),
			withStringOption("set_log_channel", "/set_log_channel [channel_id]"),
			withStringOption("add_manager_role", "/add_manager_role [role_id]"),
			withStringOption("clear_manager_roles", "/clear_manager_roles {role_id}", false),
			withStringOption("add_excluded_channel", "/add_excluded_channel [channel_id]"),
			withStringOption("clear_excluded_channels", "/clear_excluded_channels {channel_id}", false),

			withAttachmentOption("import", "/import")
		)

		ApiHolder.discord.updateCommands().addCommands(commands).complete()
	}

	private fun withoutOptions(name: String, description: String): SlashCommandData =
		Commands.slash(name, description).addOptions(emptyList())

	private fun withStringOption(name: String, description: String, obligatory: Boolean = true): SlashCommandData =
		Commands.slash(name, description)
			.addOptions(OptionData(OptionType.STRING, "arguments", "The list of arguments", obligatory))

	private fun withAttachmentOption(name: String, description: String, obligatory: Boolean = true): SlashCommandData =
		Commands.slash(name, description)
			.addOptions(OptionData(OptionType.ATTACHMENT, "arguments", "The list of arguments", obligatory))
}