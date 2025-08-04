package com.github.hummel.prokhor.service.impl

import com.github.hummel.prokhor.factory.ServiceFactory
import com.github.hummel.prokhor.service.AccessService
import com.github.hummel.prokhor.service.DataService
import com.github.hummel.prokhor.service.ManagerService
import com.github.hummel.prokhor.utils.I18n
import com.github.hummel.prokhor.utils.access
import com.github.hummel.prokhor.utils.error
import com.github.hummel.prokhor.utils.success
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ManagerServiceImpl : ManagerService {
	private val dataService: DataService = ServiceFactory.dataService
	private val accessService: AccessService = ServiceFactory.accessService

	override fun setLanguage(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_language") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val lang = arguments[0]
						if (lang != "ru" && lang != "be" && lang != "uk" && lang != "en") {
							throw Exception()
						}

						guildData.lang = lang

						val langName = I18n.of(lang, guildData)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("set_language", guildData).format(langName)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addManagerRole(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_manager_role") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val roleId = arguments[0].toLong()

						guild.getRoleById(roleId) ?: throw Exception()

						guildData.managerRoleIds.add(roleId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("add_manager_role", guildData).format(roleId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearManagerRoles(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_manager_roles") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("clear_manager_roles", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val roleId = arguments[0].toLong()

							guildData.managerRoleIds.removeIf {
								it == roleId
							}

							EmbedBuilder().success(
								event.member, guildData, I18n.of("clear_manager_roles_single", guildData).format(roleId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addMonitoredChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_monitored_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size == 1) {
					try {
						val channelId = arguments[0].toLong()

						guild.getTextChannelById(
							channelId
						) ?: guild.getThreadChannelById(
							channelId
						) ?: throw Exception()

						guildData.monitoredChannelIds.add(channelId)

						EmbedBuilder().success(
							event.member, guildData, I18n.of("add_monitored_channel", guildData).format(channelId)
						)
					} catch (_: Exception) {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
					}
				} else {
					EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearMonitoredChannels(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_monitored_channels") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.isEmpty()) {
					guildData.monitoredChannelIds.clear()

					EmbedBuilder().success(event.member, guildData, I18n.of("clear_monitored_channels", guildData))
				} else {
					if (arguments.size == 1) {
						try {
							val channelId = arguments[0].toLong()

							guildData.monitoredChannelIds.removeIf { it == channelId }

							EmbedBuilder().success(
								event.member,
								guildData,
								I18n.of("cleared_monitored_channels_single", guildData).format(channelId)
							)
						} catch (_: Exception) {
							EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_format", guildData))
						}
					} else {
						EmbedBuilder().error(event.member, guildData, I18n.of("msg_error_arg", guildData))
					}
				}
			}
			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun wipeData(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "wipe_data") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			val embed = if (!accessService.fromManagerAtLeast(event, guildData)) {
				EmbedBuilder().access(event.member, guildData, I18n.of("msg_access", guildData))
			} else {
				dataService.wipeGuildData(guild)

				EmbedBuilder().success(event.member, guildData, I18n.of("wipe_data", guildData))
			}
			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}