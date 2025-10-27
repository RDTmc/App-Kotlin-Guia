package com.milsabores.appkotlin_guia.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.milsabores.appkotlin_guia.model.Users

@Database(entities = [Users::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "appkotlin_guia.db"
                )
                    // .fallbackToDestructiveMigration() // habilita si cambias version sin migraciones
                    .build().also { INSTANCE = it }
            }
    }
}
