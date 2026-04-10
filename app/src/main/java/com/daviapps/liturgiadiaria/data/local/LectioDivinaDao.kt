package com.daviapps.liturgiadiaria.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LectioDivinaDao {

    @Query("SELECT * FROM lectio_divina WHERE data = :data")
    suspend fun getByData(data: String): LectioDivinaEntity?

    @Query("SELECT * FROM lectio_divina ORDER BY criadoEm DESC")
    fun getAll(): Flow<List<LectioDivinaEntity>>

    @Upsert
    suspend fun salvar(entry: LectioDivinaEntity)
}
