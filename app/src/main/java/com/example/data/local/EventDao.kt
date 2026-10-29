package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Event local database persistence.
 */
@Dao
interface EventDao {
  @Query("SELECT * FROM events ORDER BY createdAt DESC")
  fun getAllEvents(): Flow<List<Event>>

  @Query("SELECT * FROM events WHERE id = :id")
  suspend fun getEventById(id: String): Event?

  @Query("SELECT * FROM events WHERE status = :status ORDER BY createdAt DESC")
  fun getEventsByStatus(status: String): Flow<List<Event>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: Event)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<Event>)

  @Update
  suspend fun updateEvent(event: Event)

  @Delete
  suspend fun deleteEvent(event: Event)

  @Query("DELETE FROM events WHERE id = :id")
  suspend fun deleteEventById(id: String)

  @Query("DELETE FROM events")
  suspend fun clearAllEvents()
}
