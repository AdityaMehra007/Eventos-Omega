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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VendorCategory
import com.example.data.model.VendorListing
import com.example.data.model.VendorQuotation
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun VendorProcurementScreen(
  vendors: List<VendorListing>,
  quotations: List<VendorQuotation>,
  onDraftRFQWithAI: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var selectedCategory by remember { mutableStateOf<VendorCategory?>(null) }
  val tabs = listOf("Vendor Directory", "Smart Quote Matrix", "Equipment Vault")

  val filteredVendors = vendors.filter { v ->
    selectedCategory == null || v.category == selectedCategory
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Header & RFQ Action
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("procurement_header_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "BANGALORE VENDOR NETWORK",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Audited equipment, verified GST & zero delay SLA",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            StatusBadge(text = "GST VERIFIED", color = EmeraldSuccess)
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onDraftRFQWithAI,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("ai_draft_rfq_btn")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AI Procurement Negotiator (Draft RFQ)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Tabs
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = PrimaryIndigoLight,
        divider = {}
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp) }
          )
        }
      }
    }

    when (selectedTab) {
      0 -> { // Vendor Directory
        item {
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
              FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All Categories") }
              )
            }
            items(VendorCategory.values()) { cat ->
              FilterChip(
                selected = selectedCategory == cat,
                onClick = { selectedCategory = cat },
                label = { Text(cat.label.split(" ").first()) }
              )
            }
          }
        }

        items(filteredVendors) { v ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().testTag("vendor_card_${v.id}")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusBadge(text = v.verificationBadge, color = EmeraldSuccess)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                  Text(text = "${v.rating} (${v.completedProjects} events)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = v.businessName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${v.category.label} • ${v.areaZone} (Radius: ${v.serviceRadiusKm} km)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(8.dp))
              Text(text = v.capacityDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

              Spacer(modifier = Modifier.height(8.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                v.inventoryHighlights.forEach { gear ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(MaterialTheme.colorScheme.surface)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(text = gear, fontSize = 10.sp, color = AccentCyan)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Est: ${v.baseRateEstINR}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentAmber
                )
                Text(
                  text = v.contactPerson,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      1 -> { // Smart Quote Comparison Matrix (PDF Section 47)
        item {
          Text(
            text = "SMART QUOTE COMPARISON MATRIX",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        items(quotations) { q ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().testTag("quote_card_${q.id}")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = q.vendorName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                StatusBadge(
                  text = if (q.status == "Selected") "ACCEPTED ✓" else "UNDER REVIEW",
                  color = if (q.status == "Selected") EmeraldSuccess else AccentAmber
                )
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "₹${q.quoteAmountINR} Total Quotation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = AccentCyan
              )

              Spacer(modifier = Modifier.height(10.dp))
              Text(text = "INCLUDED IN SCOPE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
              q.scopeIncluded.forEach { inc ->
                Text(text = "• $inc", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(text = "EXCLUDED (SCOPE RISK):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonAlert)
              q.scopeExcluded.forEach { exc ->
                Text(text = "• $exc", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = q.riskAssessment,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentAmber
              )
            }
          }
        }
      }

      2 -> { // Equipment Vault (PDF Section 49)
        item {
          Text(
            text = "EQUIPMENT & ASSET INVENTORY",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        val items = listOf(
          Triple("L-Acoustics K2 Top Cabinets (Pair)", "SN: LAC-BLR-0941", "ON_SITE - Palace Grounds"),
          Triple("NovaStar UHD-4K LED Video Switcher", "SN: NVA-8820-BLR", "ON_SITE - Palace Grounds"),
          Triple("Cummins 125kVA Silent Generator Mobile", "SN: CUM-G-125K-01", "ON_SITE - Standby Power Active"),
          Triple("Shure AD4Q Quad Wireless Receiver", "SN: SHU-AX-0044", "CHECKED_OUT - Sound Booth")
        )

        items(items) { (gear, sn, status) ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = gear, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = sn, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              StatusBadge(text = status, color = if (status.contains("ON_SITE")) EmeraldSuccess else AccentCyan)
            }
          }
        }
      }
    }
  }
}
