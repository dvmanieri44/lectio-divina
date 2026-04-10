package com.daviapps.liturgiadiaria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectio_divina")
data class LectioDivinaEntity(
    @PrimaryKey val data: String,           // "DD/MM/YYYY" — chave única por dia
    val evangelhoReferencia: String = "",
    val lectioNota: String = "",            // Leitura
    val meditatioNota: String = "",         // Meditação
    val oratioNota: String = "",            // Oração
    val contemplatiaNota: String = "",      // Contemplação
    val actioNota: String = "",             // Ação
    val criadoEm: Long = System.currentTimeMillis()
)
