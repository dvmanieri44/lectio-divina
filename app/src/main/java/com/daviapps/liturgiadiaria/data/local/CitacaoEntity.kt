package com.daviapps.liturgiadiaria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citacoes")
data class CitacaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val texto: String,
    val evangelhoReferencia: String,
    val evangelhoData: String,
    val criadaEm: Long = System.currentTimeMillis()
)
