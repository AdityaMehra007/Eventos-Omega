package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
  onNavigateToControlRoom: () -> Unit,
  onNavigateToMarketplace: () -> Unit,
  onNavigateToWorkspace: (String) -> Unit,
  onNavigateToPassport: () -> Unit,
  onNavigateToAI: () -> Unit,
  onApplyOpportunity: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val liveEvent = events.firstOrNull { it.status == com.example.data.model.EventStatus.LIVE } ?: events.first()

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
            Column {
              Text(
                text = "Welcome back, ${user.name.split(" ").first()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${user.role.label} • ${user.primaryZone}, Bengaluru",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
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
              Text("TODAY'S GIGS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
              Text("142 Live", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AccentCyan)
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
            // Floating LIVE status
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

            // Action progress row
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

    // High-Demand Bangalore Opportunities
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "BANGALORE OPPORTUNITIES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Verified Gigs with Transparent Escrow",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        TextButton(onClick = onNavigateToMarketplace) {
          Text("View All (${opportunities.size})", color = PrimaryIndigoLight)
        }
      }
    }

    items(opportunities.take(3)) { opp ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("opp_card_${opp.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusBadge(text = "${opp.matchScore}% MATCH", color = PrimaryIndigoLight)
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
            text = "${opp.eventTitle} • ${opp.areaZone}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Shift: ${opp.timeWindow} • ${opp.spotsAvailable - opp.spotsFilled} spots left",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (opp.isApplied) {
              StatusBadge(text = "APPLIED ✓", color = EmeraldSuccess)
            } else {
              Button(
                onClick = { onApplyOpportunity(opp.id) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("apply_opp_${opp.id}")
              ) {
                Text("1-Click Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
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
