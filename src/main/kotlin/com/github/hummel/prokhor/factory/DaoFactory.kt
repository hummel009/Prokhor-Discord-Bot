package com.github.hummel.prokhor.factory

import com.github.hummel.prokhor.dao.FileDao
import com.github.hummel.prokhor.dao.JsonDao
import com.github.hummel.prokhor.dao.ZipDao
import com.github.hummel.prokhor.dao.impl.FileDaoImpl
import com.github.hummel.prokhor.dao.impl.JsonDaoImpl
import com.github.hummel.prokhor.dao.impl.ZipDaoImpl

@Suppress("unused", "RedundantSuppression")
object DaoFactory {
	val zipDao: ZipDao by lazy { ZipDaoImpl() }
	val jsonDao: JsonDao by lazy { JsonDaoImpl() }
	val fileDao: FileDao by lazy { FileDaoImpl() }
}