package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EventRecord
import com.example.data.model.EventosUser
import com.example.data.model.StaffOpportunity
import com.example.data.model.UserRole
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  user: EventosUser,
  events: List<EventRecord>,
  opportunities: List<StaffOpportunity>,
  isFirestoreLive: Boolean,
  isSyncing: Boolean,
  onRefreshFirestore: () -> Unit,
  onPostNewGig: (StaffOpportunity) -> Unit,
  onBookGig: (StaffOpportunity) -> Unit,
  onNavigateToControlRoom: () -> Unit,
  onNavigateToMarketplace: () -> Unit,
  onNavigateToWorkspace: (String) -> Unit,
  onNavigateToPassport: () -> Unit,
  onNavigateToAI: () -> Unit,
  onNavigateToProfile: () -> Unit = {},
  onApplyOpportunity: (String) -> Unit,
  onLeaveReview: (gigId: String, eventTitle: String, role: String, rating: Float, reviewText: String, orgName: String, orgOrg: String, punctual: Float, tech: Float, team: Float) -> Unit = { _, _, _, _, _, _, _, _, _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val liveEvent = events.firstOrNull { it.status == com.example.data.model.EventStatus.LIVE } ?: events.first()

  var selectedGigForDetails by remember { mutableStateOf<StaffOpportunity?>(null) }
  var latestConfirmedBookingRef by remember { mutableStateOf<String?>(null) }
  var gigToReview by remember { mutableStateOf<StaffOpportunity?>(null) }
  var reviewRating by remember { mutableFloatStateOf(5.0f) }
  var reviewText by remember { mutableStateOf("") }
  var reviewOrganizerName by remember { mutableStateOf("Aditya Mehra (Lead Organizer)") }
  var reviewOrganizerOrg by remember { mutableStateOf("Eventos Bangalore Ops Guild") }
  var reviewPunctual by remember { mutableFloatStateOf(5.0f) }
  var reviewTechnical by remember { mutableFloatStateOf(5.0f) }
  var reviewTeamwork by remember { mutableFloatStateOf(5.0f) }

  var showPostGigDialog by remember { mutableStateOf(false) }
  var newGigRole by remember { mutableStateOf("") }
  var newGigEvent by remember { mutableStateOf("") }
  var newGigDescription by remember { mutableStateOf("") }
  var newGigZone by remember { mutableStateOf("Koramangala") }
  var newGigCategory by remember { mutableStateOf("Corporate") }
  var newGigPay by remember { mutableStateOf("3000") }
  var newGigHours by remember { mutableStateOf("09:00 - 18:00") }

  var gigSearchQuery by remember { mutableStateOf("") }
  var selectedCategoryFilter by remember { mutableStateOf("All") }
  var selectedSortOption by remember { mutableStateOf("Best Match") }

  val categories = listOf("All", "Music", "Corporate", "Weddings", "Technical Support")
  val sortOptions = listOf("Best Match", "Highest Pay", "Urgent Fill")

  val filteredAndSortedOpportunities = remember(opportunities, gigSearchQuery, selectedCategoryFilter, selectedSortOption) {
    val query = gigSearchQuery.trim().lowercase()
    opportunities
      .filter { opp ->
        val matchesCategory = if (selectedCategoryFilter == "All") true
        else opp.category.equals(selectedCategoryFilter, ignoreCase = true)

        val matchesSearch = if (query.isEmpty()) true
        else {
          opp.eventTitle.lowercase().contains(query) ||
            opp.description.lowercase().contains(query) ||
            opp.role.lowercase().contains(query)
        }

        matchesCategory && matchesSearch
      }
      .let { list ->
        when (selectedSortOption) {
          "Highest Pay" -> list.sortedByDescending { it.payRateINR }
          "Urgent Fill" -> list.sortedBy { it.spotsAvailable - it.spotsFilled }
          else -> list.sortedByDescending { it.matchScore }
        }
      }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Top Greeting & Bangalore Pulse
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("home_pulse_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(
              modifier = Modifier.clickable { onNavigateToProfile() }
            ) {
              Text(
                text = "Welcome back, ${user.name.split(" ").first()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "${user.role.label} • ${user.primaryZone}, Bengaluru",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                  imageVector = Icons.Default.ChevronRight,
                  contentDescription = "Edit Profile",
                  tint = PrimaryIndigoLight,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(EmeraldSuccess.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldSuccess))
                Text(
                  text = "CITY ACTIVE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = EmeraldSuccess
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          Spacer(modifier = Modifier.height(10.dp))

          // Bangalore Pulse Stats Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("FIRESTORE GIGS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
              Text("${opportunities.size} Live", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
            }
            Column {
              Text("VERIFIED CREW", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
              Text("1,840 Active", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryIndigoLight)
            }
            Column {
              Text("WEATHER / TRAFFIC", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
              Text("27°C • ORR Clear", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AccentAmber)
            }
          }
        }
      }
    }

    // Role-Specific Quick Metrics
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        when (user.role) {
          UserRole.WORKER -> {
            MetricCard(
              title = "Logged Hours",
              value = "${user.verifiedHours} hrs",
              subtitle = "₹${user.verifiedHours * 350} Earned",
              icon = Icons.Default.Timer,
              accentColor = AccentCyan,
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Trust Rating",
              value = "${user.rating} ★",
              subtitle = "${user.completedEvents} Events Done",
              icon = Icons.Default.VerifiedUser,
              accentColor = AccentAmber,
              modifier = Modifier.weight(1f)
            )
          }
          UserRole.SUPERVISOR, UserRole.MANAGER -> {
            MetricCard(
              title = "Staff on Site",
              value = "${liveEvent.staffCheckedIn} / ${liveEvent.staffConfirmed}",
              subtitle = "97% Check-in Rate",
              icon = Icons.Default.Groups,
              accentColor = PrimaryIndigoLight,
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Live Attendees",
              value = "${liveEvent.attendeesCheckedIn}",
              subtitle = "Target: ${liveEvent.attendeesExpected}",
              icon = Icons.Default.CoPresent,
              accentColor = EmeraldSuccess,
              modifier = Modifier.weight(1f)
            )
          }
          UserRole.VENDOR -> {
            MetricCard(
              title = "Active RFQs",
              value = "12 Inbound",
              subtitle = "₹8.4L Quoted",
              icon = Icons.Default.RequestQuote,
              accentColor = AccentCyan,
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Equipment Deployed",
              value = "18 Units",
              subtitle = "3 Locations",
              icon = Icons.Default.Inventory2,
              accentColor = AccentAmber,
              modifier = Modifier.weight(1f)
            )
          }
          UserRole.CLIENT -> {
            MetricCard(
              title = "Active Events",
              value = "3 Summits",
              subtitle = "On Schedule",
              icon = Icons.Default.EventAvailable,
              accentColor = PrimaryIndigoLight,
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Budget Disbursed",
              value = "₹38.2L",
              subtitle = "94% Reconciled",
              icon = Icons.Default.AccountBalance,
              accentColor = EmeraldSuccess,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // LIVE EVENT COMMAND CENTER BANNER
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(PrimaryIndigo, AccentCyan))
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToControlRoom() }
          .testTag("live_control_room_banner")
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(150.dp)
          ) {
            AsyncImage(
              model = liveEvent.coverImageUrl,
              contentDescription = liveEvent.title,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(Color.Transparent, DarkSurfaceElevated.copy(alpha = 0.95f))
                  )
                )
            )
            Row(
              modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatusBadge(text = "LIVE CONTROL ROOM", color = CrimsonAlert)
              StatusBadge(text = liveEvent.areaZone, color = AccentCyan)
            }
          }

          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = liveEvent.title,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Text(
              text = "${liveEvent.venueName} • ${liveEvent.timeSlot}",
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Staff Deployment: ${liveEvent.staffCheckedIn}/${liveEvent.staffConfirmed}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = EmeraldSuccess
                )
                Text(
                  text = "Open Incidents: 0 Critical, 1 Investigating",
                  fontSize = 11.sp,
                  color = TextMutedDark
                )
              }
              Button(
                onClick = onNavigateToControlRoom,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("enter_control_room_btn")
              ) {
                Text("Command", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }

    // Quick Action Tools Grid
    item {
      Text(
        text = "OPERATIONAL APPS & TOOLS",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickToolCard(
          title = "Profile",
          desc = "Bio & Portfolio",
          icon = Icons.Default.Person,
          color = EmeraldSuccess,
          onClick = onNavigateToProfile,
          modifier = Modifier.weight(1f).testTag("quick_profile_btn")
        )
        QuickToolCard(
          title = "Marketplace",
          desc = "Browse Gigs",
          icon = Icons.Default.WorkOutline,
          color = AccentCyan,
          onClick = onNavigateToMarketplace,
          modifier = Modifier.weight(1f).testTag("quick_marketplace_btn")
        )
        QuickToolCard(
          title = "Passport ID",
          desc = "EVT-BLR",
          icon = Icons.Default.Badge,
          color = AccentAmber,
          onClick = onNavigateToPassport,
          modifier = Modifier.weight(1f).testTag("quick_passport_btn")
        )
        QuickToolCard(
          title = "AI Engine",
          desc = "Gemini Suite",
          icon = Icons.Default.Psychology,
          color = PrimaryIndigoLight,
          onClick = onNavigateToAI,
          modifier = Modifier.weight(1f).testTag("quick_ai_suite_btn")
        )
      }
    }

    // FIRESTORE LIVE EVENT GIGS SECTION
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(PrimaryIndigo.copy(alpha = 0.4f), AccentCyan.copy(alpha = 0.4f)))
        ),
        modifier = Modifier.fillMaxWidth().testTag("firestore_gigs_banner")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isFirestoreLive) EmeraldSuccess else AccentAmber)
            )
            Column {
              Text(
                text = "Cloud Firestore Database",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (isFirestoreLive) "Connected • Real-time Sync Active" else "Connecting to Firestore...",
                fontSize = 11.sp,
                color = if (isFirestoreLive) EmeraldSuccess else AccentAmber
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(
              onClick = onRefreshFirestore,
              modifier = Modifier.size(34.dp).testTag("refresh_firestore_btn")
            ) {
              if (isSyncing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
              } else {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Refresh from Firestore",
                  tint = PrimaryIndigoLight,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Button(
              onClick = { showPostGigDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.testTag("post_gig_btn")
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Post Gig", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "AVAILABLE EVENT GIGS (FIRESTORE)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Showing ${filteredAndSortedOpportunities.size} gigs • Stored in Cloud Firestore",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        TextButton(onClick = onNavigateToMarketplace) {
          Text("Marketplace Mode", color = PrimaryIndigoLight)
        }
      }
    }

    // SEARCH BAR FOR GIGS (Filter by Event Title or Description Text)
    item {
      OutlinedTextField(
        value = gigSearchQuery,
        onValueChange = { gigSearchQuery = it },
        placeholder = { Text("Search gigs by event title, description or role...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Gigs",
            tint = PrimaryIndigoLight
          )
        },
        trailingIcon = if (gigSearchQuery.isNotEmpty()) {
          {
            IconButton(
              onClick = { gigSearchQuery = "" },
              modifier = Modifier.testTag("clear_gig_search_btn")
            ) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Search")
            }
          }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryIndigoLight,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
          focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("gig_search_bar")
      )
    }

    // CATEGORY FILTER BAR (Music, Corporate, Weddings, Technical Support)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().testTag("category_filter_bar")
        ) {
          items(categories) { cat ->
            val isSelected = selectedCategoryFilter == cat
            val count = if (cat == "All") opportunities.size else opportunities.count { it.category.equals(cat, ignoreCase = true) }
            val catIcon = when (cat) {
              "Music" -> Icons.Default.MusicNote
              "Corporate" -> Icons.Default.BusinessCenter
              "Weddings" -> Icons.Default.Celebration
              "Technical Support" -> Icons.Default.Engineering
              else -> Icons.Default.Apps
            }

            FilterChip(
              selected = isSelected,
              onClick = { selectedCategoryFilter = cat },
              leadingIcon = {
                Icon(
                  imageVector = catIcon,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = if (isSelected) PrimaryIndigoLight else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              label = {
                Text(
                  text = "$cat ($count)",
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryIndigo.copy(alpha = 0.25f),
                selectedLabelColor = PrimaryIndigoLight
              ),
              modifier = Modifier.testTag("filter_cat_${cat.lowercase().replace(" ", "_")}")
            )
          }
        }

        // Sort Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Sort:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          sortOptions.forEach { opt ->
            val isSelected = selectedSortOption == opt
            AssistChip(
              onClick = { selectedSortOption = opt },
              label = {
                Text(
                  text = opt,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant
                )
              },
              colors = AssistChipDefaults.assistChipColors(
                containerColor = if (isSelected) AccentCyan.copy(alpha = 0.15f) else Color.Transparent
              ),
              border = BorderStroke(
                1.dp,
                if (isSelected) AccentCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
              ),
              modifier = Modifier.testTag("sort_opt_${opt.lowercase().replace(" ", "_")}")
            )
          }
        }
      }
    }

    // Firestore Gigs List
    if (filteredAndSortedOpportunities.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.WorkOff, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(36.dp))
            Text(
              text = if (gigSearchQuery.isNotBlank()) "No gigs matching \"$gigSearchQuery\"" else "No gigs in '$selectedCategoryFilter' found.",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (gigSearchQuery.isNotBlank()) "Try another keyword, clear the search bar, or choose 'All' categories." else "Try clearing filters or click 'Post Gig' above to add one to Firestore.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              if (gigSearchQuery.isNotBlank()) {
                OutlinedButton(
                  onClick = { gigSearchQuery = "" },
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Clear Search", fontSize = 12.sp)
                }
              }
              Button(
                onClick = {
                  selectedCategoryFilter = "All"
                  gigSearchQuery = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Show All Gigs", fontSize = 12.sp)
              }
            }
          }
        }
      }
    } else {
      items(filteredAndSortedOpportunities) { opp ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedGigForDetails = opp }
            .testTag("opp_card_${opp.id}")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(text = "${opp.matchScore}% MATCH", color = PrimaryIndigoLight)
                StatusBadge(text = opp.category, color = AccentAmber)
                StatusBadge(text = opp.areaZone, color = AccentCyan)
              }
              Text(
                text = "₹${opp.payRateINR}/day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldSuccess
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = opp.role,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${opp.eventTitle} • Client: ${opp.clientName}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (opp.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = opp.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 2
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Wage Breakdown
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Gross: ₹${opp.grossAmountINR}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Escrow Fee: ₹${opp.platformFeeINR}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Net Bank: ₹${opp.netAmountINR}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Shift: ${opp.timeWindow} • ${opp.spotsAvailable - opp.spotsFilled} spots left",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                  onClick = { selectedGigForDetails = opp },
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.testTag("details_gig_${opp.id}")
                ) {
                  Text("Details", fontSize = 11.sp)
                }

                if (opp.isBooked) {
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(text = "BOOKED ✓", color = EmeraldSuccess)
                    Button(
                      onClick = {
                        gigToReview = opp
                        reviewText = ""
                        reviewRating = 5.0f
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("rate_review_card_btn_${opp.id}")
                    ) {
                      Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = DarkSurfaceBase, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Review", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkSurfaceBase)
                    }
                  }
                } else {
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                      onClick = {
                        gigToReview = opp
                        reviewText = ""
                        reviewRating = 5.0f
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AccentAmber.copy(alpha = 0.2f)),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("organizer_rate_btn_${opp.id}")
                    ) {
                      Text("Rate Pro", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                    }
                    Button(
                      onClick = {
                        onBookGig(opp)
                        latestConfirmedBookingRef = "EVT-BKG-${(1000..9999).random()}"
                        Toast.makeText(context, "Gig Booked! Stored in Firestore 'bookings' collection.", Toast.LENGTH_SHORT).show()
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("book_gig_card_btn_${opp.id}")
                    ) {
                      Text("Book Gig", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Dialog to Post a New Event Gig directly to Cloud Firestore
  if (showPostGigDialog) {
    AlertDialog(
      onDismissRequest = { showPostGigDialog = false },
      title = { Text("Post New Gig to Firestore", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Store a new Bangalore event gig into Cloud Firestore in real time:")
          OutlinedTextField(
            value = newGigRole,
            onValueChange = { newGigRole = it },
            label = { Text("Role Title") },
            placeholder = { Text("e.g. VIP Protocol Usher / Stage Technician") },
            modifier = Modifier.fillMaxWidth().testTag("new_gig_role_input")
          )
          OutlinedTextField(
            value = newGigEvent,
            onValueChange = { newGigEvent = it },
            label = { Text("Event Name") },
            placeholder = { Text("e.g. Comic Con Bengaluru 2026") },
            modifier = Modifier.fillMaxWidth().testTag("new_gig_event_input")
          )
          OutlinedTextField(
            value = newGigDescription,
            onValueChange = { newGigDescription = it },
            label = { Text("Event & Role Description") },
            placeholder = { Text("Brief scope of work, tasks, and event context...") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("new_gig_desc_input")
          )
          OutlinedTextField(
            value = newGigZone,
            onValueChange = { newGigZone = it },
            label = { Text("Bangalore Area Zone") },
            placeholder = { Text("e.g. Whitefield / Koramangala / Indiranagar") },
            modifier = Modifier.fillMaxWidth()
          )
          Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val gigCats = listOf("Music", "Corporate", "Weddings", "Technical Support")
            items(gigCats) { cat ->
              FilterChip(
                selected = newGigCategory == cat,
                onClick = { newGigCategory = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newGigPay,
              onValueChange = { newGigPay = it },
              label = { Text("Daily Pay (₹)") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newGigHours,
              onValueChange = { newGigHours = it },
              label = { Text("Shift Time") },
              modifier = Modifier.weight(1f)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newGigRole.isNotBlank() && newGigEvent.isNotBlank()) {
              val pay = newGigPay.toIntOrNull() ?: 2800
              val fee = (pay * 0.05).toInt()
              val net = pay - fee
              val newOpp = StaffOpportunity(
                id = "opp_${System.currentTimeMillis()}",
                eventId = "evt_001",
                eventTitle = newGigEvent.trim(),
                role = newGigRole.trim(),
                areaZone = newGigZone.trim().ifEmpty { "Koramangala" },
                date = "Upcoming Weekend",
                timeWindow = newGigHours.trim().ifEmpty { "09:00 - 18:00" },
                payRateINR = pay,
                grossAmountINR = pay,
                netAmountINR = net,
                platformFeeINR = fee,
                spotsAvailable = 6,
                spotsFilled = 0,
                skillsRequired = listOf("Customer Service", "Punctuality", "English / Kannada"),
                dressCode = "Smart Casual / Armband",
                matchScore = 95,
                matchReasons = listOf("New Opportunity in Bangalore", "Instant Escrow Funded"),
                clientName = "Aditya Mehra Productions",
                urgencyTag = "New Listing",
                category = newGigCategory,
                description = newGigDescription.trim().ifEmpty { "Event operational staff gig for ${newGigEvent.trim()} in Bangalore." }
              )
              onPostNewGig(newOpp)
              Toast.makeText(context, "Gig stored in Cloud Firestore successfully!", Toast.LENGTH_SHORT).show()
              newGigRole = ""
              newGigEvent = ""
              newGigDescription = ""
              showPostGigDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("submit_new_gig_btn")
        ) {
          Text("Publish to Firestore")
        }
      },
      dismissButton = {
        TextButton(onClick = { showPostGigDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // GIG DETAIL PAGE MODAL WITH 'BOOK GIG' ACTION SAVING TO FIRESTORE 'bookings'
  selectedGigForDetails?.let { opp ->
    AlertDialog(
      onDismissRequest = { selectedGigForDetails = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(opp.role, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
          StatusBadge(text = opp.category, color = AccentAmber)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "${opp.eventTitle} • ${opp.clientName}",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
            Text(text = "${opp.areaZone}, Bengaluru", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = PrimaryIndigoLight, modifier = Modifier.size(16.dp))
            Text(text = "${opp.date} • ${opp.timeWindow}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          if (opp.description.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "EVENT & ROLE BRIEF",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryIndigoLight,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = opp.description,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

          // Financial Escrow Transparency
          Text("WAGE & ESCROW BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Gross Shift Rate:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${opp.grossAmountINR}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Platform & Escrow Fee (5%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("- ₹${opp.platformFeeINR}", fontSize = 12.sp, color = CrimsonAlert)
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Deposit to Bank:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("₹${opp.netAmountINR}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
              }
            }
          }

          Text("Dress Code: ${opp.dressCode}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

          Text("Required Skills:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            opp.skillsRequired.forEach { skill ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.surface)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(skill, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      },
      confirmButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              gigToReview = opp
              reviewText = ""
              reviewRating = 5.0f
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("modal_rate_review_btn")
          ) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = DarkSurfaceBase, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Review Pro", color = DarkSurfaceBase, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          if (opp.isBooked) {
            Button(
              onClick = { selectedGigForDetails = null },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
              Text("Booked ✓")
            }
          } else {
            Button(
              onClick = {
                onBookGig(opp)
                val ref = "EVT-BKG-${(1000..9999).random()}"
                latestConfirmedBookingRef = ref
                selectedGigForDetails = null
                Toast.makeText(context, "Booking request saved to Firestore 'bookings'!", Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              modifier = Modifier.testTag("book_gig_btn")
            ) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Book Gig")
            }
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedGigForDetails = null }) {
          Text("Close")
        }
      }
    )
  }

  // BOOKING CONFIRMATION DIALOG
  latestConfirmedBookingRef?.let { bookingRef ->
    AlertDialog(
      onDismissRequest = { latestConfirmedBookingRef = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = EmeraldSuccess)
          Text("Gig Booking Confirmed!", fontWeight = FontWeight.ExtraBold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Your booking has been permanently recorded in the Firestore 'bookings' collection.")
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Booking Reference:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(bookingRef, fontSize = 15.sp, fontWeight = FontWeight.Black, color = PrimaryIndigoLight)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Escrow Protection: SECURED (100% Funds Held)", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
              Text("Attendance Pass: Ready for QR Field Scan", fontSize = 11.sp, color = AccentCyan)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { latestConfirmedBookingRef = null },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("dismiss_booking_confirm_btn")
        ) {
          Text("Got It")
        }
      }
    )
  }

  // ORGANIZER RATING & REVIEW MODAL FOR PROFESSIONAL AFTER GIG
  gigToReview?.let { gig ->
    AlertDialog(
      onDismissRequest = { gigToReview = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = AccentAmber)
          Text("Leave Review for Pro", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Rate professional performance for \"${gig.role}\" at \"${gig.eventTitle}\". Data is saved to the Firestore 'reviews' collection.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = reviewOrganizerName,
            onValueChange = { reviewOrganizerName = it },
            label = { Text("Organizer / Reviewer Name") },
            modifier = Modifier.fillMaxWidth().testTag("home_review_org_name_input")
          )

          OutlinedTextField(
            value = reviewOrganizerOrg,
            onValueChange = { reviewOrganizerOrg = it },
            label = { Text("Organizer Organization / Client") },
            modifier = Modifier.fillMaxWidth().testTag("home_review_org_org_input")
          )

          // Rating Stars
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Rating: ${reviewRating.toInt()} / 5 Stars",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AccentAmber
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf(1f, 2f, 3f, 4f, 5f).forEach { star ->
                IconButton(
                  onClick = { reviewRating = star },
                  modifier = Modifier.size(36.dp).testTag("home_star_${star.toInt()}")
                ) {
                  Icon(
                    imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "$star stars",
                    tint = AccentAmber,
                    modifier = Modifier.size(28.dp)
                  )
                }
              }
            }
          }

          // Sub attributes
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Punctuality", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Row {
                listOf(4f, 5f).forEach { s ->
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (s <= reviewPunctual) AccentCyan else TextMutedDark,
                    modifier = Modifier.size(16.dp).clickable { reviewPunctual = s }
                  )
                }
              }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Technical", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Row {
                listOf(4f, 5f).forEach { s ->
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (s <= reviewTechnical) EmeraldSuccess else TextMutedDark,
                    modifier = Modifier.size(16.dp).clickable { reviewTechnical = s }
                  )
                }
              }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Teamwork", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Row {
                listOf(4f, 5f).forEach { s ->
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (s <= reviewTeamwork) AccentAmber else TextMutedDark,
                    modifier = Modifier.size(16.dp).clickable { reviewTeamwork = s }
                  )
                }
              }
            }
          }

          OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Performance Review & Feedback") },
            placeholder = { Text("e.g. Excellent operational leadership, on time, highly recommended!") },
            minLines = 3,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth().testTag("home_review_text_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reviewText.isNotBlank()) {
              onLeaveReview(
                gig.id,
                gig.eventTitle,
                gig.role,
                reviewRating,
                reviewText.trim(),
                reviewOrganizerName.trim(),
                reviewOrganizerOrg.trim(),
                reviewPunctual,
                reviewTechnical,
                reviewTeamwork
              )
              gigToReview = null
              reviewText = ""
              Toast.makeText(context, "Review saved to Firestore 'reviews' collection!", Toast.LENGTH_LONG).show()
            } else {
              Toast.makeText(context, "Please write feedback before submitting.", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
          modifier = Modifier.testTag("submit_home_review_btn")
        ) {
          Text("Submit Review", color = DarkSurfaceBase, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { gigToReview = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun QuickToolCard(
  title: String,
  desc: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.4f), Color.Transparent)))
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
      Text(text = desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
