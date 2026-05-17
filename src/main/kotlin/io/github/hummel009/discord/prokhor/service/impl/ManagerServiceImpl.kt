package io.github.hummel009.discord.prokhor.service.impl

import io.github.hummel009.discord.prokhor.factory.ServiceFactory
import io.github.hummel009.discord.prokhor.service.AccessService
import io.github.hummel009.discord.prokhor.service.DataService
import io.github.hummel009.discord.prokhor.service.ManagerService
import io.github.hummel009.discord.prokhor.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
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

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val lang = requireNotNull(Lang.of(arguments[0]))
					guildData.lang = lang

					val langName = I18n.of(lang.code, guildData)

					return EmbedBuilder().success(
						event.member, I18n.of("set_language", guildData, langName)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

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

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val roleId = arguments[0].toLong().also {
						requireNotNull(guild.getRoleById(it))
					}

					guildData.managerRoleIds.add(roleId)

					return EmbedBuilder().success(
						event.member, I18n.of("add_manager_role", guildData, roleId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

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

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size !in 0..1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				if (arguments.isEmpty()) {
					guildData.managerRoleIds.clear()

					return EmbedBuilder().success(event.member, I18n.of("clear_manager_roles", guildData))
				}

				try {
					val roleId = arguments[0].toLong().also {
						requireNotNull(guild.getRoleById(it))
					}

					require(guildData.managerRoleIds.removeIf { it == roleId })

					return EmbedBuilder().success(
						event.member, I18n.of("clear_manager_roles_single", guildData, roleId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun addExcludedChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "add_excluded_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val channelId = arguments[0].toLong().also {
						requireNotNull(guild.getMessageChannelById(it))
					}

					guildData.excludedChannelIds.add(channelId)

					return EmbedBuilder().success(
						event.member, I18n.of("add_excluded_channel", guildData, channelId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun clearExcludedChannels(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "clear_excluded_channels") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size !in 0..1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				if (arguments.isEmpty()) {
					guildData.excludedChannelIds.clear()

					return EmbedBuilder().success(event.member, I18n.of("clear_excluded_channels", guildData))
				}

				try {
					val channelId = arguments[0].toLong().also {
						requireNotNull(guild.getMessageChannelById(it))
					}

					require(guildData.excludedChannelIds.removeIf { it == channelId })

					return EmbedBuilder().success(
						event.member, I18n.of("cleared_excluded_channels_single", guildData, channelId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

			dataService.saveGuildData(guild, guildData)

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun setLogChannel(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "set_log_channel") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run(fun(): MessageEmbed {
				val arguments = event.getOption("arguments")?.asString?.split(" ") ?: emptyList()
				if (arguments.size != 1) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_arg", guildData))
				}

				try {
					val channelId = arguments[0].toLong().also {
						requireNotNull(guild.getMessageChannelById(it))
					}

					guildData.logChannelId = channelId

					return EmbedBuilder().success(
						event.member, I18n.of("set_log_channel", guildData, channelId)
					)
				} catch (_: Exception) {
					return EmbedBuilder().error(event.member, I18n.of("msg_error_format", guildData))
				}
			})

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

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run {
				dataService.wipeGuildData(guild)

				EmbedBuilder().success(event.member, I18n.of("wipe_data", guildData))
			}

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}

	override fun wipeBank(event: SlashCommandInteractionEvent) {
		if (event.fullCommandName != "wipe_bank") {
			return
		}

		event.deferReply().queue {
			val guild = event.guild ?: return@queue
			val guildData = dataService.loadGuildData(guild)

			accessService.managerAccessRestricted(event, guildData)?.let {
				return@queue
			}

			val embed = run {
				dataService.wipeGuildBank(guild)

				EmbedBuilder().success(event.member, I18n.of("wipe_bank", guildData))
			}

			event.hook.sendMessageEmbeds(embed).queue()
		}
	}
}