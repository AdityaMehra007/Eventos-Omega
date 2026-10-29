package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing an Event with id (Primary Key), name, date, venue, and status.
 */
@Entity(tableName = "events")
data class Event(
  @PrimaryKey
  val id: String = java.util.UUID.randomUUID().toString(),
  val name: String,
  val date: String,
  val venue: String,
  val status: String,
  val areaZone: String = "Bengaluru",
  val timeSlot: String = "09:00 - 18:00",
  val clientName: String = "Event Client",
  val description: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

// Backwards-compatibility alias
typealias EventEntity = Event
