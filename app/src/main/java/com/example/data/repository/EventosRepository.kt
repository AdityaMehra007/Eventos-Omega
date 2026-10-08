package com.example.data.repository

import android.util.Log
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

  private var opportunitiesListener: ListenerRegistration? = null
  private var reviewsListener: ListenerRegistration? = null
  private var bookingsListener: ListenerRegistration? = null

  private val _isFirestoreLive = MutableStateFlow(true)
  val isFirestoreLive: StateFlow<Boolean> = _isFirestoreLive.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

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

  // Base Default Opportunities for Seeding & Fallback
  private val defaultOpportunities = listOf(
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
      urgencyTag = "Immediate Confirmation",
      category = "Music",
      description = "Lead crowd control and safety perimeter supervision for main outdoor music stage. Manage barricades and radio team."
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
      urgencyTag = "Fast Fill",
      category = "Music",
      description = "Promote artisan food brands and drive beverage sampling interactions with festival attendees across arena stalls."
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
      urgencyTag = "LIVE Replacement",
      category = "Corporate",
      description = "Escort enterprise leaders and international keynote speakers from green rooms to main auditorium stage on schedule."
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
      urgencyTag = "Weekend Gig",
      category = "Technical Support",
      description = "Oversee delivery dispatch, manage promotional giveaways stock, and maintain accurate inventory counts at high-traffic mall booth."
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
      urgencyTag = "Premium Production",
      category = "Weddings",
      description = "Capture cinematic 4K video highlights, celebrity guest arrivals, and high-fashion luxury watch exhibition reels."
    ),
    StaffOpportunity(
      id = "opp_006",
      eventId = "evt_001",
      eventTitle = "Bengaluru Tech Summit 2026",
      role = "Stage Sound & AV Console Engineer",
      areaZone = "Central Bengaluru",
      date = "Today",
      timeWindow = "07:30 - 19:30",
      payRateINR = 4800,
      grossAmountINR = 4800,
      netAmountINR = 4560,
      platformFeeINR = 240,
      spotsAvailable = 3,
      spotsFilled = 1,
      skillsRequired = listOf("DiGiCo SD12", "Wireless Mic Frequency Scan", "DSP Tuning"),
      dressCode = "Black Crew Polo",
      matchScore = 97,
      matchReasons = listOf("Acoustic certification verified", "Palace Grounds veteran"),
      clientName = "Omega Eventworks Ltd",
      urgencyTag = "Immediate",
      category = "Technical Support",
      description = "Operate digital mixing consoles, run RF wireless microphone scans, and ensure zero feedback during executive keynotes."
    ),
    StaffOpportunity(
      id = "opp_007",
      eventId = "evt_004",
      eventTitle = "Royal Leela Palace Luxury Wedding",
      role = "VIP Hospitality & Shadow Host",
      areaZone = "Central Bengaluru",
      date = "19 Oct 2026",
      timeWindow = "16:00 - 23:30",
      payRateINR = 4200,
      grossAmountINR = 4200,
      netAmountINR = 3990,
      platformFeeINR = 210,
      spotsAvailable = 6,
      spotsFilled = 2,
      skillsRequired = listOf("Luxury Protocol", "Multi-lingual", "Silver Service Etiquette"),
      dressCode = "Traditional Indian Formal / Bandhgala",
      matchScore = 93,
      matchReasons = listOf("5-star hotel training record", "Top client rating"),
      clientName = "Aditya Mehra Signature Events",
      urgencyTag = "Luxury Booking",
      category = "Weddings",
      description = "Provide discreet personal hosting, guest reception, and bespoke banquet coordination for royal destination wedding celebration."
    ),
    StaffOpportunity(
      id = "opp_008",
      eventId = "evt_001",
      eventTitle = "Bangalore Enterprise AI Keynote",
      role = "Lead Executive Stage Runner",
      areaZone = "Whitefield",
      date = "24 Oct 2026",
      timeWindow = "08:30 - 17:30",
      payRateINR = 3400,
      grossAmountINR = 3400,
      netAmountINR = 3230,
      platformFeeINR = 170,
      spotsAvailable = 4,
      spotsFilled = 1,
      skillsRequired = listOf("Presenter Cue Management", "Teleprompter Check", "Confidentiality"),
      dressCode = "Navy Blue Formal Suit",
      matchScore = 92,
      matchReasons = listOf("Enterprise clearance verified", "Near Whitefield"),
      clientName = "Google Cloud Community Bangalore",
      urgencyTag = "Fast Fill",
      category = "Corporate",
      description = "Manage backstage speaker timing cues, teleprompter status checks, and slide clicker technical readiness."
    )
  )

  // Opportunities StateFlow
  private val _opportunities = MutableStateFlow(defaultOpportunities)
  val opportunities: StateFlow<List<StaffOpportunity>> = _opportunities.asStateFlow()

  // Bookings StateFlow (Stored in Firestore 'bookings' collection)
  private val defaultBookings = listOf(
    StaffBooking(
      id = "bkg_001",
      bookingReference = "EVT-BKG-8821",
      opportunityId = "opp_001",
      eventId = "evt_001",
      eventTitle = "Bengaluru Tech Summit 2026",
      role = "Lead Stage & Crowd Control Supervisor",
      workerId = "usr_blr_001",
      workerName = "Aditya Mehra",
      clientName = "Karnataka IT & BT Secretariat",
      areaZone = "Whitefield (KTPO)",
      date = "15-17 Oct 2026",
      timeWindow = "08:00 - 18:00",
      payRateINR = 3500,
      netAmountINR = 3325,
      status = "ACTIVE_CONFIRMED",
      escrowStatus = "ESCROW_LOCKED_100%",
      timestamp = System.currentTimeMillis() - 86400000L
    ),
    StaffBooking(
      id = "bkg_002",
      bookingReference = "EVT-BKG-4412",
      opportunityId = "opp_002",
      eventId = "evt_002",
      eventTitle = "Indiranagar Music & Food Carnival",
      role = "Experiential Operations Director",
      workerId = "usr_blr_001",
      workerName = "Aditya Mehra",
      clientName = "Bengaluru Cultural Collective",
      areaZone = "Indiranagar 100ft Rd",
      date = "28 Sep 2026",
      timeWindow = "14:00 - 23:00",
      payRateINR = 2800,
      netAmountINR = 2660,
      status = "COMPLETED",
      escrowStatus = "PAYOUT_DISBURSED",
      timestamp = System.currentTimeMillis() - (86400000L * 10)
    ),
    StaffBooking(
      id = "bkg_003",
      bookingReference = "EVT-BKG-1903",
      opportunityId = "opp_003",
      eventId = "evt_003",
      eventTitle = "Zepto Corporate Annual Leadership Meet",
      role = "Audio & 4K LED Wall Engineer",
      workerId = "usr_blr_001",
      workerName = "Aditya Mehra",
      clientName = "Zepto India Leadership Team",
      areaZone = "Koramangala 4th Block",
      date = "20 Sep 2026",
      timeWindow = "09:00 - 19:00",
      payRateINR = 3200,
      netAmountINR = 3040,
      status = "COMPLETED",
      escrowStatus = "PAYOUT_DISBURSED",
      timestamp = System.currentTimeMillis() - (86400000L * 18)
    )
  )
  private val _bookings = MutableStateFlow<List<StaffBooking>>(defaultBookings)
  val bookings: StateFlow<List<StaffBooking>> = _bookings.asStateFlow()

  // Reviews StateFlow (Stored in Firestore 'reviews' collection)
  private val defaultReviews = listOf(
    GigReview(
      id = "rev_001",
      gigId = "opp_001",
      eventTitle = "Bengaluru Tech Summit 2026",
      professionalId = "usr_blr_001",
      professionalName = "Aditya Mehra",
      organizerId = "org_blr_karnataka_it",
      organizerName = "Kiran Mazumdar / Dept of Electronics & IT",
      organizerOrganization = "Karnataka IT & BT Secretariat",
      roleExecuted = "Lead Stage & Crowd Control Supervisor",
      rating = 5.0f,
      reviewText = "Exceptional leadership under high operational pressure at KTPO! Managed the main keynote stage access control for 4,500 delegates without a single security breach or schedule delay. Rigging and sound sync was flawless.",
      punctualScore = 5.0f,
      technicalCompetenceScore = 5.0f,
      teamworkScore = 5.0f,
      formattedDate = "05 Oct 2026",
      verifiedGigBadge = true
    ),
    GigReview(
      id = "rev_002",
      gigId = "opp_002",
      eventTitle = "Indiranagar Music & Food Carnival",
      professionalId = "usr_blr_001",
      professionalName = "Aditya Mehra",
      organizerId = "org_blr_indiranagar_cul",
      organizerName = "Vikram Shenoy",
      organizerOrganization = "Bengaluru Cultural Collective",
      roleExecuted = "Experiential Operations Director",
      rating = 4.9f,
      reviewText = "Delivered outstanding crowd ingress and sound calibration for 25+ food vendors and the live acoustic stage. Highly communicative and solved power switchovers with zero audio dropouts.",
      punctualScore = 5.0f,
      technicalCompetenceScore = 4.9f,
      teamworkScore = 4.8f,
      formattedDate = "28 Sep 2026",
      verifiedGigBadge = true
    )
  )
  private val _reviews = MutableStateFlow<List<GigReview>>(defaultReviews)
  val reviews: StateFlow<List<GigReview>> = _reviews.asStateFlow()

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

  init {
    initFirestoreRealtimeSync()
  }

  /**
   * Subscribes to real-time Firestore snapshots on the "opportunities" collection.
   * If Firestore is empty on the cloud, seeds default opportunities into Firestore.
   */
  fun initFirestoreRealtimeSync() {
    val db = firestore ?: return
    _isSyncing.value = true

    try {
      opportunitiesListener?.remove()
      opportunitiesListener = db.collection("opportunities")
        .addSnapshotListener { snapshot, error ->
          _isSyncing.value = false
          if (error != null) {
            Log.w("EventosRepository", "Firestore snapshot listener error: ${error.message}")
            _isFirestoreLive.value = false
            return@addSnapshotListener
          }

          _isFirestoreLive.value = true
          if (snapshot != null && !snapshot.isEmpty) {
            val retrievedOpportunities = snapshot.documents.mapNotNull { doc ->
              try {
                StaffOpportunity(
                  id = doc.id,
                  eventId = doc.getString("eventId") ?: "evt_001",
                  eventTitle = doc.getString("eventTitle") ?: "Bangalore Event",
                  role = doc.getString("role") ?: "Event Talent",
                  areaZone = doc.getString("areaZone") ?: "Central BLR",
                  date = doc.getString("date") ?: "Upcoming",
                  timeWindow = doc.getString("timeWindow") ?: "09:00 - 18:00",
                  payRateINR = (doc.getLong("payRateINR") ?: 2500L).toInt(),
                  grossAmountINR = (doc.getLong("grossAmountINR") ?: 2500L).toInt(),
                  netAmountINR = (doc.getLong("netAmountINR") ?: 2375L).toInt(),
                  platformFeeINR = (doc.getLong("platformFeeINR") ?: 125L).toInt(),
                  spotsAvailable = (doc.getLong("spotsAvailable") ?: 5L).toInt(),
                  spotsFilled = (doc.getLong("spotsFilled") ?: 0L).toInt(),
                  skillsRequired = (doc.get("skillsRequired") as? List<*>)?.map { it.toString() } ?: listOf("Teamwork"),
                  dressCode = doc.getString("dressCode") ?: "Formal",
                  matchScore = (doc.getLong("matchScore") ?: 90L).toInt(),
                  matchReasons = (doc.get("matchReasons") as? List<*>)?.map { it.toString() } ?: listOf("Verified Talent"),
                  isApplied = doc.getBoolean("isApplied") ?: false,
                  clientName = doc.getString("clientName") ?: "Client Partner",
                  urgencyTag = doc.getString("urgencyTag") ?: "Active Gig",
                  category = doc.getString("category") ?: "Corporate",
                  description = doc.getString("description") ?: ""
                )
              } catch (e: Exception) {
                Log.w("EventosRepository", "Error parsing opportunity doc ${doc.id}", e)
                null
              }
            }
            if (retrievedOpportunities.isNotEmpty()) {
              _opportunities.value = retrievedOpportunities
            }
          } else {
            // Seed default opportunities to Firestore so cloud database is populated
            seedDefaultOpportunitiesToFirestore(db)
          }
        }

      // Reviews listener on Firestore 'reviews' collection
      reviewsListener?.remove()
      reviewsListener = db.collection("reviews")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("EventosRepository", "Firestore reviews snapshot listener error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null && !snapshot.isEmpty) {
            val retrievedReviews = snapshot.documents.mapNotNull { doc ->
              try {
                GigReview(
                  id = doc.id,
                  gigId = doc.getString("gigId") ?: "opp_001",
                  eventTitle = doc.getString("eventTitle") ?: "Event",
                  professionalId = doc.getString("professionalId") ?: "usr_blr_001",
                  professionalName = doc.getString("professionalName") ?: "Professional",
                  organizerId = doc.getString("organizerId") ?: "org_001",
                  organizerName = doc.getString("organizerName") ?: "Organizer",
                  organizerOrganization = doc.getString("organizerOrganization") ?: "Event Organizers",
                  roleExecuted = doc.getString("roleExecuted") ?: "Event Talent",
                  rating = (doc.getDouble("rating") ?: 5.0).toFloat(),
                  reviewText = doc.getString("reviewText") ?: "",
                  punctualScore = (doc.getDouble("punctualScore") ?: 5.0).toFloat(),
                  technicalCompetenceScore = (doc.getDouble("technicalCompetenceScore") ?: 5.0).toFloat(),
                  teamworkScore = (doc.getDouble("teamworkScore") ?: 5.0).toFloat(),
                  timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                  formattedDate = doc.getString("formattedDate") ?: "Oct 2026",
                  verifiedGigBadge = doc.getBoolean("verifiedGigBadge") ?: true
                )
              } catch (e: Exception) {
                Log.w("EventosRepository", "Error parsing review doc ${doc.id}", e)
                null
              }
            }
            if (retrievedReviews.isNotEmpty()) {
              _reviews.value = retrievedReviews
            }
          } else {
            seedDefaultReviewsToFirestore(db)
          }
        }

      // Bookings listener on Firestore 'bookings' collection
      bookingsListener?.remove()
      bookingsListener = db.collection("bookings")
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("EventosRepository", "Firestore bookings snapshot listener error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null && !snapshot.isEmpty) {
            val retrievedBookings = snapshot.documents.mapNotNull { doc ->
              try {
                StaffBooking(
                  id = doc.id,
                  bookingReference = doc.getString("bookingReference") ?: "EVT-BKG-0000",
                  opportunityId = doc.getString("opportunityId") ?: "",
                  eventId = doc.getString("eventId") ?: "evt_001",
                  eventTitle = doc.getString("eventTitle") ?: "Event Gig",
                  role = doc.getString("role") ?: "Staff Role",
                  workerId = doc.getString("workerId") ?: "usr_blr_001",
                  workerName = doc.getString("workerName") ?: "Aditya Mehra",
                  clientName = doc.getString("clientName") ?: "Client Partner",
                  areaZone = doc.getString("areaZone") ?: "Central BLR",
                  date = doc.getString("date") ?: "Upcoming",
                  timeWindow = doc.getString("timeWindow") ?: "09:00 - 18:00",
                  payRateINR = (doc.getLong("payRateINR") ?: 2500L).toInt(),
                  netAmountINR = (doc.getLong("netAmountINR") ?: 2375L).toInt(),
                  status = doc.getString("status") ?: "ACTIVE_CONFIRMED",
                  escrowStatus = doc.getString("escrowStatus") ?: "ESCROW_SECURED",
                  timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                )
              } catch (e: Exception) {
                Log.w("EventosRepository", "Error parsing booking doc ${doc.id}", e)
                null
              }
            }
            if (retrievedBookings.isNotEmpty()) {
              _bookings.value = retrievedBookings
            }
          } else {
            seedDefaultBookingsToFirestore(db)
          }
        }
    } catch (e: Exception) {
      Log.w("EventosRepository", "Exception setting up Firestore listener: ${e.message}")
      _isSyncing.value = false
      _isFirestoreLive.value = false
    }
  }

  private fun seedDefaultOpportunitiesToFirestore(db: FirebaseFirestore) {
    try {
      for (opp in defaultOpportunities) {
        val map = mapOf(
          "eventId" to opp.eventId,
          "eventTitle" to opp.eventTitle,
          "role" to opp.role,
          "areaZone" to opp.areaZone,
          "date" to opp.date,
          "timeWindow" to opp.timeWindow,
          "payRateINR" to opp.payRateINR,
          "grossAmountINR" to opp.grossAmountINR,
          "netAmountINR" to opp.netAmountINR,
          "platformFeeINR" to opp.platformFeeINR,
          "spotsAvailable" to opp.spotsAvailable,
          "spotsFilled" to opp.spotsFilled,
          "skillsRequired" to opp.skillsRequired,
          "dressCode" to opp.dressCode,
          "matchScore" to opp.matchScore,
          "matchReasons" to opp.matchReasons,
          "isApplied" to opp.isApplied,
          "clientName" to opp.clientName,
          "urgencyTag" to opp.urgencyTag,
          "category" to opp.category,
          "description" to opp.description
        )
        db.collection("opportunities").document(opp.id).set(map)
      }
      Log.d("EventosRepository", "Successfully seeded default opportunities into Firestore")
    } catch (e: Exception) {
      Log.w("EventosRepository", "Failed seeding opportunities to Firestore: ${e.message}")
    }
  }

  private fun seedDefaultReviewsToFirestore(db: FirebaseFirestore) {
    try {
      for (rev in defaultReviews) {
        val map = mapOf(
          "gigId" to rev.gigId,
          "eventTitle" to rev.eventTitle,
          "professionalId" to rev.professionalId,
          "professionalName" to rev.professionalName,
          "organizerId" to rev.organizerId,
          "organizerName" to rev.organizerName,
          "organizerOrganization" to rev.organizerOrganization,
          "roleExecuted" to rev.roleExecuted,
          "rating" to rev.rating,
          "reviewText" to rev.reviewText,
          "punctualScore" to rev.punctualScore,
          "technicalCompetenceScore" to rev.technicalCompetenceScore,
          "teamworkScore" to rev.teamworkScore,
          "timestamp" to rev.timestamp,
          "formattedDate" to rev.formattedDate,
          "verifiedGigBadge" to rev.verifiedGigBadge
        )
        db.collection("reviews").document(rev.id).set(map)
      }
      Log.d("EventosRepository", "Successfully seeded default reviews into Firestore 'reviews' collection")
    } catch (e: Exception) {
      Log.w("EventosRepository", "Failed seeding reviews to Firestore: ${e.message}")
    }
  }

  private fun seedDefaultBookingsToFirestore(db: FirebaseFirestore) {
    try {
      for (bkg in defaultBookings) {
        val map = mapOf(
          "bookingReference" to bkg.bookingReference,
          "opportunityId" to bkg.opportunityId,
          "eventId" to bkg.eventId,
          "eventTitle" to bkg.eventTitle,
          "role" to bkg.role,
          "workerId" to bkg.workerId,
          "workerName" to bkg.workerName,
          "clientName" to bkg.clientName,
          "areaZone" to bkg.areaZone,
          "date" to bkg.date,
          "timeWindow" to bkg.timeWindow,
          "payRateINR" to bkg.payRateINR,
          "netAmountINR" to bkg.netAmountINR,
          "status" to bkg.status,
          "escrowStatus" to bkg.escrowStatus,
          "timestamp" to bkg.timestamp
        )
        db.collection("bookings").document(bkg.id).set(map)
      }
      Log.d("EventosRepository", "Successfully seeded default bookings into Firestore 'bookings' collection")
    } catch (e: Exception) {
      Log.w("EventosRepository", "Failed seeding bookings to Firestore: ${e.message}")
    }
  }

  /**
   * Adds a new opportunity document directly to Firestore.
   */
  fun postNewGigToFirestore(opportunity: StaffOpportunity) {
    _opportunities.update { listOf(opportunity) + it }
    try {
      val map = mapOf(
        "eventId" to opportunity.eventId,
        "eventTitle" to opportunity.eventTitle,
        "role" to opportunity.role,
        "areaZone" to opportunity.areaZone,
        "date" to opportunity.date,
        "timeWindow" to opportunity.timeWindow,
        "payRateINR" to opportunity.payRateINR,
        "grossAmountINR" to opportunity.grossAmountINR,
        "netAmountINR" to opportunity.netAmountINR,
        "platformFeeINR" to opportunity.platformFeeINR,
        "spotsAvailable" to opportunity.spotsAvailable,
        "spotsFilled" to opportunity.spotsFilled,
        "skillsRequired" to opportunity.skillsRequired,
        "dressCode" to opportunity.dressCode,
        "matchScore" to opportunity.matchScore,
        "matchReasons" to opportunity.matchReasons,
        "isApplied" to opportunity.isApplied,
        "clientName" to opportunity.clientName,
        "urgencyTag" to opportunity.urgencyTag,
        "category" to opportunity.category,
        "description" to opportunity.description
      )
      firestore?.collection("opportunities")?.document(opportunity.id)?.set(map)
        ?.addOnSuccessListener {
          Log.d("EventosRepository", "New gig persisted in Firestore: ${opportunity.id}")
        }
    } catch (e: Exception) {
      Log.w("EventosRepository", "Failed to write gig to Firestore: ${e.message}")
    }
  }

  fun refreshOpportunitiesFromFirestore() {
    initFirestoreRealtimeSync()
  }

  fun applyForOpportunity(oppId: String): Boolean {
    _opportunities.update { list ->
      list.map { opp ->
        if (opp.id == oppId) opp.copy(isApplied = true, spotsFilled = opp.spotsFilled + 1) else opp
      }
    }
    syncToFirestore("applications", "app_${System.currentTimeMillis()}", mapOf(
      "opportunityId" to oppId,
      "workerId" to _currentUser.value.id,
      "workerName" to _currentUser.value.name,
      "status" to "APPLIED",
      "timestamp" to System.currentTimeMillis()
    ))
    firestore?.collection("opportunities")?.document(oppId)?.update("isApplied", true)
    return true
  }

  /**
   * Books a gig and records the booking request in the Firestore 'bookings' collection.
   */
  fun bookGig(opportunity: StaffOpportunity): StaffBooking {
    val bkgId = "bkg_${System.currentTimeMillis()}"
    val randomSuffix = (1000..9999).random()
    val bookingRef = "EVT-BKG-$randomSuffix"

    val newBooking = StaffBooking(
      id = bkgId,
      bookingReference = bookingRef,
      opportunityId = opportunity.id,
      eventId = opportunity.eventId,
      eventTitle = opportunity.eventTitle,
      role = opportunity.role,
      workerId = _currentUser.value.id,
      workerName = _currentUser.value.name,
      clientName = opportunity.clientName,
      areaZone = opportunity.areaZone,
      date = opportunity.date,
      timeWindow = opportunity.timeWindow,
      payRateINR = opportunity.payRateINR,
      netAmountINR = opportunity.netAmountINR,
      status = "CONFIRMED_BOOKED",
      escrowStatus = "ESCROW_SECURED",
      timestamp = System.currentTimeMillis()
    )

    _bookings.update { listOf(newBooking) + it }

    // Update opportunity state to booked
    _opportunities.update { list ->
      list.map { opp ->
        if (opp.id == opportunity.id) {
          opp.copy(
            isBooked = true,
            isApplied = true,
            spotsFilled = (opp.spotsFilled + 1).coerceAtMost(opp.spotsAvailable)
          )
        } else opp
      }
    }

    // Persist to new 'bookings' collection in Firestore
    val bookingData = mapOf(
      "id" to newBooking.id,
      "bookingReference" to newBooking.bookingReference,
      "opportunityId" to newBooking.opportunityId,
      "eventId" to newBooking.eventId,
      "eventTitle" to newBooking.eventTitle,
      "role" to newBooking.role,
      "workerId" to newBooking.workerId,
      "workerName" to newBooking.workerName,
      "clientName" to newBooking.clientName,
      "areaZone" to newBooking.areaZone,
      "date" to newBooking.date,
      "timeWindow" to newBooking.timeWindow,
      "payRateINR" to newBooking.payRateINR,
      "netAmountINR" to newBooking.netAmountINR,
      "status" to newBooking.status,
      "escrowStatus" to newBooking.escrowStatus,
      "timestamp" to newBooking.timestamp
    )
    syncToFirestore("bookings", newBooking.id, bookingData)

    return newBooking
  }

  /**
   * Submits a rating and review from an event organizer for a professional after a gig is completed,
   * permanently saving to Firestore 'reviews' collection and updating the professional's rating and review count.
   */
  fun submitReview(
    gigId: String,
    eventTitle: String,
    professionalId: String,
    professionalName: String,
    organizerId: String,
    organizerName: String,
    organizerOrganization: String,
    roleExecuted: String,
    rating: Float,
    reviewText: String,
    punctualScore: Float = 5.0f,
    technicalCompetenceScore: Float = 5.0f,
    teamworkScore: Float = 5.0f
  ): GigReview {
    val reviewId = "rev_${System.currentTimeMillis()}"
    val newReview = GigReview(
      id = reviewId,
      gigId = gigId,
      eventTitle = eventTitle,
      professionalId = professionalId,
      professionalName = professionalName,
      organizerId = organizerId,
      organizerName = organizerName,
      organizerOrganization = organizerOrganization,
      roleExecuted = roleExecuted,
      rating = rating,
      reviewText = reviewText,
      punctualScore = punctualScore,
      technicalCompetenceScore = technicalCompetenceScore,
      teamworkScore = teamworkScore,
      timestamp = System.currentTimeMillis(),
      formattedDate = "Oct 2026",
      verifiedGigBadge = true
    )

    // Update in-memory state
    _reviews.update { listOf(newReview) + it }

    // If review is for current professional, recalculate rating and review count
    if (professionalId == _currentUser.value.id) {
      val allReviews = _reviews.value.filter { it.professionalId == professionalId }
      val avgRating = if (allReviews.isNotEmpty()) {
        allReviews.map { it.rating }.average().toFloat()
      } else rating

      val updatedUser = _currentUser.value.copy(
        rating = (Math.round(avgRating * 100) / 100.0).toFloat(),
        reviewCount = _currentUser.value.reviewCount + 1
      )
      _currentUser.value = updatedUser

      // Sync updated user rating to Firestore
      syncToFirestore("users", updatedUser.id, mapOf(
        "rating" to updatedUser.rating,
        "reviewCount" to updatedUser.reviewCount,
        "updatedAt" to System.currentTimeMillis()
      ))
    }

    // Persist to new 'reviews' collection in Firestore
    val reviewData = mapOf(
      "id" to newReview.id,
      "gigId" to newReview.gigId,
      "eventTitle" to newReview.eventTitle,
      "professionalId" to newReview.professionalId,
      "professionalName" to newReview.professionalName,
      "organizerId" to newReview.organizerId,
      "organizerName" to newReview.organizerName,
      "organizerOrganization" to newReview.organizerOrganization,
      "roleExecuted" to newReview.roleExecuted,
      "rating" to newReview.rating.toDouble(),
      "reviewText" to newReview.reviewText,
      "punctualScore" to newReview.punctualScore.toDouble(),
      "technicalCompetenceScore" to newReview.technicalCompetenceScore.toDouble(),
      "teamworkScore" to newReview.teamworkScore.toDouble(),
      "timestamp" to newReview.timestamp,
      "formattedDate" to newReview.formattedDate,
      "verifiedGigBadge" to newReview.verifiedGigBadge
    )
    syncToFirestore("reviews", newReview.id, reviewData)
    Log.d("EventosRepository", "Review submitted and synced to Firestore 'reviews' collection: ${newReview.id}")

    return newReview
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

  /**
   * Updates professional user profile (bio, skills, portfolio) and stores
   * it in the 'users' collection in Cloud Firestore.
   */
  fun updateUserProfile(
    bio: String,
    skills: List<String>,
    portfolio: List<PortfolioItem>,
    title: String = _currentUser.value.title,
    primaryZone: String = _currentUser.value.primaryZone,
    hourlyRateINR: Int = _currentUser.value.hourlyRateINR
  ): EventosUser {
    val updatedUser = _currentUser.value.copy(
      bio = bio,
      skills = skills,
      portfolio = portfolio,
      title = title,
      primaryZone = primaryZone,
      hourlyRateINR = hourlyRateINR
    )
    _currentUser.value = updatedUser

    // Save to 'users' collection in Cloud Firestore
    val portfolioMaps = portfolio.map { item ->
      mapOf(
        "id" to item.id,
        "title" to item.title,
        "category" to item.category,
        "eventName" to item.eventName,
        "year" to item.year,
        "role" to item.role,
        "description" to item.description,
        "mediaUrl" to item.mediaUrl,
        "metrics" to item.metrics
      )
    }

    val userData = mapOf(
      "id" to updatedUser.id,
      "eventosId" to updatedUser.eventosId,
      "name" to updatedUser.name,
      "email" to updatedUser.email,
      "phone" to updatedUser.phone,
      "role" to updatedUser.role.name,
      "title" to updatedUser.title,
      "bio" to updatedUser.bio,
      "city" to updatedUser.city,
      "primaryZone" to updatedUser.primaryZone,
      "serviceRadiusKm" to updatedUser.serviceRadiusKm,
      "hourlyRateINR" to updatedUser.hourlyRateINR,
      "rating" to updatedUser.rating,
      "reviewCount" to updatedUser.reviewCount,
      "verifiedHours" to updatedUser.verifiedHours,
      "completedEvents" to updatedUser.completedEvents,
      "isVerified" to updatedUser.isVerified,
      "skills" to updatedUser.skills,
      "badges" to updatedUser.badges,
      "portfolio" to portfolioMaps,
      "avatarUrl" to updatedUser.avatarUrl,
      "updatedAt" to System.currentTimeMillis()
    )

    syncToFirestore("users", updatedUser.id, userData)
    Log.d("EventosRepository", "User profile permanently saved to 'users' collection in Firestore")
    return updatedUser
  }

  fun addPortfolioItem(item: PortfolioItem) {
    val currentPortfolio = _currentUser.value.portfolio
    updateUserProfile(
      bio = _currentUser.value.bio,
      skills = _currentUser.value.skills,
      portfolio = listOf(item) + currentPortfolio
    )
  }

  fun removePortfolioItem(itemId: String) {
    val updatedPortfolio = _currentUser.value.portfolio.filter { it.id != itemId }
    updateUserProfile(
      bio = _currentUser.value.bio,
      skills = _currentUser.value.skills,
      portfolio = updatedPortfolio
    )
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
