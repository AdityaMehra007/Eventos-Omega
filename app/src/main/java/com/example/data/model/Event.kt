package com.example.data.model

enum class EventType(val label: String) {
  TECH_CONFERENCE("Tech Summit & Conference"),
  BRAND_ACTIVATION("Brand Activation & Mall Pop-up"),
  MUSIC_CONCERT("Concert & Music Festival"),
  COLLEGE_FEST("University / College Fest"),
  CORPORATE_SUMMIT("Corporate Product Launch"),
  WEDDING_LUXURY("Luxury Destination Wedding"),
  EXHIBITION("Trade Show & Exhibition")
}

enum class EventStatus(val label: String) {
  PLANNING("Planning"),
  STAFFING("Staffing Active"),
  CONFIRMED("Confirmed"),
  LIVE("LIVE NOW"),
  COMPLETED("Completed"),
  FINANCIAL_SETTLED("Settled")
}

data class EventRecord(
  val id: String,
  val eventosId: String,
  val title: String,
  val type: EventType,
  val status: EventStatus,
  val clientName: String,
  val organizerName: String,
  val venueName: String,
  val areaZone: String, // e.g. "Whitefield", "Koramangala", "Indiranagar", "Electronic City", "Hebbal"
  val date: String,
  val timeSlot: String,
  val attendeesExpected: Int,
  val attendeesCheckedIn: Int,
  val totalStaffNeeded: Int,
  val staffConfirmed: Int,
  val staffCheckedIn: Int,
  val budgetEstimatedINR: Long,
  val budgetSpentINR: Long,
  val coverImageUrl: String,
  val description: String,
  val criticalAlerts: List<String> = emptyList()
)

data class ShiftRecord(
  val id: String,
  val eventId: String,
  val title: String,
  val shiftType: String, // Morning, Afternoon, Evening, Overnight, Split
  val startTime: String,
  val endTime: String,
  val requiredRoles: Map<String, Int>,
  val supervisorName: String,
  val activeCheckedIn: Int,
  val totalAssigned: Int
)

data class AttendanceCheckIn(
  val id: String,
  val eventId: String,
  val workerId: String,
  val workerName: String,
  val role: String,
  val timestamp: String,
  val method: String, // "QR Scanner", "Geofence Auto", "Supervisor Override"
  val status: String, // "Checked In", "Checked Out", "No Show"
  val locationCoords: String = "12.9352° N, 77.6245° E (Koramangala)"
)

data class OperationalIncident(
  val id: String,
  val eventId: String,
  val title: String,
  val category: String, // Staffing, AV / Power, Crowd Flow, Vendor Delay, Safety
  val severity: String, // LOW, MEDIUM, CRITICAL
  val status: String, // Investigating, Escalated, Resolved
  val reportedAt: String,
  val reportedBy: String,
  val actionSummary: String,
  val resolutionNotes: String = ""
)

data class EventTaskItem(
  val id: String,
  val eventId: String,
  val title: String,
  val dependency: String, // e.g. "Stage Build -> AV Rigging"
  val assignee: String,
  val deadlineTime: String,
  val isCompleted: Boolean = false,
  val category: String
)
