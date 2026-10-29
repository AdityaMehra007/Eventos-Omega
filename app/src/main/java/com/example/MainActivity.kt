package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.EventosDatabase
import com.example.data.network.GeminiService
import com.example.data.repository.EventRepository
import com.example.data.repository.EventosRepository
import com.example.ui.EventosMainApp
import com.example.ui.theme.EventosTheme
import com.example.ui.viewmodel.EventViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  private val database by lazy { EventosDatabase.getDatabase(applicationContext) }
  private val eventRepository by lazy { EventRepository(database.eventDao()) }
  private val eventViewModel by lazy { EventViewModel(eventRepository) }
  private val repository by lazy { EventosRepository(eventDao = database.eventDao()) }
  private val geminiService = GeminiService()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    try {
      FirebaseApp.initializeApp(this)
    } catch (_: Exception) {
      // Firebase initializes via provider automatically; ignore if already initialized
    }

    setContent {
      var isDarkTheme by rememberSaveable { mutableStateOf(true) }

      EventosTheme(darkTheme = isDarkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          EventosMainApp(
            repository = repository,
            eventViewModel = eventViewModel,
            geminiService = geminiService,
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = !isDarkTheme }
          )
        }
      }
    }
  }
}
