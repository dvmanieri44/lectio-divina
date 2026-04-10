package com.daviapps.liturgiadiaria.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CitacaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(citacao: CitacaoEntity)

    @Query("SELECT * FROM citacoes ORDER BY criadaEm DESC")
    fun getAll(): Flow<List<CitacaoEntity>>

    @Query("SELECT * FROM citacoes ORDER BY criadaEm DESC")
    suspend fun getAllOnce(): List<CitacaoEntity>

    @Delete
    suspend fun deletar(citacao: CitacaoEntity)
}
