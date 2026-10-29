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

  @Test
  fun `test room database event entity dao persistence operations`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val dao = db.eventDao()

    val testEvent = com.example.data.local.Event(
      id = "evt_room_001",
      name = "Bangalore EDM Carnival",
      date = "15 Nov 2026",
      venue = "Manpho Convention Centre",
      status = "CONFIRMED",
      areaZone = "Nagavara",
      clientName = "Sunburn India"
    )

    // Insert
    dao.insertEvent(testEvent)

    // Query by ID
    val retrieved = dao.getEventById("evt_room_001")
    assertNotNull(retrieved)
    assertEquals("Bangalore EDM Carnival", retrieved?.name)
    assertEquals("15 Nov 2026", retrieved?.date)
    assertEquals("Manpho Convention Centre", retrieved?.venue)
    assertEquals("CONFIRMED", retrieved?.status)

    // Update
    val updatedEvent = retrieved!!.copy(status = "LIVE")
    dao.updateEvent(updatedEvent)
    val checkUpdated = dao.getEventById("evt_room_001")
    assertEquals("LIVE", checkUpdated?.status)

    // Delete
    dao.deleteEventById("evt_room_001")
    val afterDelete = dao.getEventById("evt_room_001")
    assertNull(afterDelete)

    db.close()
  }

  @Test
  fun `test event repository clean api for viewmodel interactions`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repository = com.example.data.repository.EventRepository(db.eventDao())

    val event1 = com.example.data.local.Event(
      id = "repo_evt_001",
      name = "Koramangala Tech Startup Showcase",
      date = "20 Nov 2026",
      venue = "Koramangala Indoor Stadium",
      status = "PLANNING",
      areaZone = "Koramangala"
    )
    val event2 = com.example.data.local.Event(
      id = "repo_evt_002",
      name = "Whitefield AI Summit",
      date = "22 Nov 2026",
      venue = "KTPO Trade Centre",
      status = "CONFIRMED",
      areaZone = "Whitefield"
    )

    // Insert events via repository
    repository.insertEvents(listOf(event1, event2))

    // Query single event
    val fetched1 = repository.getEventById("repo_evt_001")
    assertNotNull(fetched1)
    assertEquals("Koramangala Tech Startup Showcase", fetched1?.name)
    assertEquals("PLANNING", fetched1?.status)

    // Update via repository
    repository.updateEvent(fetched1!!.copy(status = "CONFIRMED"))
    val updated1 = repository.getEventById("repo_evt_001")
    assertEquals("CONFIRMED", updated1?.status)

    // Delete single event via repository
    repository.deleteEventById("repo_evt_002")
    val fetched2 = repository.getEventById("repo_evt_002")
    assertNull(fetched2)

    // Clear all
    repository.clearAllEvents()
    val afterClear = repository.getEventById("repo_evt_001")
    assertNull(afterClear)

    db.close()
  }

  @Test
  fun `test event viewmodel exposes state and handles add event`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repository = com.example.data.repository.EventRepository(db.eventDao())
    val viewModel = com.example.ui.viewmodel.EventViewModel(repository)

    // Initial state
    assertEquals("ALL", viewModel.selectedStatus.value)
    assertEquals("", viewModel.searchQuery.value)

    // Filter change
    viewModel.setFilterStatus("LIVE")
    assertEquals("LIVE", viewModel.selectedStatus.value)

    // Search query change
    viewModel.setSearchQuery("Palace")
    assertEquals("Palace", viewModel.searchQuery.value)

    // Add event via viewModel
    viewModel.addEvent(
      name = "Bangalore Design Fest",
      date = "05 Dec 2026",
      venue = "Jayachamaraja Road",
      status = "PLANNING",
      areaZone = "Central Bengaluru"
    )

    db.close()
  }

  @Test
  fun `test swipe to delete triggers repository delete and updates state`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repository = com.example.data.repository.EventRepository(db.eventDao())
    val viewModel = com.example.ui.viewmodel.EventViewModel(repository)

    val eventToDelete = com.example.data.local.Event(
      id = "swipe_del_001",
      name = "Electronic City IoT Meetup",
      date = "28 Nov 2026",
      venue = "Infosys Gate 1",
      status = "CONFIRMED"
    )

    // Insert into DB
    repository.insertEvent(eventToDelete)
    val checkInserted = repository.getEventById("swipe_del_001")
    assertNotNull(checkInserted)

    // Trigger delete action (same method invoked by SwipeToDismissBox confirmValueChange)
    viewModel.deleteEvent("swipe_del_001")

    // Verify deleted from repository
    val checkDeleted = repository.getEventById("swipe_del_001")
    assertNull(checkDeleted)

    db.close()
  }

  @Test
  fun `test real time search query filters events by name and venue`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repository = com.example.data.repository.EventRepository(db.eventDao())
    val viewModel = com.example.ui.viewmodel.EventViewModel(repository)

    val ev1 = com.example.data.local.Event(
      id = "ev_search_1",
      name = "Global Fintech Summit",
      date = "12 Dec 2026",
      venue = "Palace Grounds",
      status = "CONFIRMED"
    )
    val ev2 = com.example.data.local.Event(
      id = "ev_search_2",
      name = "Indie Food Festival",
      date = "14 Dec 2026",
      venue = "Koramangala Stadium",
      status = "UPCOMING"
    )

    repository.insertEvents(listOf(ev1, ev2))

    // Test search by name
    viewModel.setSearchQuery("Fintech")
    assertEquals("Fintech", viewModel.searchQuery.value)

    // Test search by venue
    viewModel.setSearchQuery("Koramangala")
    assertEquals("Koramangala", viewModel.searchQuery.value)

    // Test clearing search
    viewModel.setSearchQuery("")
    assertEquals("", viewModel.searchQuery.value)

    db.close()
  }

  @Test
  fun `test status slice distribution computation for statistics screen`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.EventosDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repository = com.example.data.repository.EventRepository(db.eventDao())

    val events = listOf(
      com.example.data.local.Event(id = "st_1", name = "E1", date = "D1", venue = "V1", status = "Upcoming"),
      com.example.data.local.Event(id = "st_2", name = "E2", date = "D2", venue = "V2", status = "In-Progress"),
      com.example.data.local.Event(id = "st_3", name = "E3", date = "D3", venue = "V3", status = "Completed"),
      com.example.data.local.Event(id = "st_4", name = "E4", date = "D4", venue = "V4", status = "Upcoming")
    )
    repository.insertEvents(events)

    val stored = repository.getEventById("st_1")
    assertNotNull(stored)

    val upcoming = events.count { it.status.equals("Upcoming", ignoreCase = true) }
    val inProgress = events.count { it.status.equals("In-Progress", ignoreCase = true) }
    val completed = events.count { it.status.equals("Completed", ignoreCase = true) }

    assertEquals(2, upcoming)
    assertEquals(1, inProgress)
    assertEquals(1, completed)
    assertEquals(4, events.size)

    db.close()
  }

  @Test
  fun `test event csv exporter produces valid formatted csv and file`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val events = listOf(
      com.example.data.local.Event(
        id = "csv_evt_001",
        name = "Bengaluru Tech Summit, 2026",
        date = "15 Nov 2026",
        timeSlot = "09:00 - 18:00",
        venue = "Palace Grounds, Main Gate",
        areaZone = "Sadashivanagar",
        status = "CONFIRMED",
        clientName = "Govt of Karnataka",
        description = "Annual flagship tech showcase; VIP protocols."
      ),
      com.example.data.local.Event(
        id = "csv_evt_002",
        name = "Indie Music Night",
        date = "18 Nov 2026",
        timeSlot = "18:00 - 23:00",
        venue = "UB City Amphitheater",
        areaZone = "CBD",
        status = "UPCOMING",
        clientName = "LiveNation India",
        description = "Sound check at 15:00."
      )
    )

    val csvString = com.example.util.EventCsvExporter.generateCsvString(events)

    // Assert CSV header
    assertTrue(csvString.contains("Event ID,Event Name,Date,Time Slot,Venue,Area Zone,Status,Client / Organizer,Description,Created Timestamp"))

    // Assert escaped commas in title & venue
    assertTrue(csvString.contains("\"Bengaluru Tech Summit, 2026\""))
    assertTrue(csvString.contains("\"Palace Grounds, Main Gate\""))

    // Test writing to cache file
    val file = com.example.util.EventCsvExporter.writeCsvToCache(context, events)
    assertTrue(file.exists())
    assertTrue(file.length() > 0)
    assertTrue(file.name.endsWith(".csv"))
  }
}
