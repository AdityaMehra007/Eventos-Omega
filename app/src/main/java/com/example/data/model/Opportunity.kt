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
  val urgencyTag: String = "High Demand"
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
