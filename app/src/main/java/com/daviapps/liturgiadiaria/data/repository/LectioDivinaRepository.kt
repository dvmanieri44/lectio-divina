package com.daviapps.liturgiadiaria.data.repository

import com.daviapps.liturgiadiaria.data.local.LectioDivinaDao
import com.daviapps.liturgiadiaria.data.local.LectioDivinaEntity
import kotlinx.coroutines.flow.Flow

class LectioDivinaRepository(private val dao: LectioDivinaDao) {

    suspend fun getByData(data: String): LectioDivinaEntity? = dao.getByData(data)

    fun getAll(): Flow<List<LectioDivinaEntity>> = dao.getAll()

    suspend fun salvar(entry: LectioDivinaEntity) = dao.salvar(entry)
}
