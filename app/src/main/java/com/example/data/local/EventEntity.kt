package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val date: String,
  val venue: String,
  val status: String,
  val areaZone: String = "Central Bengaluru",
  val timeSlot: String = "09:00 - 18:00",
  val attendeesExpected: Int = 1000,
  val totalStaffNeeded: Int = 20,
  val budgetEstimatedINR: Long = 1000000L,
  val clientName: String = "Corporate Client",
  val description: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
