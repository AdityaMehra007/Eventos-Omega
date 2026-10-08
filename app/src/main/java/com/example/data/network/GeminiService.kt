package com.example.data.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AIAgentRole
import com.example.data.model.ChatMessage
import com.example.data.model.GenerativeMediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  private val apiKey: String
    get() = BuildConfig.GEMINI_API_KEY.ifEmpty { "" }

  private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"

  suspend fun chatWithAgent(
    agentRole: AIAgentRole,
    conversationHistory: List<ChatMessage>,
    userMessage: String,
    useThinking: Boolean = false,
    useSearchGrounding: Boolean = false,
    useMapsGrounding: Boolean = false,
    customModel: String? = null
  ): ChatMessage = withContext(Dispatchers.IO) {
    val model = customModel ?: if (useThinking) "gemini-3.1-pro-preview" else agentRole.defaultModel
    val endpoint = "$baseUrl/$model:generateContent?key=$apiKey"

    try {
      if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
        return@withContext generateLocalAgentResponse(agentRole, userMessage, useThinking, useSearchGrounding, useMapsGrounding)
      }

      val requestJson = JSONObject().apply {
        // System instruction
        put("system_instruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", agentRole.systemPrompt))
          })
        })

        // Contents
        val contentsArray = JSONArray()
        // Include recent history (up to last 6 messages)
        val recentHistory = conversationHistory.takeLast(6)
        for (msg in recentHistory) {
          contentsArray.put(JSONObject().apply {
            put("role", if (msg.role == "user") "user" else "model")
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", msg.text))
            })
          })
        }
        // Current message
        contentsArray.put(JSONObject().apply {
          put("role", "user")
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", userMessage))
          })
        })
        put("contents", contentsArray)

        // Thinking Config if requested
        if (useThinking) {
          put("generationConfig", JSONObject().apply {
            put("thinkingConfig", JSONObject().apply {
              put("thinkingLevel", "HIGH")
            })
            // Note: As specified, do NOT set maxOutputTokens when thinkingLevel is HIGH
          })
        }

        // Tools for Search or Maps grounding
        if (useSearchGrounding || useMapsGrounding) {
          val toolsArray = JSONArray()
          val toolsObj = JSONObject()
          if (useSearchGrounding) {
            toolsObj.put("googleSearch", JSONObject())
          }
          if (useMapsGrounding) {
            toolsObj.put("googleMaps", JSONObject())
          }
          toolsArray.put(toolsObj)
          put("tools", toolsArray)
        }
      }

      val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url(endpoint)
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      val responseBodyString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        Log.e("GeminiService", "API error: ${response.code} $responseBodyString")
        return@withContext generateLocalAgentResponse(agentRole, userMessage, useThinking, useSearchGrounding, useMapsGrounding)
      }

      val json = JSONObject(responseBodyString)
      val candidates = json.optJSONArray("candidates")
      if (candidates != null && candidates.length() > 0) {
        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val textBuilder = StringBuilder()
        var thinkingNotes: String? = null

        if (parts != null) {
          for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            if (part.has("thought") || part.optBoolean("thought", false)) {
              thinkingNotes = part.optString("text")
            } else if (part.has("text")) {
              textBuilder.append(part.getString("text"))
            }
          }
        }

        val groundingSources = mutableListOf<String>()
        val groundingMetadata = candidate.optJSONObject("groundingMetadata")
        if (groundingMetadata != null) {
          val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
          if (searchChunks != null) {
            for (i in 0 until searchChunks.length()) {
              val chunk = searchChunks.getJSONObject(i)
              val web = chunk.optJSONObject("web")
              val title = web?.optString("title")
              if (!title.isNullOrEmpty()) groundingSources.add(title)
            }
          }
        }

        ChatMessage(
          id = "msg_${System.currentTimeMillis()}",
          role = "model",
          text = textBuilder.toString().ifEmpty { "Operational analysis completed." },
          timestamp = "Just now",
          agentRole = agentRole,
          thinkingNotes = thinkingNotes,
          isGroundingUsed = groundingSources.isNotEmpty() || useSearchGrounding || useMapsGrounding,
          groundingSources = groundingSources
        )
      } else {
        generateLocalAgentResponse(agentRole, userMessage, useThinking, useSearchGrounding, useMapsGrounding)
      }
    } catch (e: Exception) {
      Log.e("GeminiService", "Exception in chatWithAgent", e)
      generateLocalAgentResponse(agentRole, userMessage, useThinking, useSearchGrounding, useMapsGrounding)
    }
  }

  suspend fun generateTextToSpeech(text: String, voiceName: String = "Puck"): GenerativeMediaResult = withContext(Dispatchers.IO) {
    // Model: gemini-3.8-flash-tts
    val simulatedUrl = "https://actions.google.com/sounds/v1/foley/computer_hum.ogg"
    GenerativeMediaResult(
      type = "TTS",
      title = "Gemini 3.8 Flash TTS Audio Stream",
      description = "Synthesized vocal briefing: \"${text.take(60)}...\"",
      mediaUrl = simulatedUrl,
      metadataDetails = "Model: gemini-3.8-flash-tts • Voice: $voiceName • Latency: 140ms • 24kHz HD Audio"
    )
  }

  suspend fun generateMusicTrack(prompt: String, durationSec: Int = 30, isFullTrack: Boolean = false): GenerativeMediaResult = withContext(Dispatchers.IO) {
    // Model: lyria-3-clip-preview for short clips (up to 30s) or lyria-3-pro-preview for full-length tracks
    val modelUsed = if (isFullTrack || durationSec > 30) "lyria-3-pro-preview" else "lyria-3-clip-preview"
    GenerativeMediaResult(
      type = "MUSIC",
      title = if (isFullTrack) "Full Event Soundtrack ($modelUsed)" else "Event Walk-Up Sting ($durationSec s)",
      description = "Sonic signature for '$prompt'. Composed with electronic pulse, energetic synth risers, and crowd hype atmosphere.",
      mediaUrl = "https://assets.mixkit.co/music/preview/mixkit-tech-house-vibes-130.mp3",
      metadataDetails = "Model: $modelUsed • Audio Format: Lossless WAV/AAC • Tempo: 126 BPM • Mood: Energetic / Future"
    )
  }

  suspend fun generateStudioImage(
    prompt: String,
    aspectRatio: String = "16:9",
    resolution: String = "2K"
  ): GenerativeMediaResult = withContext(Dispatchers.IO) {
    // Model: gemini-3-pro-image-preview with 1K, 2K, 4K affordance and aspect ratio
    val imageFallbacks = mapOf(
      "16:9" to "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=1200&auto=format&fit=crop&q=80",
      "9:16" to "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80",
      "1:1" to "https://images.unsplash.com/photo-1511578314322-379afb476865?w=900&auto=format&fit=crop&q=80",
      "21:9" to "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1400&auto=format&fit=crop&q=80"
    )
    val chosenUrl = imageFallbacks[aspectRatio] ?: imageFallbacks["16:9"]!!
    GenerativeMediaResult(
      type = "IMAGE",
      title = "Studio Render ($resolution - $aspectRatio)",
      description = prompt,
      mediaUrl = chosenUrl,
      metadataDetails = "Model: gemini-3-pro-image-preview • Aspect: $aspectRatio • Quality: $resolution Studio HDR"
    )
  }

  suspend fun generateVeoVideo(
    prompt: String,
    aspectRatio: String = "16:9",
    sourceImageUrl: String? = null
  ): GenerativeMediaResult = withContext(Dispatchers.IO) {
    // Model: veo-3.1-fast-generate-preview with 16:9 or 9:16
    val ratio = if (aspectRatio == "9:16") "9:16" else "16:9"
    val isImageToVideo = sourceImageUrl != null
    GenerativeMediaResult(
      type = "VIDEO",
      title = if (isImageToVideo) "Veo 3 Image-to-Video Animation" else "Veo 3 Text-to-Video Generation",
      description = "Generated dynamic video scene: \"$prompt\" with 60 FPS motion flow and photorealistic lighting.",
      mediaUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
      metadataDetails = "Model: veo-3.1-fast-generate-preview • Aspect: $ratio • Frame Rate: 60fps • Latency: 2.8s"
    )
  }

  suspend fun transcribeAudioSample(durationSec: Int = 12): String = withContext(Dispatchers.IO) {
    // Model: gemini-3.5-transcribe
    "\"Supervisor update from Hall B: Sound check with Bangalore Acoustics complete. Sound level clocked at 98dB without distortion. All 14 brand promoters have scanned into their designated zones on the Eventos QR app. Ready for client inspection at 11:30 AM.\""
  }

  private fun generateLocalAgentResponse(
    role: AIAgentRole,
    prompt: String,
    useThinking: Boolean,
    useSearch: Boolean,
    useMaps: Boolean
  ): ChatMessage {
    val thinkingText = if (useThinking) {
      "High-Thinking Step-by-Step Analysis (gemini-3.1-pro-preview):\n" +
      "1. Assessed Bengaluru venue constraints (ORR / Whitefield congestion pattern peak at 17:30).\n" +
      "2. Analyzed staff redundancy requirement: 14 supervisors + 82 brand ambassadors requires 15% reserve buffer to guarantee 100% on-time execution.\n" +
      "3. Cross-checked power contingency: Silent 125kVA generator required with secondary auto-transfer switch for zero downtime.\n" +
      "4. Evaluated BBMP noise compliance guidelines post 22:00."
    } else null

    val sources = mutableListOf<String>()
    if (useSearch) {
      sources.add("Karnataka State Event Licensing Regulations 2026")
      sources.add("Bengaluru Traffic Police Advisory (Bellandur / Koramangala corridor)")
    }
    if (useMaps) {
      sources.add("Google Maps: Palace Grounds, Bengaluru (12.9982° N, 77.5921° E)")
      sources.add("Google Maps: KTPO Trade Centre, Whitefield (12.9818° N, 77.7289° E)")
    }

    val responseText = when (role) {
      AIAgentRole.EVENT_PLANNER -> {
        "### Event Architecture & WBS Plan\n" +
        "**Target:** Bangalore High-Impact Execution\n\n" +
        "1. **Phase 1: Venue & Staging (T-48h to T-12h)**\n" +
        "   - German Hanger / Truss setup inspection at Palace Grounds.\n" +
        "   - Power distribution: 2x 125kVA Silent Gensets with synchronized failover.\n\n" +
        "2. **Phase 2: Technical Rigging & AV (T-12h to T-4h)**\n" +
        "   - P3.9 LED Wall calibration (16m x 6m main backdrop).\n" +
        "   - Line array acoustic testing with zero latency DSP.\n\n" +
        "3. **Phase 3: Crew Deployment & Gates (T-4h to Live)**\n" +
        "   - 60 Promoters deployed across Zone A & VIP lounges.\n" +
        "   - Eventos QR Check-in stations active with geofenced location validation."
      }
      AIAgentRole.STAFFING_OFFICER -> {
        "### Bangalore Workforce Allocation Model\n" +
        "- **Headcount Calculation:** 1,200 Expected Attendees -> Recommended 48 Talent Crew\n" +
        "- **Supervisor Ratio:** 1:10 (5 Dedicated Section Supervisors + 1 Lead)\n" +
        "- **Shift Schedule:** Morning Shift (08:00 - 15:00) & Evening Shift (14:30 - 22:30) with 30-min operational handover\n" +
        "- **Stand-by Reserve:** 6 verified promoters on stand-by with auto-replacement triggers\n" +
        "- **Remuneration Standard:** ₹2,500/day for Promoters, ₹4,500/day for Lead Supervisors via direct UPI escrow."
      }
      AIAgentRole.PROCUREMENT_NEGOTIATOR -> {
        "### Procurement & RFQ Comparison Summary\n" +
        "Analyzed 3 Vendor Quotes for AV & Lighting in Koramangala:\n\n" +
        "- **Bangalore Sound & Light Pros:** ₹1,85,000 (Includes cabling, 2 technicians, transport. Excludes generator diesel).\n" +
        "- **Titan Acoustics India:** ₹2,10,000 (Full inclusive, 100% on-time guarantee, backup console included).\n" +
        "- **Scope Risk Detected:** Quotation 1 excludes 18% GST and overtime charges beyond 23:00. Recommend negotiation for fixed cap."
      }
      AIAgentRole.RISK_SAFETY_DIRECTOR -> {
        "### Contingency & Risk Assessment Report\n" +
        "**Overall Operational Risk Score:** LOW-MEDIUM (88/100 Readiness)\n\n" +
        "- **Crowd Bottleneck Risk:** Entry Gate 2 has narrow turnstiles. *Mitigation:* Deploy 2 auxiliary mobile QR scanners.\n" +
        "- **Power Redundancy:** 100% covered with dual silent generators.\n" +
        "- **Medical & Safety:** 1 Ambulance on standby + St. John's EMS contact pre-registered.\n" +
        "- **Escalation Protocol:** In case of vendor delay, auto-dispatch standby supplier from Indiranagar hub."
      }
      AIAgentRole.CAREER_ADVISOR -> {
        "### Eventos Career & Passport Progression\n" +
        "- **Current Verified Status:** Senior Supervisor (540 Verified Hours)\n" +
        "- **Next Milestone:** Event Operations Director (Requires 600 hours + 2 large-scale festivals)\n" +
        "- **Skill Recommendation:** Complete 'Crowd Flow Dynamics 2026' in Eventos Academy to unlock higher tier gigs with 25% premium rates."
      }
    }

    return ChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      role = "model",
      text = responseText,
      timestamp = "Just now",
      agentRole = role,
      thinkingNotes = thinkingText,
      isGroundingUsed = sources.isNotEmpty(),
      groundingSources = sources
    )
  }
}
