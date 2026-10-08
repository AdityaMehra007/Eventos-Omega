package com.example.data.model

enum class UserRole(val label: String, val badge: String) {
  WORKER("Freelancer / Promoter", "TALENT"),
  SUPERVISOR("Site Supervisor", "OPS"),
  MANAGER("Event Manager", "COMMAND"),
  VENDOR("Vendor / Supplier", "SUPPLY"),
  CLIENT("Brand / Client", "CLIENT")
}

data class EventosUser(
  val id: String = "usr_blr_001",
  val eventosId: String = "EVT-BLR-P-000001",
  val name: String = "Aditya Mehra",
  val email: String = "aditya.mehra@eventos.in",
  val phone: String = "+91 98860 12345",
  val role: UserRole = UserRole.MANAGER,
  val title: String = "Lead Technical Production & Operations",
  val city: String = "Bengaluru",
  val primaryZone: String = "Koramangala",
  val serviceRadiusKm: Int = 25,
  val hourlyRateINR: Int = 1800,
  val rating: Float = 4.92f,
  val reviewCount: Int = 48,
  val verifiedHours: Int = 540,
  val completedEvents: Int = 36,
  val isVerified: Boolean = true,
  val skills: List<String> = listOf("Stage Management", "AV Production", "Crowd Control", "Incident Response", "Crew Deployment"),
  val badges: List<String> = listOf("Verified Supervisor", "Production Specialist", "Top 5% Bangalore 2026", "Gold Trust Seal"),
  val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80"
)

data class PassportStamp(
  val eventId: String,
  val eventName: String,
  val role: String,
  val date: String,
  val hoursLogged: Int,
  val location: String,
  val verifiedBy: String,
  val rating: Float,
  val proofHash: String,
  val clientEndorsement: String
)
