package io.github.hummel009.discord.prokhor

import io.github.hummel009.discord.prokhor.bean.BotData
import io.github.hummel009.discord.prokhor.factory.ServiceFactory
import io.github.hummel009.discord.prokhor.utils.gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class Config(
	val token: String, val ownerId: String, val reinit: Boolean
)

fun main() {
	try {
		val file = File("input/config.json")
		if (file.exists()) {
			FileReader(file).use {
				val config = _root_ide_package_.io.github.hummel009.discord.prokhor.utils.gson.fromJson(it, _root_ide_package_.io.github.hummel009.discord.prokhor.Config::class.java)

				_root_ide_package_.io.github.hummel009.discord.prokhor.launchWithData(config, "output")
			}
		} else {
			_root_ide_package_.io.github.hummel009.discord.prokhor.requestUserInput()
		}
	} catch (_: Exception) {
		_root_ide_package_.io.github.hummel009.discord.prokhor.requestUserInput()
	}
}

fun requestUserInput() {
	print("Enter the Token: ")
	val token = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	val config = _root_ide_package_.io.github.hummel009.discord.prokhor.Config(token, ownerId, reinit.toBoolean())
	try {
		val file = File("input/config.json")
		FileWriter(file).use {
			_root_ide_package_.io.github.hummel009.discord.prokhor.utils.gson.toJson(config, it)
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	_root_ide_package_.io.github.hummel009.discord.prokhor.launchWithData(config, "output")
}

fun launchWithData(config: io.github.hummel009.discord.prokhor.Config, root: String) {
	_root_ide_package_.io.github.hummel009.discord.prokhor.bean.BotData.token = config.token
	_root_ide_package_.io.github.hummel009.discord.prokhor.bean.BotData.ownerId = config.ownerId
	_root_ide_package_.io.github.hummel009.discord.prokhor.bean.BotData.root = root

	val loginService = _root_ide_package_.io.github.hummel009.discord.prokhor.factory.ServiceFactory.loginService
	loginService.loginBot(config.reinit)
}