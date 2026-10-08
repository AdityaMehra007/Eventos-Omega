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
  fun `test opportunity category filtering and sorting`() {
    val repo = EventosRepository()
    val allOpps = repo.opportunities.value
    assertTrue(allOpps.isNotEmpty())

    val musicGigs = allOpps.filter { it.category == "Music" }
    assertTrue(musicGigs.isNotEmpty())
    assertTrue(musicGigs.all { it.category == "Music" })

    val corporateGigs = allOpps.filter { it.category == "Corporate" }
    assertTrue(corporateGigs.isNotEmpty())
    assertTrue(corporateGigs.all { it.category == "Corporate" })

    val weddingGigs = allOpps.filter { it.category == "Weddings" }
    assertTrue(weddingGigs.isNotEmpty())
    assertTrue(weddingGigs.all { it.category == "Weddings" })

    val techGigs = allOpps.filter { it.category == "Technical Support" }
    assertTrue(techGigs.isNotEmpty())
    assertTrue(techGigs.all { it.category == "Technical Support" })

    val sortedByPay = allOpps.sortedByDescending { it.payRateINR }
    assertTrue(sortedByPay.first().payRateINR >= sortedByPay.last().payRateINR)
  }

  @Test
  fun `test book gig updates opportunity and stores booking`() {
    val repo = EventosRepository()
    val opp = repo.opportunities.value.first { it.id == "opp_002" }
    assertFalse(opp.isBooked)

    val booking = repo.bookGig(opp)
    assertNotNull(booking)
    assertEquals(opp.id, booking.opportunityId)
    assertTrue(booking.bookingReference.startsWith("EVT-BKG-"))
    assertEquals("CONFIRMED_BOOKED", booking.status)
    assertEquals("ESCROW_SECURED", booking.escrowStatus)

    // Verify bookings list contains newly created booking
    val recorded = repo.bookings.value.firstOrNull { it.id == booking.id }
    assertNotNull(recorded)

    // Verify opportunity is marked booked
    val updatedOpp = repo.opportunities.value.first { it.id == "opp_002" }
    assertTrue(updatedOpp.isBooked)
  }

  @Test
  fun `test user profile management with bio skills and portfolio`() {
    val repo = EventosRepository()
    val initialUser = repo.currentUser.value
    assertEquals("Aditya Mehra", initialUser.name)
    assertTrue(initialUser.bio.isNotEmpty())
    assertTrue(initialUser.skills.isNotEmpty())
    assertTrue(initialUser.portfolio.isNotEmpty())

    val newBio = "Senior Concert Technical Director in Bangalore with extensive DiGiCo & L-Acoustics experience."
    val newSkills = listOf("Live Audio DSP", "Stage Automation", "Crowd Telemetry")
    val newPortfolioItem = com.example.data.model.PortfolioItem(
      id = "port_test_99",
      title = "Palace Grounds Mega Rigging 2026",
      category = "Technical Support",
      eventName = "Bangalore Music Festival",
      year = "2026",
      role = "Technical Director",
      description = "Full stage trussing, line array alignment and safety protocol.",
      metrics = "15,000 Attendees • Zero Failure"
    )

    repo.updateUserProfile(
      bio = newBio,
      skills = newSkills,
      portfolio = listOf(newPortfolioItem) + initialUser.portfolio,
      title = "Chief Technical Producer"
    )

    val updatedUser = repo.currentUser.value
    assertEquals(newBio, updatedUser.bio)
    assertEquals("Chief Technical Producer", updatedUser.title)
    assertEquals(3, updatedUser.skills.size)
    assertTrue(updatedUser.skills.contains("Live Audio DSP"))
    assertEquals(initialUser.portfolio.size + 1, updatedUser.portfolio.size)
    assertEquals("port_test_99", updatedUser.portfolio.first().id)

    // Test removing portfolio item
    repo.removePortfolioItem("port_test_99")
    val finalUser = repo.currentUser.value
    assertEquals(initialUser.portfolio.size, finalUser.portfolio.size)
    assertNull(finalUser.portfolio.firstOrNull { it.id == "port_test_99" })
  }

  @Test
  fun `test gig list search filter by event title and description text`() {
    val repo = EventosRepository()
    val allOpps = repo.opportunities.value
    assertTrue(allOpps.isNotEmpty())

    // 1. Search by event title (e.g. "Zepto" or "Carnival")
    val queryTitle = "zepto"
    val filteredByTitle = allOpps.filter {
      it.eventTitle.lowercase().contains(queryTitle) || it.description.lowercase().contains(queryTitle)
    }
    assertTrue(filteredByTitle.isNotEmpty())
    assertTrue(filteredByTitle.all { it.eventTitle.contains("Zepto", ignoreCase = true) || it.description.contains("Zepto", ignoreCase = true) })

    // 2. Search by description keyword (e.g. "truss" or "mixing" or "crowd")
    val queryDesc = "mixing"
    val filteredByDesc = allOpps.filter {
      it.eventTitle.lowercase().contains(queryDesc) || it.description.lowercase().contains(queryDesc)
    }
    assertTrue(filteredByDesc.isNotEmpty())
    assertTrue(filteredByDesc.any { it.description.contains("mixing", ignoreCase = true) })

    // 3. Search non-existent term
    val queryNonExistent = "nonexistent_gig_xyz_123"
    val emptyResult = allOpps.filter {
      it.eventTitle.lowercase().contains(queryNonExistent) || it.description.lowercase().contains(queryNonExistent)
    }
    assertTrue(emptyResult.isEmpty())

    // 4. Combined with category filter
    val category = "Technical Support"
    val combinedResult = allOpps.filter {
      it.category.equals(category, ignoreCase = true) &&
        (it.eventTitle.lowercase().contains("sound") || it.description.lowercase().contains("microphone"))
    }
    assertTrue(combinedResult.isNotEmpty())
  }

  @Test
  fun `test organizer ratings and reviews for professionals stored in reviews collection`() {
    val repo = EventosRepository()
    val initialReviews = repo.reviews.value
    assertTrue(initialReviews.isNotEmpty())
    val initialReviewCount = repo.currentUser.value.reviewCount

    // Organizer leaves a review after a gig is completed
    val newReview = repo.submitReview(
      gigId = "opp_003",
      eventTitle = "Zepto Corporate Annual Leadership Meet",
      professionalId = "usr_blr_001",
      professionalName = "Aditya Mehra",
      organizerId = "org_zepto_corp",
      organizerName = "Ananya Singhal",
      organizerOrganization = "Zepto India Leadership Team",
      roleExecuted = "Lead Stage & AV Production Director",
      rating = 5.0f,
      reviewText = "Flawless execution! Managed live wireless mic frequencies for executive team and 4K LED keynotes without a single hitch. Highly professional and dependable.",
      punctualScore = 5.0f,
      technicalCompetenceScore = 5.0f,
      teamworkScore = 5.0f
    )

    assertNotNull(newReview)
    assertTrue(newReview.id.startsWith("rev_"))
    assertEquals("Zepto Corporate Annual Leadership Meet", newReview.eventTitle)
    assertEquals(5.0f, newReview.rating)
    assertEquals("Ananya Singhal", newReview.organizerName)
    assertTrue(newReview.verifiedGigBadge)

    // Verify it is added to the reviews StateFlow (and persisted to 'reviews' collection)
    val updatedReviews = repo.reviews.value
    assertEquals(initialReviews.size + 1, updatedReviews.size)
    assertEquals(newReview.id, updatedReviews.first().id)
    assertEquals("Zepto Corporate Annual Leadership Meet", updatedReviews.first().eventTitle)

    // Verify professional user's reviewCount and rating are updated
    val updatedUser = repo.currentUser.value
    assertEquals(initialReviewCount + 1, updatedUser.reviewCount)
    assertTrue(updatedUser.rating in 4.0f..5.0f)
  }

  @Test
  fun `test my bookings querying bookings collection for active and completed gigs`() {
    val repo = EventosRepository()
    val allBookings = repo.bookings.value
    assertTrue(allBookings.isNotEmpty())

    val currentUser = repo.currentUser.value
    val userBookings = allBookings.filter { it.workerId == currentUser.id || it.workerName == currentUser.name }
    assertTrue(userBookings.isNotEmpty())

    // Check active bookings
    val activeBookings = userBookings.filter {
      it.status.contains("ACTIVE", ignoreCase = true) || it.status.contains("CONFIRMED", ignoreCase = true)
    }
    assertTrue(activeBookings.isNotEmpty())
    assertTrue(activeBookings.all { it.workerId == currentUser.id || it.workerName == currentUser.name })

    // Check completed bookings
    val completedBookings = userBookings.filter {
      it.status.contains("COMPLETED", ignoreCase = true)
    }
    assertTrue(completedBookings.isNotEmpty())
    assertTrue(completedBookings.all { it.workerId == currentUser.id || it.workerName == currentUser.name })

    // Test booking a new gig adds to active bookings
    val targetOpp = repo.opportunities.value.first()
    val newBooking = repo.bookGig(targetOpp)
    assertNotNull(newBooking)
    assertEquals(currentUser.id, newBooking.workerId)
    assertEquals(currentUser.name, newBooking.workerName)

    val updatedBookings = repo.bookings.value
    assertEquals(allBookings.size + 1, updatedBookings.size)
    assertEquals(newBooking.id, updatedBookings.first().id)
    assertEquals("CONFIRMED_BOOKED", newBooking.status)
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
