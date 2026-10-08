package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AIAgentRole
import com.example.data.model.UserRole
import com.example.data.network.GeminiService
import com.example.data.repository.EventosRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Eventos Omega", appName)
  }

  @Test
  fun `test opportunity application updates state`() {
    val repo = EventosRepository()
    val initialOpp = repo.opportunities.value.first { it.id == "opp_001" }
    assertFalse(initialOpp.isApplied)

    val applied = repo.applyForOpportunity("opp_001")
    assertTrue(applied)

    val updatedOpp = repo.opportunities.value.first { it.id == "opp_001" }
    assertTrue(updatedOpp.isApplied)
  }

  @Test
  fun `test live check in and incident logging`() {
    val repo = EventosRepository()
    val initialEvent = repo.events.value.first { it.id == "evt_001" }
    val initialCheckedIn = initialEvent.staffCheckedIn

    val checkIn = repo.checkInWorker("Naveen Kumar", "QR Scanner")
    assertEquals("Checked In", checkIn.status)

    val updatedEvent = repo.events.value.first { it.id == "evt_001" }
    assertEquals(initialCheckedIn + 1, updatedEvent.staffCheckedIn)

    val incident = repo.logIncident("Gate 2 Congestion", "Crowd Flow", "HIGH", "Deployed backup queue marshals")
    assertEquals("Investigating", incident.status)

    repo.resolveIncident(incident.id, "Queue cleared in 3 minutes")
    val resolved = repo.incidents.value.first { it.id == incident.id }
    assertEquals("Resolved", resolved.status)
  }

  @Test
  fun `test user role switching and payout disbursement`() {
    val repo = EventosRepository()
    assertEquals(UserRole.MANAGER, repo.currentUser.value.role)

    repo.switchUserRole(UserRole.WORKER)
    assertEquals(UserRole.WORKER, repo.currentUser.value.role)

    repo.disbursePayout("pay_04")
    val payout = repo.payouts.value.first { it.id == "pay_04" }
    assertEquals("DISBURSED_UPI", payout.status)
  }

  @Test
  fun `test gemini service roles and response generation`() = runBlocking {
    val gemini = GeminiService()
    val response = gemini.chatWithAgent(
      agentRole = AIAgentRole.EVENT_PLANNER,
      conversationHistory = emptyList(),
      userMessage = "Plan stage setup for Palace Grounds",
      useThinking = false
    )
    assertNotNull(response)
    assertTrue(response.text.isNotEmpty())
  }
}
