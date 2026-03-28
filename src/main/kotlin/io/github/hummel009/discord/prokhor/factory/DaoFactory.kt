package io.github.hummel009.discord.prokhor.factory

import io.github.hummel009.discord.prokhor.dao.FileDao
import io.github.hummel009.discord.prokhor.dao.JsonDao
import io.github.hummel009.discord.prokhor.dao.ZipDao
import io.github.hummel009.discord.prokhor.dao.impl.FileDaoImpl
import io.github.hummel009.discord.prokhor.dao.impl.JsonDaoImpl
import io.github.hummel009.discord.prokhor.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}