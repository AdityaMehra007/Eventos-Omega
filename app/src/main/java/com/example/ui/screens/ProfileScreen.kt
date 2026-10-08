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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EventosUser
import com.example.data.model.GigReview
import com.example.data.model.PortfolioItem
import com.example.data.model.StaffBooking
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  user: EventosUser,
  reviews: List<GigReview> = emptyList(),
  bookings: List<StaffBooking> = emptyList(),
  onSaveProfile: (bio: String, skills: List<String>, portfolio: List<PortfolioItem>, title: String, zone: String, rate: Int) -> Unit,
  onAddPortfolioItem: (PortfolioItem) -> Unit,
  onRemovePortfolioItem: (String) -> Unit,
  onLeaveReview: (gigId: String, eventTitle: String, role: String, rating: Float, reviewText: String, orgName: String, orgOrg: String, punctual: Float, tech: Float, team: Float) -> Unit = { _, _, _, _, _, _, _, _, _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  var isEditingBio by remember { mutableStateOf(false) }
  var editedBio by remember(user.bio) { mutableStateOf(user.bio) }
  var editedTitle by remember(user.title) { mutableStateOf(user.title) }
  var editedZone by remember(user.primaryZone) { mutableStateOf(user.primaryZone) }
  var editedRate by remember(user.hourlyRateINR) { mutableStateOf(user.hourlyRateINR.toString()) }

  // Bookings filter state: "All", "Active", "Completed"
  var bookingStatusFilter by remember { mutableStateOf("All") }
  var selectedBookingDetail by remember { mutableStateOf<StaffBooking?>(null) }

  // Skills management
  var currentSkills by remember(user.skills) { mutableStateOf(user.skills) }
  var showAddSkillDialog by remember { mutableStateOf(false) }
  var newSkillInput by remember { mutableStateOf("") }

  // Portfolio dialog state
  var showAddPortfolioDialog by remember { mutableStateOf(false) }
  var newPortTitle by remember { mutableStateOf("") }
  var newPortCategory by remember { mutableStateOf("Technical Support") }
  var newPortEvent by remember { mutableStateOf("") }
  var newPortYear by remember { mutableStateOf("2026") }
  var newPortRole by remember { mutableStateOf("Lead Technical Production") }
  var newPortDesc by remember { mutableStateOf("") }
  var newPortMetrics by remember { mutableStateOf("") }
  var newPortMediaUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=600&auto=format&fit=crop&q=80") }

  // Selected portfolio item for detail modal
  var selectedPortfolioDetail by remember { mutableStateOf<PortfolioItem?>(null) }

  // Organizer Rating & Review Dialog State
  var showLeaveReviewDialog by remember { mutableStateOf(false) }
  var reviewEventTitle by remember { mutableStateOf("Bengaluru Tech Summit 2026") }
  var reviewGigId by remember { mutableStateOf("opp_001") }
  var reviewRole by remember { mutableStateOf("Lead Stage & Sound Supervisor") }
  var reviewRating by remember { mutableFloatStateOf(5.0f) }
  var reviewText by remember { mutableStateOf("") }
  var reviewOrganizerName by remember { mutableStateOf("Ramesh Narayan") }
  var reviewOrganizerOrg by remember { mutableStateOf("Bengaluru Events & Summits Guild") }
  var reviewPunctual by remember { mutableFloatStateOf(5.0f) }
  var reviewTechnical by remember { mutableFloatStateOf(5.0f) }
  var reviewTeamwork by remember { mutableFloatStateOf(5.0f) }

  val portfolioCategories = listOf("Technical Support", "Music", "Corporate", "Weddings", "Production")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Top Hero Card: Identity & Verification
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(PrimaryIndigo, AccentCyan))
        ),
        modifier = Modifier.fillMaxWidth().testTag("profile_hero_card")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(72.dp)) {
                AsyncImage(
                  model = user.avatarUrl,
                  contentDescription = user.name,
                  modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, PrimaryIndigoLight, CircleShape),
                  contentScale = ContentScale.Crop
                )
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(EmeraldSuccess)
                    .align(Alignment.BottomEnd)
                    .border(2.dp, DarkSurfaceElevated, CircleShape)
                )
              }

              Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                  )
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Identity",
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Text(
                  text = user.title,
                  style = MaterialTheme.typography.bodyMedium,
                  color = PrimaryIndigoLight,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${user.eventosId} • ${user.city}, KA",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextMutedDark
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AccentAmber.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = user.role.badge,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = AccentAmber
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(14.dp))

          // Key verified stats
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("RATING", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.rating} ★", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AccentAmber)
              Text("${user.reviewCount} reviews", fontSize = 10.sp, color = TextSecondaryDark)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("LOGGED HOURS", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.verifiedHours} hrs", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
              Text("On-Site Ground", fontSize = 10.sp, color = TextSecondaryDark)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("EVENTS PRODUCED", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.completedEvents}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryIndigoLight)
              Text("Bangalore Venues", fontSize = 10.sp, color = TextSecondaryDark)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("DAILY ESCROW", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("₹${user.hourlyRateINR}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
              Text("Direct Bank", fontSize = 10.sp, color = TextSecondaryDark)
            }
          }
        }
      }
    }

    // CLOUD FIRESTORE PERSISTENCE BANNER
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("profile_firestore_banner")
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
                .background(EmeraldSuccess)
            )
            Column {
              Text(
                text = "Firestore 'users' Collection",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Changes persist to cloud: /users/${user.id}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Button(
            onClick = {
              val rateInt = editedRate.toIntOrNull() ?: user.hourlyRateINR
              onSaveProfile(editedBio, currentSkills, user.portfolio, editedTitle, editedZone, rateInt)
              isEditingBio = false
              Toast.makeText(context, "Profile bio & settings synced to Firestore 'users' collection!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("save_profile_firestore_btn")
          ) {
            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save to Cloud", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // SECTION 1: PROFESSIONAL BIO & CREDENTIALS
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("profile_bio_card")
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(imageVector = Icons.Default.AccountBox, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
              Text(
                text = "PROFESSIONAL BIO & TITLE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            IconButton(
              onClick = { isEditingBio = !isEditingBio },
              modifier = Modifier.size(32.dp).testTag("toggle_edit_bio_btn")
            ) {
              Icon(
                imageVector = if (isEditingBio) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = "Edit Bio",
                tint = PrimaryIndigoLight
              )
            }
          }

          if (isEditingBio) {
            OutlinedTextField(
              value = editedTitle,
              onValueChange = { editedTitle = it },
              label = { Text("Professional Title") },
              modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
            )
            OutlinedTextField(
              value = editedBio,
              onValueChange = { editedBio = it },
              label = { Text("Professional Bio (Executive Summary)") },
              modifier = Modifier.fillMaxWidth().testTag("edit_bio_input"),
              minLines = 3,
              maxLines = 6
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = editedZone,
                onValueChange = { editedZone = it },
                label = { Text("Base Zone (e.g. Koramangala)") },
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = editedRate,
                onValueChange = { editedRate = it },
                label = { Text("Day Rate (₹)") },
                modifier = Modifier.weight(1f)
              )
            }
            Button(
              onClick = {
                val rateInt = editedRate.toIntOrNull() ?: user.hourlyRateINR
                onSaveProfile(editedBio, currentSkills, user.portfolio, editedTitle, editedZone, rateInt)
                isEditingBio = false
                Toast.makeText(context, "Saved to Firestore 'users' collection!", Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.align(Alignment.End).testTag("save_bio_btn")
            ) {
              Text("Save Changes")
            }
          } else {
            Text(
              text = user.bio,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 22.sp
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatusBadge(text = "Zone: ${user.primaryZone}", color = PrimaryIndigoLight)
              StatusBadge(text = "Standard Day Rate: ₹${user.hourlyRateINR}", color = EmeraldSuccess)
              StatusBadge(text = "Base: Bengaluru", color = AccentCyan)
            }
          }
        }
      }
    }

    // SECTION 2: VERIFIED SKILLS MANAGEMENT
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("profile_skills_card")
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(20.dp))
              Text(
                text = "SKILLS & EXPERTISE (${currentSkills.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            IconButton(
              onClick = { showAddSkillDialog = true },
              modifier = Modifier.size(32.dp).testTag("add_skill_btn")
            ) {
              Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Skill",
                tint = PrimaryIndigoLight
              )
            }
          }

          Text(
            text = "Skills verified by client reviews, supervisor sign-offs, and venue credentials:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Skills Flow Chips
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentSkills.chunked(2).forEach { pair ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                pair.forEach { skill ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f).testTag("skill_chip_${skill.lowercase().replace(" ", "_")}")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                      ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                        Text(
                          text = skill,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                      }
                      IconButton(
                        onClick = {
                          val updated = currentSkills.filter { it != skill }
                          currentSkills = updated
                          val rateInt = editedRate.toIntOrNull() ?: user.hourlyRateINR
                          onSaveProfile(editedBio, updated, user.portfolio, editedTitle, editedZone, rateInt)
                          Toast.makeText(context, "Skill removed & updated in Firestore!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(20.dp)
                      ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Skill", tint = TextMutedDark, modifier = Modifier.size(12.dp))
                      }
                    }
                  }
                }
                if (pair.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }

          // Suggested quick-add tags
          val suggestedSkills = listOf("Live Streaming", "Pyrotechnics Safety", "Rigging Certified", "Kannada & Hindi Fluency")
            .filter { !currentSkills.contains(it) }

          if (suggestedSkills.isNotEmpty()) {
            Text("SUGGESTED ADDITIONS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigoLight)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(suggestedSkills) { sug ->
                AssistChip(
                  onClick = {
                    val updated = currentSkills + sug
                    currentSkills = updated
                    val rateInt = editedRate.toIntOrNull() ?: user.hourlyRateINR
                    onSaveProfile(editedBio, updated, user.portfolio, editedTitle, editedZone, rateInt)
                    Toast.makeText(context, "Added '$sug' to Firestore profile!", Toast.LENGTH_SHORT).show()
                  },
                  label = { Text("+ $sug", fontSize = 11.sp) },
                  colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
                )
              }
            }
          }
        }
      }
    }

    // SECTION 3: PORTFOLIO SHOWCASE (STORED IN FIRESTORE 'users')
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "PORTFOLIO & TRACK RECORD",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${user.portfolio.size} major productions recorded in Cloud Firestore",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showAddPortfolioDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_portfolio_item_btn")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // PORTFOLIO ITEMS LIST
    if (user.portfolio.isEmpty()) {
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
            Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(36.dp))
            Text("No Portfolio Projects Yet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Showcase your past events, festivals, and stage setups to attract top clients.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
              onClick = { showAddPortfolioDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Add Your First Project", fontSize = 12.sp)
            }
          }
        }
      }
    } else {
      items(user.portfolio) { item ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedPortfolioDetail = item }
            .testTag("portfolio_card_${item.id}")
        ) {
          Column {
            if (item.mediaUrl.isNotBlank()) {
              Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                AsyncImage(
                  model = item.mediaUrl,
                  contentDescription = item.title,
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(listOf(Color.Transparent, DarkSurfaceElevated.copy(alpha = 0.85f)))
                    )
                )
                Row(
                  modifier = Modifier.padding(10.dp).align(Alignment.TopStart),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  StatusBadge(text = item.category, color = AccentCyan)
                  StatusBadge(text = item.year, color = AccentAmber)
                }
              }
            }

            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = item.title,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                  onClick = {
                    onRemovePortfolioItem(item.id)
                    Toast.makeText(context, "Removed project from Firestore!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(24.dp).testTag("delete_portfolio_${item.id}")
                ) {
                  Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CrimsonAlert, modifier = Modifier.size(16.dp))
                }
              }

              Text(
                text = "${item.eventName} • ${item.role}",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryIndigoLight,
                fontWeight = FontWeight.SemiBold
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
              )

              if (item.metrics.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surface,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                    Text(text = item.metrics, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                  }
                }
              }
            }
          }
        }
      }
    }

    // SECTION 3: MY BOOKINGS (FIRESTORE 'bookings' COLLECTION)
    // Filter bookings for the logged-in user: active, completed, or all
    val userBookings = bookings.filter { it.workerId == user.id || it.workerName == user.name }
    val filteredBookings = userBookings.filter { bkg ->
      when (bookingStatusFilter) {
        "Active" -> bkg.status.contains("ACTIVE", ignoreCase = true) || bkg.status.contains("CONFIRMED", ignoreCase = true)
        "Completed" -> bkg.status.contains("COMPLETED", ignoreCase = true)
        else -> true
      }
    }

    item {
      Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "MY BOOKINGS (FIRESTORE)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = AccentCyan.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "${userBookings.size} Total",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentCyan,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Live query from Firestore 'bookings' for ${user.name}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Filter Chips (All, Active, Completed)
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("All", "Active", "Completed").forEach { statusOption ->
              val isSelected = bookingStatusFilter == statusOption
              AssistChip(
                onClick = { bookingStatusFilter = statusOption },
                label = {
                  Text(
                    text = statusOption,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                },
                colors = AssistChipDefaults.assistChipColors(
                  containerColor = if (isSelected) AccentCyan.copy(alpha = 0.2f) else Color.Transparent
                ),
                border = BorderStroke(
                  1.dp,
                  if (isSelected) AccentCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("filter_booking_${statusOption.lowercase()}")
              )
            }
          }
        }
      }
    }

    // LIST OF BOOKINGS FROM FIRESTORE
    if (filteredBookings.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(36.dp))
            Text(
              text = if (bookingStatusFilter == "All") "No Bookings Found in Cloud" else "No $bookingStatusFilter Bookings",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Book gigs on the Home or Marketplace screen to track them in real time.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(filteredBookings) { booking ->
        val isActive = booking.status.contains("ACTIVE", ignoreCase = true) || booking.status.contains("CONFIRMED", ignoreCase = true)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = BorderStroke(1.dp, if (isActive) AccentCyan.copy(alpha = 0.35f) else EmeraldSuccess.copy(alpha = 0.35f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { selectedBookingDetail = booking }
            .testTag("booking_card_${booking.id}")
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: Reference + Status
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, tint = PrimaryIndigoLight, modifier = Modifier.size(16.dp))
                Text(
                  text = booking.bookingReference,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = PrimaryIndigoLight
                )
              }

              StatusBadge(
                text = if (isActive) "ACTIVE / BOOKED" else "COMPLETED ✓",
                color = if (isActive) AccentCyan else EmeraldSuccess
              )
            }

            // Event Title & Role
            Text(
              text = booking.eventTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Text(
              text = "${booking.role} • Client: ${booking.clientName}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )

            // Shift Window, Zone & Date
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(14.dp))
                Text(
                  text = "${booking.date} • ${booking.timeWindow}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(14.dp))
                Text(
                  text = booking.areaZone,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Financial Payout Row
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(
                    imageVector = if (isActive) Icons.Default.Lock else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isActive) AccentAmber else EmeraldSuccess,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = booking.escrowStatus,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) AccentAmber else EmeraldSuccess
                  )
                }

                Text(
                  text = "₹${booking.netAmountINR} Net Payout",
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = EmeraldSuccess
                )
              }
            }

            // Footer info
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Collection: firestore /bookings/${booking.id}",
                fontSize = 10.sp,
                color = TextMutedDark
              )
              TextButton(
                onClick = { selectedBookingDetail = booking },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.testTag("view_booking_details_${booking.id}")
              ) {
                Text("View Details", fontSize = 11.sp, color = PrimaryIndigoLight)
              }
            }
          }
        }
      }
    }

    // SECTION 4: COMPLETED GIG REVIEWS & ORGANIZER RATINGS (FIRESTORE 'reviews' COLLECTION)
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "CLIENT & ORGANIZER REVIEWS",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = AccentAmber.copy(alpha = 0.15f),
              modifier = Modifier.padding(start = 4.dp)
            ) {
              Text(
                text = "${reviews.size} Verified",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentAmber,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Post-gig verified reviews stored in Firestore 'reviews' collection",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showLeaveReviewDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("leave_review_organizer_btn")
        ) {
          Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = DarkSurfaceBase, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Rate & Review", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkSurfaceBase)
        }
      }
    }

    // REVIEWS LIST
    if (reviews.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.StarOutline, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(36.dp))
            Text("No Gig Reviews Yet", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Event organizers can leave reviews and ratings here after completed gigs.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
              onClick = { showLeaveReviewDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Leave Organizer Review", fontSize = 12.sp, color = DarkSurfaceBase, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(reviews) { rev ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.25f)),
          modifier = Modifier.fillMaxWidth().testTag("review_card_${rev.id}")
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Rating + Event
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                  repeat(5) { starIndex ->
                    val isFilled = starIndex < rev.rating.toInt()
                    Icon(
                      imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                      contentDescription = null,
                      tint = AccentAmber,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
                Text(
                  text = "${rev.rating} ★",
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp,
                  color = AccentAmber
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldSuccess.copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
                  Text("Completed Gig", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                }
              }
            }

            Text(
              text = rev.eventTitle,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Role: ${rev.roleExecuted} • Date: ${rev.formattedDate}",
              fontSize = 12.sp,
              color = PrimaryIndigoLight,
              fontWeight = FontWeight.SemiBold
            )

            // Review text
            Text(
              text = "\"${rev.reviewText}\"",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 18.sp
            )

            // Sub-scores row
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Punctuality: ${rev.punctualScore} ★", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                Text("Technical Skill: ${rev.technicalCompetenceScore} ★", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                Text("Teamwork: ${rev.teamworkScore} ★", fontSize = 11.sp, color = AccentAmber, fontWeight = FontWeight.Bold)
              }
            }

            // Organizer Author details
            Row(
              modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = PrimaryIndigoLight, modifier = Modifier.size(18.dp))
                Column {
                  Text(
                    text = rev.organizerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = rev.organizerOrganization,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Text(
                text = "Firestore /reviews",
                fontSize = 10.sp,
                color = TextMutedDark
              )
            }
          }
        }
      }
    }
  }

  // DIALOG TO ADD NEW SKILL
  if (showAddSkillDialog) {
    AlertDialog(
      onDismissRequest = { showAddSkillDialog = false },
      title = { Text("Add New Skill to Profile", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Enter a technical, operational or artistic skill to save in your Firestore profile:")
          OutlinedTextField(
            value = newSkillInput,
            onValueChange = { newSkillInput = it },
            label = { Text("Skill Name") },
            placeholder = { Text("e.g. DiGiCo Console / Stage Pyrotechnics") },
            modifier = Modifier.fillMaxWidth().testTag("new_skill_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newSkillInput.isNotBlank()) {
              val updated = currentSkills + newSkillInput.trim()
              currentSkills = updated
              val rateInt = editedRate.toIntOrNull() ?: user.hourlyRateINR
              onSaveProfile(editedBio, updated, user.portfolio, editedTitle, editedZone, rateInt)
              newSkillInput = ""
              showAddSkillDialog = false
              Toast.makeText(context, "New skill synced to Firestore 'users' collection!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("submit_new_skill_btn")
        ) {
          Text("Add Skill")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddSkillDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // DIALOG TO ADD NEW PORTFOLIO PROJECT
  if (showAddPortfolioDialog) {
    AlertDialog(
      onDismissRequest = { showAddPortfolioDialog = false },
      title = { Text("Add Portfolio Project to Firestore", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newPortTitle,
            onValueChange = { newPortTitle = it },
            label = { Text("Project Title") },
            placeholder = { Text("e.g. Bangalore Arena Main Rigging") },
            modifier = Modifier.fillMaxWidth().testTag("new_port_title_input")
          )
          OutlinedTextField(
            value = newPortEvent,
            onValueChange = { newPortEvent = it },
            label = { Text("Event Name") },
            placeholder = { Text("e.g. Echoes of Earth 2026") },
            modifier = Modifier.fillMaxWidth().testTag("new_port_event_input")
          )
          Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(portfolioCategories) { cat ->
              FilterChip(
                selected = newPortCategory == cat,
                onClick = { newPortCategory = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newPortYear,
              onValueChange = { newPortYear = it },
              label = { Text("Year") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newPortRole,
              onValueChange = { newPortRole = it },
              label = { Text("Your Role") },
              modifier = Modifier.weight(2f)
            )
          }
          OutlinedTextField(
            value = newPortDesc,
            onValueChange = { newPortDesc = it },
            label = { Text("Scope & Highlights") },
            placeholder = { Text("e.g. Coordinated 40 tops line array, managed sound delays...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )
          OutlinedTextField(
            value = newPortMetrics,
            onValueChange = { newPortMetrics = it },
            label = { Text("Key Metrics / Impact") },
            placeholder = { Text("e.g. 8,000 attendees • 100% On-time schedule") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newPortTitle.isNotBlank() && newPortEvent.isNotBlank()) {
              val newPortItem = PortfolioItem(
                id = "port_${System.currentTimeMillis()}",
                title = newPortTitle.trim(),
                category = newPortCategory,
                eventName = newPortEvent.trim(),
                year = newPortYear.trim().ifEmpty { "2026" },
                role = newPortRole.trim().ifEmpty { "Technical Production" },
                description = newPortDesc.trim().ifEmpty { "High-stakes production execution in Bangalore." },
                metrics = newPortMetrics.trim().ifEmpty { "100% Execution Success" },
                mediaUrl = newPortMediaUrl
              )
              onAddPortfolioItem(newPortItem)
              showAddPortfolioDialog = false
              newPortTitle = ""
              newPortEvent = ""
              newPortDesc = ""
              newPortMetrics = ""
              Toast.makeText(context, "Portfolio project written to Firestore 'users' collection!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("submit_portfolio_btn")
        ) {
          Text("Publish Project")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddPortfolioDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // PORTFOLIO ITEM DETAILS MODAL
  selectedPortfolioDetail?.let { item ->
    AlertDialog(
      onDismissRequest = { selectedPortfolioDetail = null },
      title = { Text(item.title, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (item.mediaUrl.isNotBlank()) {
            AsyncImage(
              model = item.mediaUrl,
              contentDescription = item.title,
              modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Crop
            )
          }
          Text("Event: ${item.eventName} (${item.year})", fontWeight = FontWeight.SemiBold)
          Text("Role: ${item.role}")
          Text("Category: ${item.category}", color = AccentCyan)
          HorizontalDivider()
          Text("Description:", fontWeight = FontWeight.SemiBold)
          Text(item.description, fontSize = 13.sp)
          if (item.metrics.isNotBlank()) {
            Text("Verified Metrics: ${item.metrics}", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedPortfolioDetail = null },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
        ) {
          Text("Close")
        }
      }
    )
  }

  // DIALOG FOR ORGANIZER TO LEAVE RATING & REVIEW FOR PROFESSIONAL (SAVED TO FIRESTORE 'reviews')
  if (showLeaveReviewDialog) {
    AlertDialog(
      onDismissRequest = { showLeaveReviewDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = AccentAmber)
          Text("Leave Organizer Review", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Rate and review ${user.name} after completed event gig. Data persists to Firestore 'reviews' collection.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = reviewEventTitle,
            onValueChange = { reviewEventTitle = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth().testTag("review_event_name_input")
          )

          OutlinedTextField(
            value = reviewRole,
            onValueChange = { reviewRole = it },
            label = { Text("Role Executed by Professional") },
            modifier = Modifier.fillMaxWidth().testTag("review_role_input")
          )

          OutlinedTextField(
            value = reviewOrganizerName,
            onValueChange = { reviewOrganizerName = it },
            label = { Text("Organizer Name") },
            modifier = Modifier.fillMaxWidth().testTag("review_organizer_name_input")
          )

          OutlinedTextField(
            value = reviewOrganizerOrg,
            onValueChange = { reviewOrganizerOrg = it },
            label = { Text("Organization / Production House") },
            modifier = Modifier.fillMaxWidth().testTag("review_organizer_org_input")
          )

          // Overall Rating Stars Selector
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Overall Rating: ${reviewRating.toInt()} / 5 Stars",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AccentAmber
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf(1f, 2f, 3f, 4f, 5f).forEach { star ->
                IconButton(
                  onClick = { reviewRating = star },
                  modifier = Modifier.size(36.dp).testTag("rating_star_${star.toInt()}")
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

          // Sub-attributes Rating Row (Punctuality, Technical, Teamwork)
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
            label = { Text("Organizer Feedback & Review Details") },
            placeholder = { Text("Describe on-site performance, execution quality, and reliability...") },
            minLines = 3,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth().testTag("review_text_input")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reviewText.isNotBlank()) {
              onLeaveReview(
                reviewGigId,
                reviewEventTitle.trim(),
                reviewRole.trim(),
                reviewRating,
                reviewText.trim(),
                reviewOrganizerName.trim(),
                reviewOrganizerOrg.trim(),
                reviewPunctual,
                reviewTechnical,
                reviewTeamwork
              )
              showLeaveReviewDialog = false
              reviewText = ""
              Toast.makeText(context, "Review & rating permanently saved to Firestore 'reviews'!", Toast.LENGTH_LONG).show()
            } else {
              Toast.makeText(context, "Please write feedback text before submitting.", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
          modifier = Modifier.testTag("submit_review_dialog_btn")
        ) {
          Text("Submit Review", color = DarkSurfaceBase, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLeaveReviewDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // BOOKING DETAILS DIALOG MODAL (FIRESTORE 'bookings' COLLECTION DETAIL)
  selectedBookingDetail?.let { bkg ->
    val isActive = bkg.status.contains("ACTIVE", ignoreCase = true) || bkg.status.contains("CONFIRMED", ignoreCase = true)
    AlertDialog(
      onDismissRequest = { selectedBookingDetail = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(bkg.role, fontWeight = FontWeight.Bold)
          StatusBadge(
            text = if (isActive) "ACTIVE" else "COMPLETED",
            color = if (isActive) AccentCyan else EmeraldSuccess
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Booking Reference:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(bkg.bookingReference, fontSize = 16.sp, fontWeight = FontWeight.Black, color = PrimaryIndigoLight)
              Text("Firestore Document: /bookings/${bkg.id}", fontSize = 10.sp, color = TextMutedDark)
            }
          }

          Text("Event: ${bkg.eventTitle}", fontWeight = FontWeight.SemiBold)
          Text("Client / Production: ${bkg.clientName}")
          Text("Venue & Zone: ${bkg.areaZone}, Bengaluru")
          Text("Scheduled Shift: ${bkg.date} • ${bkg.timeWindow}")

          HorizontalDivider()

          // Escrow & Financial details
          Text("Escrow & Payment Breakdown:", fontWeight = FontWeight.SemiBold)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Agreed Pay Rate:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("₹${bkg.payRateINR}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Escrow Status:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(bkg.escrowStatus, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isActive) AccentAmber else EmeraldSuccess)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Net Payout to Bank:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("₹${bkg.netAmountINR}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedBookingDetail = null },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("close_booking_detail_btn")
        ) {
          Text("Close")
        }
      }
    )
  }
}
