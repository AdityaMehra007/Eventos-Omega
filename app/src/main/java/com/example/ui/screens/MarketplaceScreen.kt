package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StaffOpportunity
import com.example.ui.components.BangaloreZoneChip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
  opportunities: List<StaffOpportunity>,
  onApply: (String) -> Unit,
  onBookGig: (StaffOpportunity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedZone by remember { mutableStateOf("All Bangalore") }
  var selectedCategory by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedOppForDetails by remember { mutableStateOf<StaffOpportunity?>(null) }

  val zones = listOf("All Bangalore", "Koramangala", "Indiranagar", "Whitefield", "Central BLR", "HSR Layout", "Electronic City")
  val categories = listOf("All", "Music", "Corporate", "Weddings", "Technical Support")

  val filteredList = opportunities.filter { opp ->
    (selectedZone == "All Bangalore" || opp.areaZone == selectedZone) &&
      (selectedCategory == "All" || opp.category.equals(selectedCategory, ignoreCase = true)) &&
      (searchQuery.isBlank() || opp.role.contains(searchQuery, ignoreCase = true) || opp.eventTitle.contains(searchQuery, ignoreCase = true))
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = "BANGALORE EVENT MARKETPLACE",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Zero hidden commissions • Escrow protected • Instant verified booking",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Search bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search roles (e.g. Supervisor, AV, Promoter)...") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PrimaryIndigoLight)
        },
        trailingIcon = if (searchQuery.isNotEmpty()) {
          {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        } else null,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("marketplace_search_input")
      )
    }

    // Bangalore Geo Engine Zones Bar
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(zones) { zone ->
          BangaloreZoneChip(
            zone = zone,
            isSelected = selectedZone == zone,
            onClick = { selectedZone = zone },
            modifier = Modifier.testTag("zone_chip_$zone")
          )
        }
      }
    }

    // Category Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 12.sp) }
          )
        }
      }
    }

    // Results count & transparency note
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Showing ${filteredList.size} verified opportunities",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(12.dp))
          Text(text = "100% Escrow Backed", fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Opportunities Cards
    items(filteredList) { opp ->
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("opportunity_card_${opp.id}")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Top Row: Match Score + Urgency + Pay
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
              StatusBadge(text = "${opp.matchScore}% MATCH", color = PrimaryIndigoLight)
              StatusBadge(text = opp.category, color = AccentCyan)
              StatusBadge(text = opp.urgencyTag, color = AccentAmber)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "₹${opp.payRateINR}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = EmeraldSuccess
              )
              Text(text = "net/day", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = opp.role,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${opp.eventTitle} • ${opp.areaZone}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Transparent Wage Breakdown (PDF Section 63)
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Gross: ₹${opp.grossAmountINR}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Platform Fee (5%): ₹${opp.platformFeeINR}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Net in Bank: ₹${opp.netAmountINR}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Skills required chips
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            opp.skillsRequired.take(3).forEach { skill ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.surface)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(text = skill, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Action row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Shift: ${opp.timeWindow} • ${opp.date}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (opp.isApplied) {
              StatusBadge(text = "APPLICATION SUBMITTED ✓", color = EmeraldSuccess)
            } else {
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                  onClick = { selectedOppForDetails = opp },
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text("Details", fontSize = 12.sp)
                }

                Button(
                  onClick = { onApply(opp.id) },
                  colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("apply_now_${opp.id}")
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

  // Opportunity Details Modal
  selectedOppForDetails?.let { opp ->
    AlertDialog(
      onDismissRequest = { selectedOppForDetails = null },
      title = { Text(opp.role, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Event: ${opp.eventTitle}", fontWeight = FontWeight.SemiBold)
          Text("Client: ${opp.clientName}")
          Text("Location: ${opp.areaZone}, Bengaluru")
          Text("Dress Code: ${opp.dressCode}")
          Text("Matching Algorithm Signals:", fontWeight = FontWeight.SemiBold, color = PrimaryIndigoLight)
          opp.matchReasons.forEach { reason ->
            Text("• $reason", fontSize = 12.sp)
          }
          HorizontalDivider()
          Text("Financial Settlement Terms:", fontWeight = FontWeight.SemiBold)
          Text("• Payout disbursed automatically via IMPS/UPI within 24 hours of supervisor sign-off.")
        }
      },
      confirmButton = {
        if (!opp.isBooked) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                onBookGig(opp)
                selectedOppForDetails = null
              },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              modifier = Modifier.testTag("marketplace_book_gig_btn")
            ) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Book Gig")
            }
          }
        } else {
          Button(onClick = { selectedOppForDetails = null }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)) {
            Text("Already Booked ✓")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedOppForDetails = null }) {
          Text("Back")
        }
      }
    )
  }
}
