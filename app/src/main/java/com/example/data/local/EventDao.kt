package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
  @Query("SELECT * FROM events ORDER BY createdAt DESC")
  fun getAllEvents(): Flow<List<EventEntity>>

  @Query("SELECT * FROM events WHERE id = :id")
  suspend fun getEventById(id: String): EventEntity?

  @Query("SELECT * FROM events WHERE status = :status ORDER BY createdAt DESC")
  fun getEventsByStatus(status: String): Flow<List<EventEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: EventEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<EventEntity>)

  @Update
  suspend fun updateEvent(event: EventEntity)

  @Delete
  suspend fun deleteEvent(event: EventEntity)

  @Query("DELETE FROM events WHERE id = :id")
  suspend fun deleteEventById(id: String)

  @Query("DELETE FROM events")
  suspend fun clearAllEvents()
}
