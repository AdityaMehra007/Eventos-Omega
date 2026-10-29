package com.example.data.repository

import com.example.data.local.Event
import com.example.data.local.EventDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository to abstract Room database operations for Event entities,
 * providing a clean, reactive API for ViewModels to interact with stored event data.
 */
class EventRepository(
  private val eventDao: EventDao
) {
  /**
   * Flow of all stored events ordered by creation timestamp (newest first).
   */
  val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

  /**
   * Retrieves events matching a specific status reactively (e.g., "LIVE", "CONFIRMED", "PLANNING").
   */
  fun getEventsByStatus(status: String): Flow<List<Event>> {
    return eventDao.getEventsByStatus(status)
  }

  /**
   * Single event lookup by unique ID.
   */
  suspend fun getEventById(id: String): Event? {
    return eventDao.getEventById(id)
  }

  /**
   * Inserts or updates an event in the local database.
   */
  suspend fun insertEvent(event: Event) {
    eventDao.insertEvent(event)
  }

  /**
   * Inserts multiple events in a single transaction.
   */
  suspend fun insertEvents(events: List<Event>) {
    eventDao.insertEvents(events)
  }

  /**
   * Updates an existing event record.
   */
  suspend fun updateEvent(event: Event) {
    eventDao.updateEvent(event)
  }

  /**
   * Deletes an event from the local database.
   */
  suspend fun deleteEvent(event: Event) {
    eventDao.deleteEvent(event)
  }

  /**
   * Deletes an event by its unique ID.
   */
  suspend fun deleteEventById(id: String) {
    eventDao.deleteEventById(id)
  }

  /**
   * Clears all stored events from the local database.
   */
  suspend fun clearAllEvents() {
    eventDao.clearAllEvents()
  }
}
