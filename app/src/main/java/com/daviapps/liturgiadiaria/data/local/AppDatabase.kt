package com.daviapps.liturgiadiaria.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LectioDivinaEntity::class, CitacaoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lectioDivinaDao(): LectioDivinaDao
    abstract fun citacaoDao(): CitacaoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "liturgia_diaria.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
