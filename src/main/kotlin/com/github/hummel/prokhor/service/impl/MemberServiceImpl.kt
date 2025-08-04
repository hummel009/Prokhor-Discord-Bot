package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.DataService
import com.github.hummel.prokhor.service.MemberService
import com.github.hummel.prokhor.utils.I18n
import com.github.hummel.prokhor.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class MemberServiceImpl : MemberService {
	private val dataService: DataService = ServiceFactory.dataService

	override fun info(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "info") {
			return
		}

		event.deferReply().queue { consumer ->
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			guildData.managerRoleIds.removeIf {
				guild.getRoleById(it) == null
			}

			guildData.monitoredChannelIds.removeIf {
				guild.getTextChannelById(it) == null && guild.getThreadChannelById(it) == null
			}

			val text = buildString {
				val langName = I18n.of(guildData.lang, guildData)
				append(I18n.of("info_language", guildData).format(langName), "\r\n")

				if (guildData.managerRoleIds.isEmpty()) {
					append("\r\n", I18n.of("no_manager_roles", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_manager_roles", guildData), "\r\n")
					guildData.managerRoleIds.joinTo(this, "\r\n") { roleId ->
						I18n.of("manager_role", guildData).format(roleId)
					}
					append("\r\n")
				}
				if (guildData.monitoredChannelIds.isEmpty()) {
					append("\r\n", I18n.of("no_monitored_channels", guildData), "\r\n")
				} else {
					append("\r\n", I18n.of("has_monitored_channels", guildData), "\r\n")
					guildData.monitoredChannelIds.joinTo(this, "\r\n") {
						I18n.of("monitored_channel", guildData).format(it)
					}
					append("\r\n")
				}
			}
			dataService.saveGuildData(guild, guildData)

			val embed = EmbedBuilder().success(event.member, guildData, text)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}