package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.factory.ServiceFactory
import io.github.hummel009.discord.prokhor.service.DataService
import io.github.hummel009.discord.prokhor.service.MemberService
import io.github.hummel009.discord.prokhor.utils.I18n
import io.github.hummel009.discord.prokhor.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class MemberServiceImpl : MemberService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun info(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "info") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			guildData.managerRoleIds.removeIf {
				guild.getRoleById(it) == null
			}

			guildData.excludedChannelIds.removeIf {
				guild.getTextChannelById(it) == null && guild.getThreadChannelById(it) == null
			}

			val text = buildString {
				val langName = I18n.of(guildData.lang, guildData)
				append(I18n.of("info_language", guildData, langName), "\n")
				append(I18n.of("info_log_channel", guildData, guildData.logChannelId), "\n")

				if (guildData.managerRoleIds.isEmpty()) {
					append("\n", I18n.of("no_manager_roles", guildData), "\n")
				} else {
					append("\n", I18n.of("has_manager_roles", guildData), "\n")
					guildData.managerRoleIds.joinTo(this, "\n") {
						I18n.of("manager_role", guildData, it).s()
					}
					append("\n")
				}

				if (guildData.excludedChannelIds.isEmpty()) {
					append("\n", I18n.of("no_excluded_channels", guildData), "\n")
				} else {
					append("\n", I18n.of("has_excluded_channels", guildData), "\n")
					guildData.excludedChannelIds.joinTo(this, "\n") {
						I18n.of("excluded_channel", guildData, it).s()
					}
					append("\n")
				}
			}
			dataService.saveGuildData(guild, guildData)

			val embed = EmbedBuilder().success(event.member, I18n(text, guildData.lang))

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}