package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class EventosDatabase : RoomDatabase() {
  abstract fun eventDao(): EventDao

  companion object {
    @Volatile
    private var INSTANCE: EventosDatabase? = null

    fun getDatabase(context: Context): EventosDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          EventosDatabase::class.java,
          "eventos_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
