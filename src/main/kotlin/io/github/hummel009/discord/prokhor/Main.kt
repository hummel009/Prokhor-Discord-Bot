package io.github.hummel009.discord.prokhor

import io.github.hummel009.discord.prokhor.utils.gson
import io.github.hummel009.discord.prokhor.utils.input
import java.io.File
import java.io.FileWriter

data class Config(
	val discordToken: String, val ownerId: String, val reinit: Boolean
)

fun main() {
	ensureConfigExists()

	ApiHolder.establishConnections()
}

fun ensureConfigExists() {
	val file = File(input, "config.json")
	if (file.exists()) {
		return
	}

	print("Enter the Discord Token: ")
	val discordToken = readln()

	print("Enter the Owner ID: ")
	val ownerId = readln()

	print("Reinit? Type true/false: ")
	val reinit = readln()

	FileWriter(file).use {
		gson.toJson(Config(discordToken, ownerId, reinit.toBoolean()), it)
	}
}