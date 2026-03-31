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
				val config = gson.fromJson(it, Config::class.java)

				launchWithData(config, "output")
			}
		} else {
			requestUserInput()
		}
	} catch (_: Exception) {
		requestUserInput()
	}
}

fun requestUserInput() {
	print("Enter the Token: ")
	val token = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	val config = Config(token, ownerId, reinit.toBoolean())
	try {
		val file = File("input/config.json")
		FileWriter(file).use {
			gson.toJson(config, it)
		}
	} catch (e: Exception) {
		e.printStackTrace()
	}

	launchWithData(config, "output")
}

fun launchWithData(config: Config, root: String) {
	BotData.token = config.token
	BotData.ownerId = config.ownerId
	BotData.root = root

	val loginService = ServiceFactory.loginService
	loginService.loginBot(config.reinit)
}