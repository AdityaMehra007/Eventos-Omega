package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EventosRepository {
  private val firestore by lazy {
    try {
      FirebaseFirestore.getInstance()
    } catch (e: Exception) {
      Log.w("EventosRepository", "Firestore init fallback: ${e.message}")
      null
    }
  }

  // Active User State
  private val _currentUser = MutableStateFlow(
    EventosUser(
      id = "usr_blr_001",
      eventosId = "EVT-BLR-P-000001",
      name = "Aditya Mehra",
      email = "aditya.mehra@eventos.in",
      phone = "+91 98860 12345",
      role = UserRole.MANAGER,
      title = "Senior Technical Event Producer",
      city = "Bengaluru",
      primaryZone = "Koramangala",
      hourlyRateINR = 2200,
      rating = 4.95f,
      reviewCount = 52,
      verifiedHours = 640,
      completedEvents = 42,
      skills = listOf("Stage Management", "AV Production", "Crew Deployment", "Incident Command", "Vendor Auditing", "Live Streaming"),
      badges = listOf("Verified Supervisor", "Production Specialist", "Bangalore Top 5% 2026", "Gold Trust Shield")
    )
  )
  val currentUser: StateFlow<EventosUser> = _currentUser.asStateFlow()

  // Passport stamps
  private val _passportStamps = MutableStateFlow(
    listOf(
      PassportStamp(
        eventId = "evt_001",
        eventName = "Bengaluru Tech Summit 2026",
        role = "Lead Stage Supervisor",
        date = "15 Oct 2026",
        hoursLogged = 14,
        location = "Palace Grounds, Bengaluru",
        verifiedBy = "Karnataka Innovation Authority",
        rating = 5.0f,
        proofHash = "0x8F3C92...A14B",
        clientEndorsement = "Exceptional crowd coordination and zero delay in keynote transitions."
      ),
      PassportStamp(
        eventId = "evt_002",
        eventName = "Sunburn Arena Bangalore",
        role = "AV Production Marshal",
        date = "22 Sep 2026",
        hoursLogged = 16,
        location = "Manpho Convention, Hebbal",
        verifiedBy = "Percept Live Events",
        rating = 4.9f,
        proofHash = "0x1A4D7E...88C2",
        clientEndorsement = "Flawless backstage power distribution and artist crew management."
      ),
      PassportStamp(
        eventId = "evt_003",
        eventName = "Google Cloud AI Day Bangalore",
        role = "Operations Director",
        date = "05 Aug 2026",
        hoursLogged = 12,
        location = "KTPO Whitefield, Bengaluru",
        verifiedBy = "Google Developer Groups",
        rating = 5.0f,
        proofHash = "0x5E8B32...D9F1",
        clientEndorsement = "Handled 2,500 delegates with spotless registration and live feed telemetry."
      )
    )
  )
  val passportStamps: StateFlow<List<PassportStamp>> = _passportStamps.asStateFlow()

  // Events
  private val _events = MutableStateFlow(
    listOf(
      EventRecord(
        id = "evt_001",
        eventosId = "EVT-BLR-E-000101",
        title = "Bengaluru Tech Summit 2026",
        type = EventType.TECH_CONFERENCE,
        status = EventStatus.LIVE,
        clientName = "Karnataka Digital Economy Mission",
        organizerName = "Omega Eventworks Ltd",
        venueName = "Palace Grounds (Tripuravasini)",
        areaZone = "Central Bengaluru",
        date = "Today, Oct 8",
        timeSlot = "08:00 - 20:00",
        attendeesExpected = 4500,
        attendeesCheckedIn = 3840,
        totalStaffNeeded = 95,
        staffConfirmed = 95,
        staffCheckedIn = 92,
        budgetEstimatedINR = 4500000,
        budgetSpentINR = 3820000,
        coverImageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&auto=format&fit=crop&q=80",
        description = "Premier technology confluence bringing together AI founders, enterprise leaders and international delegations in Bangalore.",
        criticalAlerts = listOf("Rain forecast for 18:00 - Waterproof canopy check", "Gate 3 attendee surge")
      ),
      EventRecord(
        id = "evt_002",
        eventosId = "EVT-BLR-E-000102",
        title = "Indiranagar Music & Food Carnival",
        type = EventType.MUSIC_CONCERT,
        status = EventStatus.STAFFING,
        clientName = "Bangalore Urban Culture",
        organizerName = "Apex Live Experiences",
        venueName = "Indiranagar Club Lawns",
        areaZone = "Indiranagar",
        date = "Sat, 11 Oct",
        timeSlot = "14:00 - 23:00",
        attendeesExpected = 2800,
        attendeesCheckedIn = 0,
        totalStaffNeeded = 45,
        staffConfirmed = 38,
        staffCheckedIn = 0,
        budgetEstimatedINR = 1800000,
        budgetSpentINR = 850000,
        coverImageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80",
        description = "Live indie music acts, food trucks, craft stalls and experiential brand activations.",
        criticalAlerts = listOf("Need 7 more verified brand promoters")
      ),
      EventRecord(
        id = "evt_003",
        eventosId = "EVT-BLR-E-000103",
        title = "Zepto Superfast Launch Activation",
        type = EventType.BRAND_ACTIVATION,
        status = EventStatus.CONFIRMED,
        clientName = "Zepto India",
        organizerName = "Velocity Activations",
        venueName = "Nexus Koramangala Mall",
        areaZone = "Koramangala",
        date = "Sun, 12 Oct",
        timeSlot = "11:00 - 21:00",
        attendeesExpected = 6000,
        attendeesCheckedIn = 0,
        totalStaffNeeded = 30,
        staffConfirmed = 30,
        staffCheckedIn = 0,
        budgetEstimatedINR = 750000,
        budgetSpentINR = 450000,
        coverImageUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&auto=format&fit=crop&q=80",
        description = "Interactive experiential sampling booths, VR gaming zones, and giveaway dispatch.",
        criticalAlerts = emptyList()
      ),
      EventRecord(
        id = "evt_004",
        eventosId = "EVT-BLR-E-000104",
        title = "Titan Edge Luxury Showcase",
        type = EventType.CORPORATE_SUMMIT,
        status = EventStatus.PLANNING,
        clientName = "Titan Company Ltd",
        organizerName = "Omega Eventworks Ltd",
        venueName = "The Leela Palace, HAL Airport Rd",
        areaZone = "Central Bengaluru",
        date = "18 Oct 2026",
        timeSlot = "18:00 - 23:30",
        attendeesExpected = 450,
        attendeesCheckedIn = 0,
        totalStaffNeeded = 22,
        staffConfirmed = 15,
        staffCheckedIn = 0,
        budgetEstimatedINR = 2600000,
        budgetSpentINR = 600000,
        coverImageUrl = "https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&auto=format&fit=crop&q=80",
        description = "Ultra-premium watch exhibition and high-net-worth VIP dinner experience.",
        criticalAlerts = emptyList()
      )
    )
  )
  val events: StateFlow<List<EventRecord>> = _events.asStateFlow()

  // Opportunities
  private val _opportunities = MutableStateFlow(
    listOf(
      StaffOpportunity(
        id = "opp_001",
        eventId = "evt_002",
        eventTitle = "Indiranagar Music & Food Carnival",
        role = "Senior Crowd Supervisor",
        areaZone = "Indiranagar",
        date = "11 Oct 2026",
        timeWindow = "13:30 - 23:00",
        payRateINR = 4500,
        grossAmountINR = 4500,
        netAmountINR = 4275,
        platformFeeINR = 225,
        spotsAvailable = 4,
        spotsFilled = 2,
        skillsRequired = listOf("Crowd Control", "Incident Command", "Walkie-Talkie Protocol"),
        dressCode = "All Black Formal / Eventos Armband",
        matchScore = 96,
        matchReasons = listOf("Verified Supervisor Badge", "5km from Koramangala base", "Previous concert experience"),
        clientName = "Bangalore Urban Culture",
        urgencyTag = "Immediate Confirmation"
      ),
      StaffOpportunity(
        id = "opp_002",
        eventId = "evt_002",
        eventTitle = "Indiranagar Music & Food Carnival",
        role = "Experiential Brand Promoter",
        areaZone = "Indiranagar",
        date = "11 Oct 2026",
        timeWindow = "14:00 - 22:30",
        payRateINR = 2200,
        grossAmountINR = 2200,
        netAmountINR = 2090,
        platformFeeINR = 110,
        spotsAvailable = 10,
        spotsFilled = 5,
        skillsRequired = listOf("Fluent English & Kannada", "Product Sampling", "High Energy"),
        dressCode = "Branded T-Shirt (Provided) + Dark Jeans",
        matchScore = 88,
        matchReasons = listOf("Matches your language preferences", "High punctuality rating"),
        clientName = "Bangalore Urban Culture",
        urgencyTag = "Fast Fill"
      ),
      StaffOpportunity(
        id = "opp_003",
        eventId = "evt_001",
        eventTitle = "Bengaluru Tech Summit 2026",
        role = "VIP Protocol & Speaker Liaison",
        areaZone = "Central Bengaluru",
        date = "Today",
        timeWindow = "08:00 - 18:00",
        payRateINR = 3800,
        grossAmountINR = 3800,
        netAmountINR = 3610,
        platformFeeINR = 190,
        spotsAvailable = 2,
        spotsFilled = 1,
        skillsRequired = listOf("Executive Hospitality", "Stage Flow", "Badge Accreditation"),
        dressCode = "Business Formal Blazer",
        matchScore = 94,
        matchReasons = listOf("Previous tech summit record", "Client 5-star endorsement"),
        clientName = "Karnataka Digital Economy Mission",
        urgencyTag = "LIVE Replacement"
      ),
      StaffOpportunity(
        id = "opp_004",
        eventId = "evt_003",
        eventTitle = "Zepto Superfast Launch Activation",
        role = "Lead Logistics & Inventory Marshal",
        areaZone = "Koramangala",
        date = "12 Oct 2026",
        timeWindow = "10:30 - 21:00",
        payRateINR = 3200,
        grossAmountINR = 3200,
        netAmountINR = 3040,
        platformFeeINR = 160,
        spotsAvailable = 5,
        spotsFilled = 3,
        skillsRequired = listOf("Stock Reconcilation", "Booth Security", "Quick Math"),
        dressCode = "Black Polo + Cargo Pants",
        matchScore = 91,
        matchReasons = listOf("5 mins from Koramangala", "Verified inventory skill"),
        clientName = "Zepto India",
        urgencyTag = "Weekend Gig"
      ),
      StaffOpportunity(
        id = "opp_005",
        eventId = "evt_004",
        eventTitle = "Titan Edge Luxury Showcase",
        role = "4K Drone & Gimbal Cinematographer",
        areaZone = "Central Bengaluru",
        date = "18 Oct 2026",
        timeWindow = "17:00 - 23:30",
        payRateINR = 8500,
        grossAmountINR = 8500,
        netAmountINR = 8075,
        platformFeeINR = 425,
        spotsAvailable = 2,
        spotsFilled = 1,
        skillsRequired = listOf("Sony FX3 / A7SIII", "DJI Ronin", "Color Grading"),
        dressCode = "Black Formal Suit",
        matchScore = 98,
        matchReasons = listOf("Cinema gear verified", "Luxury portfolio approved"),
        clientName = "Titan Company Ltd",
        urgencyTag = "Premium Production"
      )
    )
  )
  val opportunities: StateFlow<List<StaffOpportunity>> = _opportunities.asStateFlow()

  // Vendors
  private val _vendors = MutableStateFlow(
    listOf(
      VendorListing(
        id = "vnd_001",
        businessName = "Bangalore Sound & Light Pros",
        category = VendorCategory.AV_SOUND,
        areaZone = "Koramangala",
        serviceRadiusKm = 30,
        rating = 4.9f,
        completedProjects = 180,
        verificationBadge = "GST & ISO Verified",
        baseRateEstINR = "₹1,50,000 - ₹5,00,000",
        capacityDesc = "Supports arena concerts up to 15,000 people. L-Acoustics K2 & Yamaha Rivage DSP.",
        inventoryHighlights = listOf("32-channel Line Array", "Shure Axient Digital", "DiGiCo SD12 Console"),
        contactPerson = "Raghavan N. (+91 99000 88776)"
      ),
      VendorListing(
        id = "vnd_002",
        businessName = "Titan Stage & Truss Works",
        category = VendorCategory.STAGING,
        areaZone = "Whitefield",
        serviceRadiusKm = 40,
        rating = 4.8f,
        completedProjects = 135,
        verificationBadge = "Safety Certified Trussing",
        baseRateEstINR = "₹80,000 - ₹3,50,000",
        capacityDesc = "Heavy-duty aluminum box trussing, German pagoda tents, and hydraulic stage lifts.",
        inventoryHighlights = listOf("60ft x 40ft Main Stage", "Prolyte Truss Towers", "Flame-retardant Canopies"),
        contactPerson = "Suresh Kumar (+91 98450 33221)"
      ),
      VendorListing(
        id = "vnd_003",
        businessName = "Lumina Vision High-Res LED",
        category = VendorCategory.LIGHTING_TRUSS,
        areaZone = "Indiranagar",
        serviceRadiusKm = 25,
        rating = 4.95f,
        completedProjects = 210,
        verificationBadge = "Official Tech Partner",
        baseRateEstINR = "₹1,20,000 - ₹4,80,000",
        capacityDesc = "P2.6 and P3.9 indoor/outdoor curved LED display walls, NovaStar UHD processors.",
        inventoryHighlights = listOf("120 sqm P2.9 High Refresh Panels", "Barco S3-4K Switcher", "Robo-heads"),
        contactPerson = "Anjali Menon (+91 97410 55443)"
      ),
      VendorListing(
        id = "vnd_004",
        businessName = "ElectroSilent Gensets Bengaluru",
        category = VendorCategory.POWER_GEN,
        areaZone = "Electronic City",
        serviceRadiusKm = 50,
        rating = 4.85f,
        completedProjects = 320,
        verificationBadge = "CPCB-IV Emission Compliant",
        baseRateEstINR = "₹35,000 - ₹1,10,000 / day",
        capacityDesc = "Silent sound-proof generators from 62.5kVA to 250kVA with automatic sync & transfer.",
        inventoryHighlights = listOf("2x 125kVA Cummins Silent", "Synchronizing Panel", "100m Armored Cable"),
        contactPerson = "Dinesh Gowda (+91 99801 11223)"
      )
    )
  )
  val vendors: StateFlow<List<VendorListing>> = _vendors.asStateFlow()

  // Quotations for Event
  private val _quotations = MutableStateFlow(
    listOf(
      VendorQuotation(
        id = "q_001",
        rfqId = "rfq_blr_881",
        vendorId = "vnd_001",
        vendorName = "Bangalore Sound & Light Pros",
        category = VendorCategory.AV_SOUND,
        quoteAmountINR = 185000,
        scopeIncluded = listOf("Line Array 16 Tops + 8 Subs", "2 Sound Engineers", "Digital Console", "Transport to Palace Grounds"),
        scopeExcluded = listOf("18% GST (Extra)", "Generator Diesel Fuel", "Overtime past 23:00"),
        setupHoursRequired = 6,
        deliveryDate = "Oct 8, 06:00 AM",
        riskAssessment = "LOW RISK: Verified in 42 previous city summits.",
        status = "Selected"
      ),
      VendorQuotation(
        id = "q_002",
        rfqId = "rfq_blr_881",
        vendorId = "vnd_003",
        vendorName = "Lumina Vision High-Res LED",
        category = VendorCategory.LIGHTING_TRUSS,
        quoteAmountINR = 240000,
        scopeIncluded = listOf("14m x 4m Curved P2.9 LED Backdrop", "4K Video Processing", "Backup Display Controller", "All Rigging"),
        scopeExcluded = listOf("Power connection from venue", "Custom motion graphic design"),
        setupHoursRequired = 8,
        deliveryDate = "Oct 8, 05:00 AM",
        riskAssessment = "LOW RISK: Gold reliability score 99.4%.",
        status = "Pending"
      )
    )
  )
  val quotations: StateFlow<List<VendorQuotation>> = _quotations.asStateFlow()

  // Operational Incidents
  private val _incidents = MutableStateFlow(
    listOf(
      OperationalIncident(
        id = "inc_001",
        eventId = "evt_001",
        title = "Attendee Congestion at Gate 3 QR Scan",
        category = "Crowd Flow",
        severity = "MEDIUM",
        status = "Resolved",
        reportedAt = "10:15 AM",
        reportedBy = "Kavita Rao (Supervisor)",
        actionSummary = "Dispatched 2 backup handheld QR readers and routed tech attendees to Gate 4.",
        resolutionNotes = "Turnstile wait time dropped from 7 mins to under 45 seconds."
      ),
      OperationalIncident(
        id = "inc_002",
        eventId = "evt_001",
        title = "Auxiliary Sound Check Hum in VIP Lounge",
        category = "AV / Power",
        severity = "LOW",
        status = "Resolved",
        reportedAt = "08:45 AM",
        reportedBy = "Sanjay M (AV Lead)",
        actionSummary = "Replaced faulty XLR ground loop isolator on secondary amplifier feed.",
        resolutionNotes = "Acoustic baseline verified at 0dB noise floor."
      )
    )
  )
  val incidents: StateFlow<List<OperationalIncident>> = _incidents.asStateFlow()

  // Attendance Records
  private val _attendance = MutableStateFlow(
    listOf(
      AttendanceCheckIn("att_001", "evt_001", "usr_blr_001", "Aditya Mehra", "Site Supervisor", "07:30 AM", "Geofence Auto", "Checked In"),
      AttendanceCheckIn("att_002", "evt_001", "usr_blr_102", "Pooja Hegde", "VIP Host Lead", "07:45 AM", "QR Scanner", "Checked In"),
      AttendanceCheckIn("att_003", "evt_001", "usr_blr_103", "Rohan Mehta", "Stage Coordinator", "08:00 AM", "QR Scanner", "Checked In"),
      AttendanceCheckIn("att_004", "evt_001", "usr_blr_104", "Vikram Das", "Promoter Team A", "08:15 AM", "QR Scanner", "Checked In")
    )
  )
  val attendance: StateFlow<List<AttendanceCheckIn>> = _attendance.asStateFlow()

  // Task Items
  private val _tasks = MutableStateFlow(
    listOf(
      EventTaskItem("tsk_01", "evt_001", "Main Stage & Truss Sign-off", "Venue Ingress -> Stage Assembly", "Suresh (Titan Truss)", "07:00 AM", true, "Stage"),
      EventTaskItem("tsk_02", "evt_001", "Acoustic Rigging & Audio DSP Line Check", "Stage Assembly -> Audio Rigging", "Raghavan (Bangalore Sound)", "08:30 AM", true, "AV"),
      EventTaskItem("tsk_03", "evt_001", "LED Wall 4K Playback Sync & Test", "Audio Rigging -> Video Calibration", "Anjali (Lumina Vision)", "09:00 AM", true, "Visuals"),
      EventTaskItem("tsk_04", "evt_001", "Promoter Briefing & Armband Handout", "Staff Check-in -> Gate Prep", "Aditya Mehra", "09:30 AM", true, "Staffing"),
      EventTaskItem("tsk_05", "evt_001", "VIP Delegation Walkthrough & Red Carpet", "Doors Open -> Keynote", "Pooja Hegde", "11:00 AM", false, "VIP"),
      EventTaskItem("tsk_06", "evt_001", "Evening Concert Lighting Transition", "Day Sessions -> Stage Reset", "Lumina Vision Techs", "17:30 PM", false, "Lighting")
    )
  )
  val tasks: StateFlow<List<EventTaskItem>> = _tasks.asStateFlow()

  // Finance Summary
  private val _budgetSummary = MutableStateFlow(
    BudgetSummary(
      totalBudgetEstimatedINR = 4500000,
      totalBudgetApprovedINR = 4500000,
      totalCommittedINR = 3980000,
      totalSpentINR = 3820000,
      outstandingPayablesINR = 160000,
      contingencyBufferINR = 520000,
      categories = listOf(
        BudgetCategoryItem("Staging, Tents & Structure", 1100000, 1050000, 95),
        BudgetCategoryItem("AV, Acoustics & 4K LED Walls", 1450000, 1420000, 97),
        BudgetCategoryItem("Workforce & Supervisors (95 Crew)", 750000, 680000, 90),
        BudgetCategoryItem("Silent Generators & Fuel Buffer", 320000, 310000, 96),
        BudgetCategoryItem("VIP Catering, F&B & Hospitality", 580000, 540000, 93),
        BudgetCategoryItem("Emergency & Medical Buffer", 300000, 120000, 40)
      )
    )
  )
  val budgetSummary: StateFlow<BudgetSummary> = _budgetSummary.asStateFlow()

  // Worker payouts
  private val _payouts = MutableStateFlow(
    listOf(
      WorkerPayoutRecord("pay_01", "Kavita Rao", "Bengaluru Tech Summit 2026", 4500, 45, 4455, "DISBURSED_UPI", "UPI/6281099234@okaxis", "08 Oct 2026"),
      WorkerPayoutRecord("pay_02", "Pooja Hegde", "Bengaluru Tech Summit 2026", 3800, 38, 3762, "APPROVED", "UPI/pooja.hegde@ybl", "08 Oct 2026"),
      WorkerPayoutRecord("pay_03", "Rohan Mehta", "Bengaluru Tech Summit 2026", 3200, 32, 3168, "APPROVED", "UPI/rohanm@icici", "08 Oct 2026"),
      WorkerPayoutRecord("pay_04", "Vikram Das", "Bengaluru Tech Summit 2026", 2500, 25, 2475, "PENDING_APPROVAL", "UPI/vikram.das@paytm", "08 Oct 2026")
    )
  )
  val payouts: StateFlow<List<WorkerPayoutRecord>> = _payouts.asStateFlow()

  // Actions
  fun applyForOpportunity(oppId: String): Boolean {
    _opportunities.update { list ->
      list.map { opp ->
        if (opp.id == oppId) opp.copy(isApplied = true, spotsFilled = opp.spotsFilled + 1) else opp
      }
    }
    syncToFirestore("applications", oppId, mapOf("workerId" to _currentUser.value.id, "status" to "APPLIED", "timestamp" to System.currentTimeMillis()))
    return true
  }

  fun checkInWorker(workerName: String, method: String = "QR Scanner"): AttendanceCheckIn {
    val newRecord = AttendanceCheckIn(
      id = "att_${System.currentTimeMillis()}",
      eventId = "evt_001",
      workerId = "usr_${System.currentTimeMillis()}",
      workerName = workerName,
      role = "Verified Crew",
      timestamp = "Just now",
      method = method,
      status = "Checked In"
    )
    _attendance.update { listOf(newRecord) + it }
    // Update event checked-in count
    _events.update { list ->
      list.map { ev ->
        if (ev.id == "evt_001") ev.copy(staffCheckedIn = ev.staffCheckedIn + 1) else ev
      }
    }
    syncToFirestore("attendance", newRecord.id, mapOf("workerName" to workerName, "method" to method, "status" to "Checked In"))
    return newRecord
  }

  fun logIncident(title: String, category: String, severity: String, action: String): OperationalIncident {
    val newIncident = OperationalIncident(
      id = "inc_${System.currentTimeMillis()}",
      eventId = "evt_001",
      title = title,
      category = category,
      severity = severity,
      status = "Investigating",
      reportedAt = "Just now",
      reportedBy = _currentUser.value.name,
      actionSummary = action,
      resolutionNotes = "Active incident desk monitoring"
    )
    _incidents.update { listOf(newIncident) + it }
    syncToFirestore("incidents", newIncident.id, mapOf("title" to title, "category" to category, "severity" to severity))
    return newIncident
  }

  fun resolveIncident(incidentId: String, notes: String) {
    _incidents.update { list ->
      list.map { inc ->
        if (inc.id == incidentId) inc.copy(status = "Resolved", resolutionNotes = notes) else inc
      }
    }
    syncToFirestore("incidents", incidentId, mapOf("status" to "Resolved", "resolutionNotes" to notes))
  }

  fun toggleTask(taskId: String) {
    _tasks.update { list ->
      list.map { tsk ->
        if (tsk.id == taskId) tsk.copy(isCompleted = !tsk.isCompleted) else tsk
      }
    }
  }

  fun switchUserRole(role: UserRole) {
    _currentUser.update { it.copy(role = role) }
  }

  fun disbursePayout(payoutId: String) {
    _payouts.update { list ->
      list.map { p ->
        if (p.id == payoutId) p.copy(status = "DISBURSED_UPI") else p
      }
    }
  }

  private fun syncToFirestore(collection: String, docId: String, data: Map<String, Any>) {
    try {
      firestore?.collection(collection)?.document(docId)?.set(data)
        ?.addOnSuccessListener {
          Log.d("EventosRepository", "Synced $collection/$docId to Firestore")
        }
        ?.addOnFailureListener { e ->
          Log.w("EventosRepository", "Firestore write failed: ${e.message}")
        }
    } catch (e: Exception) {
      Log.w("EventosRepository", "Exception in syncToFirestore: ${e.message}")
    }
  }
}
