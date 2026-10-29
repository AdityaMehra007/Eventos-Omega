package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.Event
import com.example.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for the Event List Screen.
 */
data class EventUiState(
  val events: List<Event> = emptyList(),
  val isLoading: Boolean = false,
  val selectedFilterStatus: String = "ALL", // "ALL", "PLANNING", "CONFIRMED", "LIVE", "COMPLETED"
  val searchQuery: String = ""
)

/**
 * EventViewModel to manage and expose event state from EventRepository to the Compose UI.
 */
class EventViewModel(
  private val eventRepository: EventRepository
) : ViewModel() {

  private val _selectedStatus = MutableStateFlow("ALL")
  val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  /**
   * Observable stream of all events persisted in the Room database.
   */
  val events: StateFlow<List<Event>> = eventRepository.allEvents
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  fun setFilterStatus(status: String) {
    _selectedStatus.value = status
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  /**
   * Adds a new event to the Room database.
   */
  fun addEvent(
    name: String,
    date: String,
    venue: String,
    status: String,
    areaZone: String = "Bengaluru",
    clientName: String = "Client Partner",
    timeSlot: String = "09:00 - 18:00",
    description: String = ""
  ) {
    viewModelScope.launch {
      val newEvent = Event(
        name = name.trim(),
        date = date.trim(),
        venue = venue.trim(),
        status = status.trim(),
        areaZone = areaZone.trim(),
        clientName = clientName.trim(),
        timeSlot = timeSlot.trim(),
        description = description.trim()
      )
      eventRepository.insertEvent(newEvent)
    }
  }

  /**
   * Deletes an event by ID from the Room database.
   */
  fun deleteEvent(id: String) {
    viewModelScope.launch {
      eventRepository.deleteEventById(id)
    }
  }

  /**
   * Updates an existing event in the Room database.
   */
  fun updateEvent(event: Event) {
    viewModelScope.launch {
      eventRepository.updateEvent(event)
    }
  }

  /**
   * ViewModelFactory to instantiate EventViewModel with EventRepository.
   */
  class Factory(
    private val eventRepository: EventRepository
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
        return EventViewModel(eventRepository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}
