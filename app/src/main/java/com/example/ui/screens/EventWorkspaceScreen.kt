package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventRecord
import com.example.data.model.EventTaskItem
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun EventWorkspaceScreen(
  event: EventRecord,
  tasks: List<EventTaskItem>,
  onToggleTask: (String) -> Unit,
  onCloneEvent: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("WBS & Tasks", "Shift Engine", "Dependency Map", "SOP Checklists")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Top Event Title Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("workspace_hero_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusBadge(text = event.eventosId, color = AccentCyan)
            StatusBadge(text = event.status.label, color = EmeraldSuccess)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = event.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${event.venueName} • ${event.areaZone}, Bengaluru",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Expected: ${event.attendeesExpected} Pax • Budget: ₹${event.budgetEstimatedINR / 100000} Lakh",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
              onClick = onCloneEvent,
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("clone_event_action_btn")
            ) {
              Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clone Event", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Scrollable Tab Row
    item {
      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        contentColor = PrimaryIndigoLight,
        divider = {}
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            }
          )
        }
      }
    }

    when (selectedTab) {
      0 -> { // WBS & Tasks
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "EXECUTION WBS TASKS",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val completed = tasks.count { it.isCompleted }
            Text(
              text = "$completed / ${tasks.size} Completed",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryIndigoLight
            )
          }
        }

        items(tasks) { task ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onToggleTask(task.id) }
              .testTag("task_item_${task.id}")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleTask(task.id) },
                colors = CheckboxDefaults.colors(checkedColor = EmeraldSuccess)
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = task.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Assigned: ${task.assignee} • Deadline: ${task.deadlineTime}",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Dependency: ${task.dependency}",
                  fontSize = 11.sp,
                  color = AccentCyan
                )
              }
            }
          }
        }
      }

      1 -> { // Shift Engine (PDF Section 35)
        item {
          Text(
            text = "BANGALORE SHIFT ENGINE ROSTER",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        item {
          ShiftCard(
            type = "Morning Rigging Shift",
            hours = "06:00 - 14:00 (8 hrs)",
            supervisor = "Raghavan N. (AV Lead)",
            crewCount = 28,
            status = "Completed",
            statusColor = EmeraldSuccess
          )
        }
        item {
          ShiftCard(
            type = "Summit Operations Peak",
            hours = "13:30 - 21:30 (8 hrs)",
            supervisor = "Aditya Mehra (Ops Lead)",
            crewCount = 52,
            status = "ACTIVE NOW",
            statusColor = CrimsonAlert
          )
        }
        item {
          ShiftCard(
            type = "Overnight Teardown / Egress",
            hours = "21:00 - 05:00 (8 hrs)",
            supervisor = "Suresh K. (Staging)",
            crewCount = 15,
            status = "Scheduled",
            statusColor = AccentAmber
          )
        }
      }

      2 -> { // Dependency Map (PDF Section 43)
        item {
          Text(
            text = "EVENT DEPENDENCY GRAPH",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Critical Downstream Path:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AccentCyan
              )
              val stages = listOf(
                "1. Venue Clearance & Police Permission" to "Completed at T-48h",
                "2. German Hanger & Main Truss Assembly" to "Completed at T-24h",
                "3. Acoustic Rigging & Line Array Rigging" to "Completed at T-10h",
                "4. Genset Synchronization & 3-Phase Power" to "Completed at T-8h",
                "5. LED Backdrop Calibration & Content Sync" to "Completed at T-6h",
                "6. Crew Briefing & Armband Accreditation" to "Completed at T-2h",
                "7. DOORS OPEN & KEYNOTE ADDRESS" to "LIVE (Zero Delay)"
              )

              stages.forEach { (title, subtitle) ->
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(EmeraldSuccess)
                  )
                  Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
              }
            }
          }
        }
      }

      3 -> { // SOP Checklists (PDF Section 40)
        item {
          Text(
            text = "STANDARDIZED SOP TEMPLATES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        val sops = listOf(
          "Karnataka State BBMP Noise & Fire Clearance Protocol",
          "Electrical Surge & Backup Genset Transfer Test (Zero Blackout)",
          "VIP Executive Protocol & Green Room Security Perimeter",
          "Medical Ambulance Dispatch & St. John's Emergency Channel",
          "Post-Event Asset Reconcilation & Damage Inspection"
        )

        items(sops) { sop ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
              Text(
                text = sop,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ShiftCard(
  type: String,
  hours: String,
  supervisor: String,
  crewCount: Int,
  status: String,
  statusColor: Color
) {
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
        Text(text = type, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        StatusBadge(text = status, color = statusColor)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = hours, fontSize = 12.sp, color = AccentCyan, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Supervisor: $supervisor • Assigned Crew: $crewCount",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
