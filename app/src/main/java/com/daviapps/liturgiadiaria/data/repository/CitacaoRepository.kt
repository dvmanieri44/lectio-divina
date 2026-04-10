package com.daviapps.liturgiadiaria.data.repository

import com.daviapps.liturgiadiaria.data.local.CitacaoDao
import com.daviapps.liturgiadiaria.data.local.CitacaoEntity
import kotlinx.coroutines.flow.Flow

class CitacaoRepository(private val dao: CitacaoDao) {

    fun getAll(): Flow<List<CitacaoEntity>> = dao.getAll()

    suspend fun getAllOnce(): List<CitacaoEntity> = dao.getAllOnce()

    suspend fun inserir(citacao: CitacaoEntity) = dao.inserir(citacao)

    suspend fun deletar(citacao: CitacaoEntity) = dao.deletar(citacao)
}
