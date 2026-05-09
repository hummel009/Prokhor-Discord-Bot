package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.ApiHolder
import io.github.hummel009.discord.prokhor.service.StartService
import io.github.hummel009.discord.prokhor.utils.Lang
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
			withoutOptions("exit"),
			withoutOptions("export"),
			withoutOptions("info"),
			withoutOptions("wipe_bank"),
			withoutOptions("wipe_data"),

			withStringOption("add_excluded_channel", "[channel_id]"),
			withStringOption("add_manager_role", "[role_id]"),
			withStringOption("set_language", "[${Lang.entries.joinToString("/")}]"),
			withStringOption("set_log_channel", "[channel_id]"),

			withStringOption("clear_excluded_channels", "{channel_id}", false),
			withStringOption("clear_manager_roles", "{role_id}", false),

			withAttachmentOption("import")
		)

		ApiHolder.discord.updateCommands().addCommands(commands).complete()
	}

	private fun withoutOptions(command: String): SlashCommandData =
		Commands.slash(command, "/$command").addOptions(emptyList())

	private fun withStringOption(command: String, parameters: String, obligatory: Boolean = true): SlashCommandData =
		Commands.slash(command, "/$command $parameters")
			.addOptions(OptionData(OptionType.STRING, "arguments", "The list of arguments", obligatory))

	private fun withAttachmentOption(command: String): SlashCommandData =
		Commands.slash(command, "/$command")
			.addOptions(OptionData(OptionType.ATTACHMENT, "arguments", "The list of arguments", true))
}