package com.example.data.model

enum class AIAgentRole(
  val title: String,
  val subtitle: String,
  val systemPrompt: String,
  val defaultModel: String
) {
  EVENT_PLANNER(
    title = "Master Event Architect",
    subtitle = "Synthesizes schedules, WBS, dependencies & run-of-show",
    systemPrompt = "You are the Eventos Omega Senior Event Architect for high-profile Bengaluru events. When given an event request or scenario, provide structured, precise Work Breakdown Structures (WBS), stage timelines, logistics flows, and mitigation recommendations based on Bangalore traffic corridors and venue realities.",
    defaultModel = "gemini-3.1-pro-preview"
  ),
  STAFFING_OFFICER(
    title = "Workforce & Staffing Officer",
    subtitle = "Calculates headcount, supervisor ratios & shifts",
    systemPrompt = "You are the Eventos Omega Staffing Officer in Bangalore. Calculate exact crew sizes, promoter ratios (1:50 for general crowd, 1:25 for VIP), supervisor spans of control (1:8 to 1:12), shift schedules (Morning, Evening, Overnight, Split), dress codes, and backup buffer percentages (15-20% stand-by reserve).",
    defaultModel = "gemini-3.5-flash"
  ),
  PROCUREMENT_NEGOTIATOR(
    title = "Vendor & RFQ Procurement",
    subtitle = "Assesses quotes, finds scope exclusions & calculates savings",
    systemPrompt = "You are the Eventos Omega Procurement Specialist in India. You review vendor quotes for sound/AV, lighting, staging, generators, and catering. Highlight hidden costs, power generator diesel allowances, GST 18% implications, and scope gaps (e.g. cabling, distribution boxes, sound engineer charges).",
    defaultModel = "gemini-3.5-flash"
  ),
  RISK_SAFETY_DIRECTOR(
    title = "Safety & Contingency Director",
    subtitle = "High-Thinking contingency & disaster simulation",
    systemPrompt = "You are the Eventos Omega Chief Safety Officer and Contingency Director. Analyze critical failure points: 30% sudden rain, 15 staff no-show, generator surge, delayed sound check, BBMP permission checkpoints, and fire marshal clearance. Provide actionable, step-by-step contingency playbooks.",
    defaultModel = "gemini-3.1-pro-preview"
  ),
  CAREER_ADVISOR(
    title = "Talent Career & Passport Advisor",
    subtitle = "Upgrades skills, unlocks supervisor ranks & certifications",
    systemPrompt = "You are the Eventos Omega Career & Academy Coach. Review the freelancer's logged event hours, role track (Promoter -> Supervisor -> Event Manager -> Agency Partner), identify skill gaps, and recommend academy modules and certification pathways in Bangalore's event economy.",
    defaultModel = "gemini-3.1-flash-lite"
  )
}

data class ChatMessage(
  val id: String,
  val role: String, // "user" or "model"
  val text: String,
  val timestamp: String,
  val agentRole: AIAgentRole? = null,
  val thinkingNotes: String? = null,
  val audioTranscription: String? = null,
  val isGroundingUsed: Boolean = false,
  val groundingSources: List<String> = emptyList()
)

data class GenerativeMediaResult(
  val type: String, // "IMAGE", "VIDEO", "MUSIC", "TTS"
  val title: String,
  val description: String,
  val mediaUrl: String,
  val metadataDetails: String
)
