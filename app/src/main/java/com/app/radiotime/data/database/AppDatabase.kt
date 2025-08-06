package com.app.radiotime.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.radiotime.data.models.Favorite
import com.app.radiotime.data.models.Station
import com.app.radiotime.utils.Constants.DATABASE

@Database(
    entities = [Station::class, Favorite::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun radioStationDao(): EntityDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, DATABASE
            ).addMigrations().build()
        }
    }
}