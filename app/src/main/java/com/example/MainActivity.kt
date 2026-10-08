package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.network.GeminiService
import com.example.data.repository.EventosRepository
import com.example.ui.EventosMainApp
import com.example.ui.theme.DarkSurfaceBase
import com.example.ui.theme.EventosTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  private val repository = EventosRepository()
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
      EventosTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkSurfaceBase
        ) {
          EventosMainApp(
            repository = repository,
            geminiService = geminiService
          )
        }
      }
    }
  }
}
