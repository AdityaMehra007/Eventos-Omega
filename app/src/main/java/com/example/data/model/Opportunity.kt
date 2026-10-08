package com.example.data.model

data class StaffOpportunity(
  val id: String,
  val eventId: String,
  val eventTitle: String,
  val role: String, // e.g. "Lead Stage Supervisor", "Senior Brand Promoter", "AV Audio Engineer", "VIP Hospitality Lead"
  val areaZone: String, // "Indiranagar", "Koramangala", "Whitefield", "HSR Layout", "Electronic City", "Hebbal", "Central BLR"
  val date: String,
  val timeWindow: String,
  val payRateINR: Int, // e.g. 2500 per day or per shift
  val grossAmountINR: Int,
  val netAmountINR: Int,
  val platformFeeINR: Int,
  val spotsAvailable: Int,
  val spotsFilled: Int,
  val skillsRequired: List<String>,
  val dressCode: String,
  val matchScore: Int, // e.g. 96
  val matchReasons: List<String>,
  val isApplied: Boolean = false,
  val isShortlisted: Boolean = false,
  val isBooked: Boolean = false,
  val clientName: String,
  val urgencyTag: String = "High Demand",
  val category: String = "Corporate",
  val description: String = ""
)

data class ProofOfWorkRecord(
  val id: String,
  val bookingId: String,
  val eventTitle: String,
  val role: String,
  val hoursWorked: Int,
  val proofPhotos: List<String>,
  val responsibilitiesExecuted: List<String>,
  val supervisorVerification: Boolean,
  val clientVerification: Boolean,
  val clientReview: String,
  val clientRating: Float,
  val txHash: String
)

data class StaffBooking(
  val id: String,
  val bookingReference: String, // e.g. "EVT-BKG-0042"
  val opportunityId: String,
  val eventId: String,
  val eventTitle: String,
  val role: String,
  val workerId: String,
  val workerName: String,
  val clientName: String,
  val areaZone: String,
  val date: String,
  val timeWindow: String,
  val payRateINR: Int,
  val netAmountINR: Int,
  val status: String = "CONFIRMED_BOOKED",
  val escrowStatus: String = "ESCROW_SECURED",
  val timestamp: Long = System.currentTimeMillis()
)

data class GigReview(
  val id: String,
  val gigId: String,
  val eventTitle: String,
  val professionalId: String,
  val professionalName: String,
  val organizerId: String,
  val organizerName: String,
  val organizerOrganization: String,
  val roleExecuted: String,
  val rating: Float, // 1.0 to 5.0
  val reviewText: String,
  val punctualScore: Float = 5.0f,
  val technicalCompetenceScore: Float = 5.0f,
  val teamworkScore: Float = 5.0f,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String = "Oct 2026",
  val verifiedGigBadge: Boolean = true
)

