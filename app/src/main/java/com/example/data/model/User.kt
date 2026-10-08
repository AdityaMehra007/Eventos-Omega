package com.example.data.model

enum class UserRole(val label: String, val badge: String) {
  WORKER("Freelancer / Promoter", "TALENT"),
  SUPERVISOR("Site Supervisor", "OPS"),
  MANAGER("Event Manager", "COMMAND"),
  VENDOR("Vendor / Supplier", "SUPPLY"),
  CLIENT("Brand / Client", "CLIENT")
}

data class PortfolioItem(
  val id: String,
  val title: String,
  val category: String, // e.g. "Main Stage Audio", "LED & Lighting Rigging", "Crowd Management"
  val eventName: String,
  val year: String,
  val role: String,
  val description: String,
  val mediaUrl: String = "",
  val metrics: String = "" // e.g. "4,500 Attendees • 0 Delays"
)

data class EventosUser(
  val id: String = "usr_blr_001",
  val eventosId: String = "EVT-BLR-P-000001",
  val name: String = "Aditya Mehra",
  val email: String = "aditya.mehra@eventos.in",
  val phone: String = "+91 98860 12345",
  val role: UserRole = UserRole.MANAGER,
  val title: String = "Lead Technical Production & Operations",
  val bio: String = "Bengaluru-based Event Technical Director with 8+ years specializing in stadium concerts, premier tech conferences, and high-stakes brand activations across Palace Grounds, KTPO, and Manpho Convention. Expert in sound engineering, crowd logistics, and real-time operations command.",
  val city: String = "Bengaluru",
  val primaryZone: String = "Koramangala",
  val serviceRadiusKm: Int = 25,
  val hourlyRateINR: Int = 1800,
  val rating: Float = 4.92f,
  val reviewCount: Int = 48,
  val verifiedHours: Int = 540,
  val completedEvents: Int = 36,
  val isVerified: Boolean = true,
  val skills: List<String> = listOf("Stage Management", "AV Production", "Crowd Control", "Incident Response", "Crew Deployment", "Wireless Frequency Scanning", "DiGiCo SD12"),
  val badges: List<String> = listOf("Verified Supervisor", "Production Specialist", "Top 5% Bangalore 2026", "Gold Trust Seal"),
  val portfolio: List<PortfolioItem> = listOf(
    PortfolioItem(
      id = "port_01",
      title = "Palace Grounds Main Stage Audio & Rigging",
      category = "Technical Support",
      eventName = "Bengaluru Tech Summit 2026",
      year = "2026",
      role = "Lead Technical Production",
      description = "Engineered 32-channel L-Acoustics line array system and wireless mic frequencies for 4,500 delegates and international keynotes.",
      metrics = "4,500 Delegates • 0 Audio Drops",
      mediaUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80"
    ),
    PortfolioItem(
      id = "port_02",
      title = "Curved 4K UHD LED Wall & Lighting Truss Sync",
      category = "Music",
      eventName = "Sunburn Arena Bangalore",
      year = "2025",
      role = "AV Production Marshal",
      description = "Coordinated 120sqm P2.9 curved LED backdrop, NovaStar processors, and synchronized timecode laser trussing.",
      metrics = "12,000 Attendees • 100% Uptime",
      mediaUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80"
    ),
    PortfolioItem(
      id = "port_03",
      title = "Experiential Launch Booth & VIP Protocol",
      category = "Corporate",
      eventName = "Titan Edge Luxury Showcase",
      year = "2025",
      role = "Operations Director",
      description = "Designed guest accreditation flow and turnstile RFID access control for ultra-high-net-worth VIP dinner.",
      metrics = "450 VIPs • <15s Check-in Speed",
      mediaUrl = "https://images.unsplash.com/photo-1511578314322-379afb476865?w=600&auto=format&fit=crop&q=80"
    )
  ),
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
