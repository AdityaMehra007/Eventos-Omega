package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AIAgentRole
import com.example.data.model.ChatMessage
import com.example.data.model.GenerativeMediaResult
import com.example.data.network.GeminiService
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AIAgentsScreen(
  geminiService: GeminiService,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Intelligence Chat", "Generative Studio", "Live Voice (3.8)")

  // Chat State
  var selectedAgent by remember { mutableStateOf(AIAgentRole.EVENT_PLANNER) }
  var useThinking by remember { mutableStateOf(false) }
  var useSearchGrounding by remember { mutableStateOf(false) }
  var useMapsGrounding by remember { mutableStateOf(false) }
  var useFastLite by remember { mutableStateOf(false) }
  var chatInput by remember { mutableStateOf("") }
  var isGeneratingChat by remember { mutableStateOf(false) }

  var messages by remember {
    mutableStateOf(
      listOf(
        ChatMessage(
          id = "init_1",
          role = "model",
          text = "Welcome to Eventos AI Intelligence Layer. I am configured as your **Master Event Architect** for Bengaluru. Ask for WBS schedules, staff allocations, vendor RFQs, contingency simulations, or localized city intelligence.",
          timestamp = "Just now",
          agentRole = AIAgentRole.EVENT_PLANNER
        )
      )
    )
  }

  // Generative Studio State
  var studioSubTab by remember { mutableIntStateOf(0) } // 0: TTS, 1: Music, 2: Image, 3: Video
  var ttsInput by remember { mutableStateOf("Attention Palace Grounds operations team: Main stage keynote transitions at 11:30 AM. VIP delegation has arrived at Gate 1.") }
  var ttsVoice by remember { mutableStateOf("Puck (Executive)") }
  var musicPrompt by remember { mutableStateOf("High energy electronic festival walk-up synth with punchy sub-bass and futuristic Bengaluru crowd hype") }
  var isFullMusicTrack by remember { mutableStateOf(false) }
  var imagePrompt by remember { mutableStateOf("Futuristic Bangalore high-tech event main stage with curved 4K LED backdrop, dynamic purple lasers, and professional production crew") }
  var selectedAspectRatio by remember { mutableStateOf("16:9") }
  var selectedResolution by remember { mutableStateOf("2K") }
  var videoPrompt by remember { mutableStateOf("Drone fly-through across Palace Grounds exhibition dome as laser lights power on at sunset") }
  var videoAspectRatio by remember { mutableStateOf("16:9") }
  var isGeneratingMedia by remember { mutableStateOf(false) }
  var generatedMediaResult by remember { mutableStateOf<GenerativeMediaResult?>(null) }

  // Live Voice State
  var isLiveVoiceActive by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Top Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.Transparent,
      contentColor = PrimaryIndigoLight,
      divider = {}
    ) {
      tabs.forEachIndexed { idx, title ->
        Tab(
          selected = selectedTab == idx,
          onClick = { selectedTab = idx },
          text = { Text(title, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (selectedTab) {
      0 -> { // Multi-Agent Chat
        // Agent Role Selector Bar
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(AIAgentRole.values()) { role ->
            FilterChip(
              selected = selectedAgent == role,
              onClick = { selectedAgent = role },
              label = { Text(role.title, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryIndigo.copy(alpha = 0.25f),
                selectedLabelColor = PrimaryIndigoLight
              ),
              modifier = Modifier.testTag("agent_role_${role.name}")
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Advanced Mode Toggles Bar (Thinking, Search, Maps, Lite)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FilterChip(
            selected = useThinking,
            onClick = {
              useThinking = !useThinking
              if (useThinking) useFastLite = false
            },
            label = { Text("High Thinking", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(12.dp), tint = AccentAmber)
            },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentAmber.copy(alpha = 0.25f), selectedLabelColor = AccentAmber),
            modifier = Modifier.testTag("toggle_thinking_btn")
          )
          FilterChip(
            selected = useSearchGrounding,
            onClick = { useSearchGrounding = !useSearchGrounding },
            label = { Text("Search", fontSize = 10.sp) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.TravelExplore, contentDescription = null, modifier = Modifier.size(12.dp), tint = AccentCyan)
            },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentCyan.copy(alpha = 0.25f), selectedLabelColor = AccentCyan),
            modifier = Modifier.testTag("toggle_search_btn")
          )
          FilterChip(
            selected = useMapsGrounding,
            onClick = { useMapsGrounding = !useMapsGrounding },
            label = { Text("Maps", fontSize = 10.sp) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.PinDrop, contentDescription = null, modifier = Modifier.size(12.dp), tint = EmeraldSuccess)
            },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldSuccess.copy(alpha = 0.25f), selectedLabelColor = EmeraldSuccess),
            modifier = Modifier.testTag("toggle_maps_btn")
          )
          FilterChip(
            selected = useFastLite,
            onClick = {
              useFastLite = !useFastLite
              if (useFastLite) useThinking = false
            },
            label = { Text("Lite (Fast)", fontSize = 10.sp) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(12.dp), tint = PrimaryIndigoLight)
            },
            modifier = Modifier.testTag("toggle_lite_btn")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Message Thread
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          items(messages) { msg ->
            if (msg.role == "user") {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Card(
                  shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                  colors = CardDefaults.cardColors(containerColor = PrimaryIndigo),
                  modifier = Modifier.widthIn(max = 300.dp)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = msg.text, color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = msg.timestamp, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.End))
                  }
                }
              }
            } else {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
              ) {
                Card(
                  shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = PrimaryIndigoLight, modifier = Modifier.size(16.dp))
                        Text(
                          text = msg.agentRole?.title ?: "Eventos AI",
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                      }
                      if (msg.isGroundingUsed) {
                        StatusBadge(text = "GROUNDED", color = AccentCyan)
                      }
                    }

                    // Thought Disclosure for High Thinking Mode
                    msg.thinkingNotes?.let { thinking ->
                      Spacer(modifier = Modifier.height(8.dp))
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AccentAmber.copy(alpha = 0.4f), Color.Transparent))),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(14.dp))
                            Text("Deep Thinking Chain (gemini-3.1-pro-preview)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                          }
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(text = thinking, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = msg.text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)

                    if (msg.groundingSources.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(8.dp))
                      Text("Verified Sources:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                      msg.groundingSources.forEach { src ->
                        Text("• $src", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }
                    }
                  }
                }
              }
            }
          }

          if (isGeneratingChat) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryIndigoLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing response...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }

        // Quick Operational Prompt Suggestions
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
          val suggestions = listOf(
            "Staff a 1,000-person conference in Bangalore",
            "Simulate power outage & rain contingency",
            "Analyze AV quote exclusions & GST impact",
            "What skills unlock my next supervisor rank?"
          )
          items(suggestions) { promptText ->
            SuggestionChip(
              onClick = { chatInput = promptText },
              label = { Text(promptText, fontSize = 10.sp) }
            )
          }
        }

        // Message Input Row with Audio Transcribe (gemini-3.5-transcribe)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Audio Transcription Button
          IconButton(
            onClick = {
              coroutineScope.launch {
                Toast.makeText(context, "Transcribing field audio with gemini-3.5-transcribe...", Toast.LENGTH_SHORT).show()
                val transcription = geminiService.transcribeAudioSample()
                chatInput = transcription
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .testTag("audio_transcribe_btn")
          ) {
            Icon(imageVector = Icons.Default.Mic, contentDescription = "Transcribe Audio", tint = AccentAmber)
          }

          OutlinedTextField(
            value = chatInput,
            onValueChange = { chatInput = it },
            placeholder = { Text("Ask ${selectedAgent.title}...", fontSize = 12.sp) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.weight(1f).testTag("chat_input_field")
          )

          IconButton(
            onClick = {
              if (chatInput.isNotBlank() && !isGeneratingChat) {
                val userPrompt = chatInput.trim()
                chatInput = ""
                val userMsg = ChatMessage(id = "usr_${System.currentTimeMillis()}", role = "user", text = userPrompt, timestamp = "Just now")
                messages = messages + userMsg
                isGeneratingChat = true

                coroutineScope.launch {
                  val customModel = if (useFastLite) "gemini-3.1-flash-lite" else null
                  val responseMsg = geminiService.chatWithAgent(
                    agentRole = selectedAgent,
                    conversationHistory = messages,
                    userMessage = userPrompt,
                    useThinking = useThinking,
                    useSearchGrounding = useSearchGrounding,
                    useMapsGrounding = useMapsGrounding,
                    customModel = customModel
                  )
                  messages = messages + responseMsg
                  isGeneratingChat = false
                  listState.animateScrollToItem(messages.size - 1)
                }
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(PrimaryIndigo)
              .testTag("chat_send_btn")
          ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }
      }

      1 -> { // Generative Studio
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            TabRow(
              selectedTabIndex = studioSubTab,
              containerColor = Color.Transparent,
              contentColor = AccentCyan,
              divider = {}
            ) {
              listOf("TTS (3.8)", "Music (Lyria)", "Studio Img (Pro)", "Veo 3 Video").forEachIndexed { i, title ->
                Tab(
                  selected = studioSubTab == i,
                  onClick = { studioSubTab = i },
                  text = { Text(title, fontSize = 11.sp, fontWeight = if (studioSubTab == i) FontWeight.Bold else FontWeight.Normal) }
                )
              }
            }
          }

          when (studioSubTab) {
            0 -> { // Text to Speech: gemini-3.8-flash-tts
              item {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Text to Speech (gemini-3.8-flash-tts)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryIndigoLight)
                    Text("Synthesize immediate natural audio announcements for field crew, gate marshals, and VIP welcomes.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                      value = ttsInput,
                      onValueChange = { ttsInput = it },
                      label = { Text("Announcement Script") },
                      modifier = Modifier.fillMaxWidth().testTag("tts_input_field")
                    )

                    Button(
                      onClick = {
                        isGeneratingMedia = true
                        coroutineScope.launch {
                          generatedMediaResult = geminiService.generateTextToSpeech(ttsInput)
                          isGeneratingMedia = false
                          Toast.makeText(context, "TTS Audio stream generated with gemini-3.8-flash-tts!", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                      modifier = Modifier.fillMaxWidth().testTag("synthesize_tts_btn")
                    ) {
                      Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Synthesize Audio (gemini-3.8-flash-tts)")
                    }
                  }
                }
              }
            }

            1 -> { // Music Generation: lyria-3-clip-preview & lyria-3-pro-preview
              item {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Music & Theme Track Generator (Lyria 3)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentAmber)
                    Text("Generate 30s walk-up clips with lyria-3-clip-preview or full-length tracks with lyria-3-pro-preview.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                      value = musicPrompt,
                      onValueChange = { musicPrompt = it },
                      label = { Text("Music / Mood Prompt") },
                      modifier = Modifier.fillMaxWidth().testTag("music_prompt_field")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      FilterChip(
                        selected = !isFullMusicTrack,
                        onClick = { isFullMusicTrack = false },
                        label = { Text("30s Clip (lyria-3-clip-preview)") }
                      )
                      FilterChip(
                        selected = isFullMusicTrack,
                        onClick = { isFullMusicTrack = true },
                        label = { Text("Full Track (lyria-3-pro-preview)") }
                      )
                    }

                    Button(
                      onClick = {
                        isGeneratingMedia = true
                        coroutineScope.launch {
                          generatedMediaResult = geminiService.generateMusicTrack(musicPrompt, if (isFullMusicTrack) 180 else 30, isFullMusicTrack)
                          isGeneratingMedia = false
                          Toast.makeText(context, "Lyria 3 Soundtrack generated!", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                      modifier = Modifier.fillMaxWidth().testTag("generate_music_btn")
                    ) {
                      Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp), tint = DarkSurfaceBase)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Generate Audio Signature", color = DarkSurfaceBase, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            2 -> { // Image Creation: gemini-3-pro-image-preview & gemini-3.1-flash-image-preview
              item {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Studio Image Generator (gemini-3-pro-image-preview)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCyan)
                    Text("Generate high-quality stage concepts, floor plans, and stage lighting renders.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                      value = imagePrompt,
                      onValueChange = { imagePrompt = it },
                      label = { Text("Creative Concept Prompt") },
                      modifier = Modifier.fillMaxWidth().testTag("image_prompt_field")
                    )

                    // Aspect Ratio Control (1:1, 2:3, 3:2, 3:4, 4:3, 9:16, 16:9, 21:9)
                    Text("Aspect Ratio:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      val ratios = listOf("16:9", "9:16", "1:1", "21:9", "4:3", "3:2")
                      items(ratios) { r ->
                        FilterChip(
                          selected = selectedAspectRatio == r,
                          onClick = { selectedAspectRatio = r },
                          label = { Text(r, fontSize = 11.sp) }
                        )
                      }
                    }

                    // Resolution Affordance (1K, 2K, 4K)
                    Text("Image Resolution Quality:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      listOf("1K", "2K", "4K").forEach { res ->
                        FilterChip(
                          selected = selectedResolution == res,
                          onClick = { selectedResolution = res },
                          label = { Text(res, fontSize = 11.sp) }
                        )
                      }
                    }

                    Button(
                      onClick = {
                        isGeneratingMedia = true
                        coroutineScope.launch {
                          generatedMediaResult = geminiService.generateStudioImage(imagePrompt, selectedAspectRatio, selectedResolution)
                          isGeneratingMedia = false
                          Toast.makeText(context, "Rendered studio visual with gemini-3-pro-image-preview!", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AccentCyanDark),
                      modifier = Modifier.fillMaxWidth().testTag("generate_image_btn")
                    ) {
                      Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Render $selectedResolution Visual ($selectedAspectRatio)")
                    }
                  }
                }
              }
            }

            3 -> { // Video Generation: veo-3.1-fast-generate-preview
              item {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Video Generation & Animation (veo-3.1-fast-generate-preview)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldSuccess)
                    Text("Generate video scenes from text or animate stage photos into cinematic motion clips.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                      value = videoPrompt,
                      onValueChange = { videoPrompt = it },
                      label = { Text("Video Action Prompt") },
                      modifier = Modifier.fillMaxWidth().testTag("video_prompt_field")
                    )

                    // Aspect Ratio: 16:9 or 9:16
                    Text("Video Format:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      listOf("16:9 (Landscape)", "9:16 (Portrait Story)").forEach { rat ->
                        val rKey = if (rat.startsWith("16:9")) "16:9" else "9:16"
                        FilterChip(
                          selected = videoAspectRatio == rKey,
                          onClick = { videoAspectRatio = rKey },
                          label = { Text(rat, fontSize = 11.sp) }
                        )
                      }
                    }

                    Button(
                      onClick = {
                        isGeneratingMedia = true
                        coroutineScope.launch {
                          generatedMediaResult = geminiService.generateVeoVideo(videoPrompt, videoAspectRatio)
                          isGeneratingMedia = false
                          Toast.makeText(context, "Veo 3.1 video generation completed!", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                      modifier = Modifier.fillMaxWidth().testTag("generate_video_btn")
                    ) {
                      Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Generate Veo 3 Video")
                    }
                  }
                }
              }
            }
          }

          // Output Display Card
          generatedMediaResult?.let { result ->
            item {
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PrimaryIndigo, AccentCyan))),
                modifier = Modifier.fillMaxWidth().testTag("generative_result_card")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = result.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    StatusBadge(text = result.type, color = AccentCyan)
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  if (result.type == "IMAGE") {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))) {
                      AsyncImage(
                        model = result.mediaUrl,
                        contentDescription = result.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                      )
                    }
                  } else {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MaterialTheme.colorScheme.surface,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Play", tint = EmeraldSuccess, modifier = Modifier.size(32.dp))
                        Column {
                          Text(text = result.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                          Text(text = result.metadataDetails, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(text = result.metadataDetails, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextMutedDark)
                }
              }
            }
          }
        }
      }

      2 -> { // Live Voice (gemini-3.8-live)
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(100.dp)
                  .clip(CircleShape)
                  .background(
                    if (isLiveVoiceActive) Brush.linearGradient(listOf(CrimsonAlert, AccentAmber))
                    else Brush.linearGradient(listOf(PrimaryIndigo, AccentCyan))
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isLiveVoiceActive) Icons.Default.GraphicEq else Icons.Default.Mic,
                  contentDescription = "Voice Stream",
                  tint = Color.White,
                  modifier = Modifier.size(48.dp)
                )
              }

              Text(
                text = if (isLiveVoiceActive) "LIVE AUDIO CONVERSATION CONNECTED" else "gemini-3.8-live Voice Mode",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (isLiveVoiceActive) EmeraldSuccess else Color.White
              )

              Text(
                text = if (isLiveVoiceActive)
                  "Real-time bidirectional operational voice stream. Ask about staff arrivals, vendor delays, or request live incident escalations."
                else "Tap below to open a low-latency live conversational channel with the Eventos Omega Live API.",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )

              Button(
                onClick = {
                  isLiveVoiceActive = !isLiveVoiceActive
                  Toast.makeText(
                    context,
                    if (isLiveVoiceActive) "Live API (gemini-3.8-live) session initiated!" else "Live Voice session ended.",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isLiveVoiceActive) CrimsonAlert else PrimaryIndigo
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("toggle_live_voice_btn")
              ) {
                Icon(
                  imageVector = if (isLiveVoiceActive) Icons.Default.CallEnd else Icons.Default.Call,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (isLiveVoiceActive) "End Live Voice Session" else "Start Live Conversation (gemini-3.8-live)",
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
