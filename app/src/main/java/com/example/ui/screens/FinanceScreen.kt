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
import androidx.compose.runtime.*
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
import com.example.data.model.BudgetSummary
import com.example.data.model.WorkerPayoutRecord
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun FinanceScreen(
  budget: BudgetSummary,
  payouts: List<WorkerPayoutRecord>,
  onDisbursePayout: (String) -> Unit,
  onExplainFinanceWithAI: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Master Budget", "UPI Escrow Payouts", "P&L Profitability")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Header
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("finance_header_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "FINANCIAL COMMAND & LEDGER",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Automated TDS, Instant UPI Escrow & P&L Reconciliation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            StatusBadge(text = "RECONCILED", color = EmeraldSuccess)
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onExplainFinanceWithAI,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("ai_finance_audit_btn")
          ) {
            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AI Finance Audit & Cost Anomaly Detector", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
      0 -> { // Master Budget (PDF Section 61)
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricCard(
              title = "Approved Budget",
              value = "₹${budget.totalBudgetApprovedINR / 100000} Lakh",
              subtitle = "₹${budget.totalSpentINR / 100000}L Spent so far",
              icon = Icons.Default.AccountBalanceWallet,
              accentColor = EmeraldSuccess,
              modifier = Modifier.weight(1f)
            )
            MetricCard(
              title = "Outstanding",
              value = "₹${budget.outstandingPayablesINR / 1000}K",
              subtitle = "Escrow Buffer: ₹${budget.contingencyBufferINR / 100000}L",
              icon = Icons.Default.PendingActions,
              accentColor = AccentAmber,
              modifier = Modifier.weight(1f)
            )
          }
        }

        item {
          Text(
            text = "BUDGET ALLOCATION BY STREAM",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        items(budget.categories) { cat ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = cat.categoryName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "${cat.percentageUsed}%", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = if (cat.percentageUsed > 90) AccentAmber else AccentCyan)
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { cat.percentageUsed / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (cat.percentageUsed > 90) AccentAmber else PrimaryIndigoLight,
                trackColor = MaterialTheme.colorScheme.surface
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Spent: ₹${cat.spentINR / 1000}K", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Allocated: ₹${cat.allocatedINR / 1000}K", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }

      1 -> { // UPI Escrow Payouts (PDF Section 62)
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "WORKER ESCROW DISBURSEMENTS",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Direct UPI Instant Transfer",
              fontSize = 11.sp,
              color = EmeraldSuccess,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        items(payouts) { payout ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().testTag("payout_card_${payout.id}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = payout.workerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                StatusBadge(
                  text = payout.status,
                  color = if (payout.status.contains("DISBURSED")) EmeraldSuccess else AccentAmber
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = payout.eventTitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Gross: ₹${payout.grossAmountINR}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("TDS (1%): ₹${payout.tdsDeductionINR}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Net in Bank: ₹${payout.netPayableINR}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "UPI: ${payout.upiRefId}",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMutedDark
              )

              if (payout.status != "DISBURSED_UPI") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    onDisbursePayout(payout.id)
                    Toast.makeText(context, "Instant UPI Payout Disbursed to ${payout.workerName}!", Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.align(Alignment.End).testTag("disburse_payout_${payout.id}")
                ) {
                  Text("Disburse UPI Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      2 -> { // Event P&L Profitability (PDF Section 142)
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentCyan))),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Text(
                text = "Bengaluru Tech Summit 2026 P&L",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(12.dp))

              val pl = listOf(
                "Client Contract Revenue" to "+ ₹58,00,000",
                "Workforce Labor (95 Crew)" to "- ₹6,80,000",
                "AV, Acoustics & LED Backdrops" to "- ₹14,20,000",
                "Staging & German Structures" to "- ₹10,50,000",
                "Venue Rental & Permissions" to "- ₹8,00,000",
                "Hospitality & Logistics" to "- ₹5,40,000"
              )

              pl.forEach { (item, amt) ->
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = item, fontSize = 12.sp, color = TextSecondaryDark)
                  Text(
                    text = amt,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (amt.startsWith("+")) EmeraldSuccess else TextPrimaryDark
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = DarkSurfaceBorder)
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("NET EVENT CONTRIBUTION MARGIN", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AccentCyanLight)
                Text("₹13,10,000 (22.5%)", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
              }
            }
          }
        }
      }
    }
  }
}
