package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventosUser
import com.example.data.model.PassportStamp
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun PassportScreen(
  user: EventosUser,
  stamps: List<PassportStamp>,
  onUpgradeCareerWithAI: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Official Eventos Identity Passport Card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.linearGradient(listOf(AccentAmber, PrimaryIndigoLight, AccentCyan))
        ),
        modifier = Modifier.fillMaxWidth().testTag("passport_identity_card")
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(AccentAmber),
                contentAlignment = Alignment.Center
              ) {
                Text("Ω", color = DarkSurfaceBase, fontWeight = FontWeight.Black, fontSize = 18.sp)
              }
              Text(
                text = "EVENTOS PASSPORT",
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 14.sp,
                color = Color.White
              )
            }
            StatusBadge(text = "VERIFIED IDENTITY", color = EmeraldSuccess)
          }

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = user.title,
                style = MaterialTheme.typography.bodyMedium,
                color = AccentCyanLight
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = user.eventosId,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AccentAmber
              )
            }

            // Simulated QR Stamp
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Eventos QR ID",
                tint = Color.Black,
                modifier = Modifier.fillMaxSize()
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider(color = DarkSurfaceBorder)
          Spacer(modifier = Modifier.height(12.dp))

          // Verified Metrics Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("LOGGED HOURS", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.verifiedHours} hrs", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Column {
              Text("TRUST RATING", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.rating} ★ (${user.reviewCount})", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AccentAmber)
            }
            Column {
              Text("COMPLETED GIGS", fontSize = 10.sp, color = TextMutedDark, fontWeight = FontWeight.Bold)
              Text("${user.completedEvents} Events", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AccentCyan)
            }
          }
        }
      }
    }

    // Export & Career Advisor Action
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            Toast.makeText(context, "Exported LinkedIn-Ready Verified Career Sheet!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).testTag("export_passport_btn")
        ) {
          Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryIndigoLight)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Export PDF", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        Button(
          onClick = onUpgradeCareerWithAI,
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).testTag("career_coach_ai_btn")
        ) {
          Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("AI Career Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Career Graph Progression Ladder (PDF Section 192)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "EVENT CAREER LADDER",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          val levels = listOf(
            Triple("1. Student / Campus Ambassador", "Completed (100 hrs)", true),
            Triple("2. Freelancer / Experiential Promoter", "Completed (250 hrs)", true),
            Triple("3. Senior Site Supervisor", "CURRENT RANK (540+ hrs)", true),
            Triple("4. Technical Event Manager", "Target: 700 hrs (14 events remaining)", false),
            Triple("5. Event Operations Agency Partner", "Target: 1,200 hrs + 5 Teams", false)
          )

          levels.forEach { (rank, status, isDone) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isDone) EmeraldSuccess else MaterialTheme.colorScheme.outline)
                )
                Text(text = rank, fontSize = 12.sp, fontWeight = if (rank.contains("CURRENT")) FontWeight.Bold else FontWeight.Normal)
              }
              Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDone) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Verified Event Proof of Work Stamps (PDF Section 21)
    item {
      Text(
        text = "VERIFIED EVENT EXPERIENCE STAMPS",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(stamps) { stamp ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess.copy(alpha = 0.3f), Color.Transparent))),
        modifier = Modifier.fillMaxWidth().testTag("stamp_card_${stamp.eventId}")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stamp.eventName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            StatusBadge(text = "${stamp.rating} ★", color = AccentAmber)
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${stamp.role} • ${stamp.location}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Logged: ${stamp.hoursLogged} Hours • Verified by: ${stamp.verifiedBy}",
            fontSize = 11.sp,
            color = AccentCyan
          )

          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                text = "\"${stamp.clientEndorsement}\"",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Cryptographic Proof Hash: ${stamp.proofHash}",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMutedDark
              )
            }
          }
        }
      }
    }
  }
}
