package com.example.data.model

enum class VendorCategory(val label: String, val iconName: String) {
  AV_SOUND("Sound & Acoustic Engineering", "VolumeUp"),
  LIGHTING_TRUSS("LED Walls, Truss & Intelligent Lighting", "Lightbulb"),
  STAGING("German Tent, Staging & Fabrications", "Architecture"),
  POWER_GEN("Silent Generators & Power Distribution", "Bolt"),
  CATERING("Corporate & VIP Catering / F&B", "Restaurant"),
  SECURITY_BOUNCERS("Accredited Security, Bouncers & Marshals", "Shield"),
  PHOTO_VIDEO("Cinema 4K Multi-cam, Drones & Live Streaming", "Videocam")
}

data class VendorListing(
  val id: String,
  val businessName: String,
  val category: VendorCategory,
  val areaZone: String,
  val serviceRadiusKm: Int,
  val rating: Float,
  val completedProjects: Int,
  val verificationBadge: String, // "GST Verified", "ISO Compliant", "Premier Partner"
  val baseRateEstINR: String,
  val capacityDesc: String,
  val inventoryHighlights: List<String>,
  val contactPerson: String,
  val isPreferredVendor: Boolean = true
)

data class VendorQuotation(
  val id: String,
  val rfqId: String,
  val vendorId: String,
  val vendorName: String,
  val category: VendorCategory,
  val quoteAmountINR: Long,
  val scopeIncluded: List<String>,
  val scopeExcluded: List<String>,
  val setupHoursRequired: Int,
  val deliveryDate: String,
  val riskAssessment: String, // "Low Risk - 100% on-time record"
  val status: String // "Pending", "Selected", "Rejected"
)

data class EquipmentItem(
  val id: String,
  val name: String,
  val serialNumber: String,
  val category: String,
  val condition: String, // "EXCELLENT", "GOOD", "MAINTENANCE_DUE"
  val assignedEvent: String,
  val returnStatus: String // "CHECKED_OUT", "ON_SITE", "RETURNED_VERIFIED"
)
