package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceCheckIn
import com.example.data.model.EventRecord
import com.example.data.model.OperationalIncident
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ControlRoomScreen(
  event: EventRecord,
  attendanceList: List<AttendanceCheckIn>,
  incidents: List<OperationalIncident>,
  onCheckInWorker: (String, String) -> Unit,
  onLogIncident: (String, String, String, String) -> Unit,
  onResolveIncident: (String, String) -> Unit,
  onNavigateToStaffReplacement: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showIncidentDialog by remember { mutableStateOf(false) }
  var showQrScanDialog by remember { mutableStateOf(false) }
  var scanNameInput by remember { mutableStateOf("") }
  var incidentTitle by remember { mutableStateOf("") }
  var incidentCategory by remember { mutableStateOf("Staffing") }
  var incidentSeverity by remember { mutableStateOf("HIGH") }
  var incidentAction by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Header & Event Status
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CrimsonAlert, AccentAmber))),
        modifier = Modifier.fillMaxWidth().testTag("control_room_status_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(CrimsonAlert)
              )
              Text(
                text = "LIVE CONTROL ROOM",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.White
              )
            }
            StatusBadge(text = "PALACE GROUNDS", color = AccentCyan)
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
          Text(
            text = "Venue: ${event.venueName} • Bengaluru, KA",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = DarkSurfaceBorder)
          Spacer(modifier = Modifier.height(12.dp))

          // Operational Day Mode Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { showQrScanDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).testTag("qr_scan_action_btn")
            ) {
              Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Scan QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { showIncidentDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).testTag("report_incident_btn")
            ) {
              Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Report Issue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Telemetry Statistics (PDF Section 33)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Crew Checked In",
          value = "${event.staffCheckedIn} / ${event.staffConfirmed}",
          subtitle = "${event.staffConfirmed - event.staffCheckedIn} Missing / En Route",
          icon = Icons.Default.HowToReg,
          accentColor = if (event.staffConfirmed - event.staffCheckedIn > 5) CrimsonAlert else EmeraldSuccess,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Audience Ingress",
          value = "${event.attendeesCheckedIn}",
          subtitle = "${(event.attendeesCheckedIn * 100) / event.attendeesExpected}% of Capacity",
          icon = Icons.Default.Sensors,
          accentColor = AccentCyan,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Missing Staff / Replacement Engine Alert
    item {
      val missingStaffCount = event.staffConfirmed - event.staffCheckedIn
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(AccentAmber.copy(alpha = 0.5f), Color.Transparent))
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AccentAmber.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null, tint = AccentAmber)
            }
            Column {
              Text(
                text = "Staff Replacement Engine",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$missingStaffCount crew pending. 6 standby candidates within 5km radius.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Button(
            onClick = onNavigateToStaffReplacement,
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("auto_replace_staff_btn")
          ) {
            Text("Auto-Fill", color = DarkSurfaceBase, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // Live Operational Incidents Desk
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(18.dp))
          Text(
            text = "LIVE INCIDENTS & ESCALATIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Text(
          text = "${incidents.size} Active",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    items(incidents) { inc ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().testTag("incident_item_${inc.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
              StatusBadge(
                text = inc.severity,
                color = if (inc.severity == "CRITICAL") CrimsonAlert else if (inc.severity == "MEDIUM") AccentAmber else AccentCyan
              )
              Text(
                text = inc.category,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            StatusBadge(
              text = inc.status,
              color = if (inc.status == "Resolved") EmeraldSuccess else AccentAmber
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = inc.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Action: ${inc.actionSummary}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (inc.status != "Resolved") {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { onResolveIncident(inc.id, "Resolved on site by field supervisor.") },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.align(Alignment.End).testTag("resolve_incident_${inc.id}")
            ) {
              Text("Mark Resolved", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Real-Time Attendance Stream
    item {
      Text(
        text = "REAL-TIME CREW INGRESS STREAM",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(attendanceList) { att ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PrimaryIndigo.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
            }
            Column {
              Text(
                text = att.workerName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${att.role} • ${att.method}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = att.timestamp, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentCyan)
            Text(text = "Verified", fontSize = 10.sp, color = EmeraldSuccess)
          }
        }
      }
    }
  }

  // QR Scan Simulator Dialog
  if (showQrScanDialog) {
    AlertDialog(
      onDismissRequest = { showQrScanDialog = false },
      title = { Text("Eventos QR Field Scanner", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Simulate live attendee or crew QR code badge validation at Palace Grounds Gate 1:")
          OutlinedTextField(
            value = scanNameInput,
            onValueChange = { scanNameInput = it },
            label = { Text("Worker / Guest Name") },
            placeholder = { Text("e.g. Rahul Sen / Vipul Verma") },
            modifier = Modifier.fillMaxWidth().testTag("scan_name_field")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val name = scanNameInput.ifBlank { "Naveen Raj (Promoter)" }
            onCheckInWorker(name, "QR Badge Scanner")
            scanNameInput = ""
            showQrScanDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
          modifier = Modifier.testTag("confirm_scan_btn")
        ) {
          Text("Record Check-In")
        }
      },
      dismissButton = {
        TextButton(onClick = { showQrScanDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Report Incident Dialog
  if (showIncidentDialog) {
    AlertDialog(
      onDismissRequest = { showIncidentDialog = false },
      title = { Text("Report Site Incident", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = incidentTitle,
            onValueChange = { incidentTitle = it },
            label = { Text("Incident Title") },
            placeholder = { Text("e.g. VIP line array power surge") },
            modifier = Modifier.fillMaxWidth().testTag("incident_title_field")
          )
          OutlinedTextField(
            value = incidentAction,
            onValueChange = { incidentAction = it },
            label = { Text("Immediate Action Taken") },
            placeholder = { Text("e.g. Switched to backup Cummins 125kVA generator") },
            modifier = Modifier.fillMaxWidth().testTag("incident_action_field")
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { incidentSeverity = "MEDIUM" },
              colors = ButtonDefaults.buttonColors(containerColor = if (incidentSeverity == "MEDIUM") AccentAmber else MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.weight(1f)
            ) {
              Text("MEDIUM", fontSize = 11.sp)
            }
            Button(
              onClick = { incidentSeverity = "CRITICAL" },
              colors = ButtonDefaults.buttonColors(containerColor = if (incidentSeverity == "CRITICAL") CrimsonAlert else MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.weight(1f)
            ) {
              Text("CRITICAL", fontSize = 11.sp)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (incidentTitle.isNotBlank()) {
              onLogIncident(incidentTitle, incidentCategory, incidentSeverity, incidentAction)
              incidentTitle = ""
              incidentAction = ""
              showIncidentDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
          modifier = Modifier.testTag("submit_incident_btn")
        ) {
          Text("Broadcast Incident")
        }
      },
      dismissButton = {
        TextButton(onClick = { showIncidentDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
