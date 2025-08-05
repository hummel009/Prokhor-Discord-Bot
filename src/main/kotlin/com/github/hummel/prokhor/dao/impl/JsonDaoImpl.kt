package com.github.hummel.prokhor.dao.impl

import com.github.hummel.prokhor.dao.FileDao
import com.github.hummel.prokhor.dao.JsonDao
import com.github.hummel.prokhor.factory.DaoFactory
import com.github.hummel.prokhor.utils.gson

class JsonDaoImpl : JsonDao {
	private val fileDao: FileDao = DaoFactory.fileDao

	override fun <T> readFromFile(filePath: String, clazz: Class<T>): T? {
		val file = fileDao.getFile(filePath)

		if (!file.exists()) {
			return null
		}

		try {
			val json = String(fileDao.readFromFile(filePath))
			return gson.fromJson(json, clazz)
		} catch (_: Exception) {
			return null
		}
	}

	override fun <T> writeToFile(filePath: String, obj: T) {
		val file = fileDao.getFile(filePath)

		if (!file.exists()) {
			fileDao.createEmptyFile(filePath)
		}

		val json = gson.toJson(obj)
		fileDao.writeToFile(filePath, json.toByteArray())
	}
}