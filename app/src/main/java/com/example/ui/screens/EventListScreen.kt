package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Event
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

/**
 * Screen displaying the list of persistent events stored in the Room database using LazyColumn,
 * observing state from EventViewModel, with filtering, searching, and a Floating Action Button
 * to open the Add Event Dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
  viewModel: EventViewModel,
  modifier: Modifier = Modifier,
  isDarkTheme: Boolean = true,
  onToggleTheme: () -> Unit = {},
  onNavigateToStats: () -> Unit = {},
  onEventSelected: (Event) -> Unit = {}
) {
  val context = LocalContext.current
  val events by viewModel.events.collectAsStateWithLifecycle()
  val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }
  var selectedEventDetails by remember { mutableStateOf<Event?>(null) }
  var isCalendarView by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  // Filtered and searched events by name or venue in real-time
  val filteredEvents = remember(events, selectedStatus, searchQuery) {
    events.filter { event ->
      val matchesStatus = when (selectedStatus.uppercase()) {
        "ALL" -> true
        "UPCOMING" -> event.status.equals("Upcoming", ignoreCase = true) || event.status.equals("CONFIRMED", ignoreCase = true) || event.status.equals("PLANNING", ignoreCase = true)
        "IN-PROGRESS", "IN_PROGRESS" -> event.status.equals("In-Progress", ignoreCase = true) || event.status.equals("LIVE", ignoreCase = true)
        "COMPLETED" -> event.status.equals("Completed", ignoreCase = true) || event.status.equals("FINANCIAL_SETTLED", ignoreCase = true)
        else -> event.status.equals(selectedStatus, ignoreCase = true)
      }
      val query = searchQuery.trim().lowercase()
      val matchesSearch = if (query.isEmpty()) true else {
        event.name.lowercase().contains(query) ||
          event.venue.lowercase().contains(query)
      }
      matchesStatus && matchesSearch
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = DarkSurfaceBase,
    topBar = {
      // Pinned real-time search bar at the very top of the event list screen
      Surface(
        color = DarkSurfaceBase,
        tonalElevation = 6.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("event_search_bar_top_container")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Top Title Row with live count & Analytics button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = PrimaryIndigoLight,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "Events Registry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Calendar Grid vs List Layout Toggle Button
              FilledTonalIconButton(
                onClick = { isCalendarView = !isCalendarView },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (isCalendarView) PrimaryIndigo.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = if (isCalendarView) PrimaryIndigoLight else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                  .size(36.dp)
                  .testTag("toggle_calendar_view_btn")
              ) {
                Icon(
                  imageVector = if (isCalendarView) Icons.Default.ViewAgenda else Icons.Default.CalendarMonth,
                  contentDescription = if (isCalendarView) "Switch to List View" else "Switch to Calendar View",
                  modifier = Modifier.size(18.dp)
                )
              }

              // CSV Export Button
              FilledTonalIconButton(
                onClick = {
                  if (events.isEmpty()) {
                    Toast.makeText(context, "No events available to export.", Toast.LENGTH_SHORT).show()
                  } else {
                    val success = com.example.util.EventCsvExporter.shareCsvFile(context, events)
                    if (!success) {
                      Toast.makeText(context, "Unable to generate CSV export file.", Toast.LENGTH_SHORT).show()
                    }
                  }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = EmeraldSuccess.copy(alpha = 0.18f),
                  contentColor = EmeraldSuccess
                ),
                modifier = Modifier
                  .size(36.dp)
                  .testTag("export_events_csv_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Share,
                  contentDescription = "Export Events CSV",
                  modifier = Modifier.size(18.dp)
                )
              }

              // Theme Toggle Button
              FilledTonalIconButton(
                onClick = onToggleTheme,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (isDarkTheme) AccentAmber.copy(alpha = 0.18f) else PrimaryIndigo.copy(alpha = 0.18f),
                  contentColor = if (isDarkTheme) AccentAmber else PrimaryIndigo
                ),
                modifier = Modifier
                  .size(36.dp)
                  .testTag("event_list_theme_toggle_btn")
              ) {
                Icon(
                  imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                  contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                  modifier = Modifier.size(18.dp)
                )
              }

              // Analytics Button
              FilledTonalIconButton(
                onClick = onNavigateToStats,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = AccentCyan.copy(alpha = 0.18f),
                  contentColor = AccentCyan
                ),
                modifier = Modifier
                  .size(36.dp)
                  .testTag("navigate_to_stats_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.PieChart,
                  contentDescription = "Event Statistics",
                  modifier = Modifier.size(18.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryIndigo.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.4f))
              ) {
                Text(
                  text = "${filteredEvents.size} of ${events.size}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryIndigoLight,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          // Real-time Search Bar Input Field
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = {
              Text(
                text = "Search by event name or venue...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search events",
                tint = if (searchQuery.isNotEmpty()) PrimaryIndigoLight else MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { viewModel.setSearchQuery("") },
                  modifier = Modifier.testTag("clear_search_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = PrimaryIndigoLight,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("event_search_bar")
          )
        }
      }
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showAddDialog = true },
        icon = {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Event Icon",
            modifier = Modifier.size(22.dp)
          )
        },
        text = {
          Text(
            text = "Add Event",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        },
        containerColor = PrimaryIndigo,
        contentColor = Color.White,
        modifier = Modifier
          .testTag("add_event_fab")
          .padding(bottom = 16.dp)
      )
    }
  ) { paddingValues ->
    if (isCalendarView) {
      com.example.ui.components.EventCalendarView(
        events = filteredEvents,
        onEventClick = { event ->
          selectedEventDetails = event
          onEventSelected(event)
        },
        onEventDelete = { event ->
          viewModel.deleteEvent(event.id)
          Toast.makeText(context, "Deleted \"${event.name}\" from Room database", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp)
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
      ) {
      // Header Banner Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("event_list_hero_card")
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Event,
                  contentDescription = null,
                  tint = PrimaryIndigoLight,
                  modifier = Modifier.size(24.dp)
                )
                Text(
                  text = "EVENT REGISTRY",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp,
                  color = PrimaryIndigoLight
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldSuccess.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "${events.size} Room Cached",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = EmeraldSuccess,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Text(
              text = "Production & On-Site Schedule",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Stored locally in SQLite via Room database. Reactive state observed with Flow.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Switch between Agenda and Month Calendar views",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                  onClick = { isCalendarView = true },
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = PrimaryIndigo.copy(alpha = 0.2f),
                    contentColor = PrimaryIndigoLight
                  ),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("hero_calendar_view_btn")
                ) {
                  Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Calendar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                FilledTonalButton(
                  onClick = {
                    if (events.isEmpty()) {
                      Toast.makeText(context, "No events to export.", Toast.LENGTH_SHORT).show()
                    } else {
                      val success = com.example.util.EventCsvExporter.shareCsvFile(context, events)
                      if (!success) {
                        Toast.makeText(context, "Export failed. Please try again.", Toast.LENGTH_SHORT).show()
                      }
                    }
                  },
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = EmeraldSuccess.copy(alpha = 0.18f),
                    contentColor = EmeraldSuccess
                  ),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("hero_export_csv_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Export CSV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }


      // Filter Status Chips Row
      item {
        val filterOptions = listOf("ALL", "UPCOMING", "IN-PROGRESS", "COMPLETED", "LIVE", "PLANNING", "CONFIRMED")
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("event_status_filters_row")
        ) {
          items(filterOptions) { status ->
            val isSelected = selectedStatus == status
            val badgeColor = when (status) {
              "LIVE", "IN-PROGRESS" -> CrimsonAlert
              "CONFIRMED" -> EmeraldSuccess
              "UPCOMING", "PLANNING" -> AccentAmber
              "COMPLETED" -> PrimaryIndigoLight
              else -> AccentCyan
            }

            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setFilterStatus(status) },
              label = {
                Text(
                  text = status,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = badgeColor.copy(alpha = 0.25f),
                selectedLabelColor = badgeColor
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = if (isSelected) badgeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                selectedBorderColor = badgeColor,
                enabled = true,
                selected = isSelected
              ),
              modifier = Modifier.testTag("filter_chip_$status")
            )
          }
        }
      }

      // Events List or Empty State
      if (filteredEvents.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("empty_events_card")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.EventBusy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(44.dp)
              )
              Text(
                text = if (searchQuery.isNotEmpty() || selectedStatus != "ALL") "No Matching Events" else "No Events in Room DB",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Tap '+ Add Event' to save your first event into the Room database.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("empty_state_add_event_btn")
              ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New Event", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      } else {
        items(
          items = filteredEvents,
          key = { it.id }
        ) { event ->
          val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
              if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                viewModel.deleteEvent(event.id)
                Toast.makeText(context, "Deleted \"${event.name}\" from Room database", Toast.LENGTH_SHORT).show()
                true
              } else {
                false
              }
            }
          )

          SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            modifier = Modifier.testTag("swipe_to_delete_${event.id}"),
            backgroundContent = {
              val isEndToStart = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
              val alignment = if (isEndToStart) Alignment.CenterEnd else Alignment.CenterStart
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(CrimsonAlert.copy(alpha = 0.85f), shape = RoundedCornerShape(16.dp))
                  .padding(horizontal = 20.dp),
                contentAlignment = alignment
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Event",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                  Text(
                    text = "Swipe to Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }
            }
          ) {
            EventCard(
              event = event,
              onClick = {
                selectedEventDetails = event
                onEventSelected(event)
              },
              onDelete = {
                viewModel.deleteEvent(event.id)
                Toast.makeText(context, "Deleted \"${event.name}\" from Room database", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }
    }
  }

  // ADD EVENT DIALOG
  if (showAddDialog) {
    AddEventDialog(
      onDismiss = { showAddDialog = false },
      onConfirm = { name, date, venue, status, zone, client, time, desc ->
        viewModel.addEvent(
          name = name,
          date = date,
          venue = venue,
          status = status,
          areaZone = zone,
          clientName = client,
          timeSlot = time,
          description = desc
        )
        showAddDialog = false
        Toast.makeText(context, "Saved \"$name\" into Room database!", Toast.LENGTH_LONG).show()
      }
    )
  }

  // MODAL BOTTOM SHEET FOR EVENT DETAILS
  selectedEventDetails?.let { event ->
    ModalBottomSheet(
      onDismissRequest = { selectedEventDetails = null },
      sheetState = sheetState,
      containerColor = DarkSurfaceElevated,
      dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)) },
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      modifier = Modifier.testTag("event_details_bottom_sheet")
    ) {
      EventDetailsBottomSheetContent(
        event = event,
        onDismiss = { selectedEventDetails = null },
        onDelete = {
          viewModel.deleteEvent(event.id)
          selectedEventDetails = null
          Toast.makeText(context, "Deleted \"${event.name}\" from Room database", Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

/**
 * Individual Event Item Card in the LazyColumn.
 */
@Composable
fun EventCard(
  event: Event,
  onClick: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val normalizedStatus = event.status.trim()
  val (statusColor, statusIcon, displayLabel) = when (normalizedStatus.uppercase()) {
    "LIVE", "IN-PROGRESS", "IN_PROGRESS", "IN PROGRESS" -> Triple(CrimsonAlert, Icons.Default.Sensors, if (normalizedStatus.equals("LIVE", ignoreCase = true)) "LIVE NOW" else "IN-PROGRESS")
    "UPCOMING", "PLANNING", "SCHEDULED" -> Triple(AccentAmber, Icons.Default.Upcoming, if (normalizedStatus.equals("PLANNING", ignoreCase = true)) "PLANNING" else "UPCOMING")
    "CONFIRMED" -> Triple(EmeraldSuccess, Icons.Default.CheckCircle, "CONFIRMED")
    "COMPLETED", "SETTLED" -> Triple(PrimaryIndigoLight, Icons.Default.TaskAlt, "COMPLETED")
    else -> Triple(AccentCyan, Icons.Default.Event, normalizedStatus.uppercase())
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("event_item_${event.id}")
  ) {
    Row(modifier = Modifier.fillMaxWidth()) {
      // Leading status color strip indicator
      Box(
        modifier = Modifier
          .width(6.dp)
          .fillMaxHeight()
          .background(statusColor)
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Header: Status Icon + Event Name + Visual Status Badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Visual Status Icon indicator
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = statusColor.copy(alpha = 0.15f),
              modifier = Modifier.size(28.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = statusIcon,
                  contentDescription = displayLabel,
                  tint = statusColor,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Text(
              text = event.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Visual Status Badge with icon and label
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = statusColor.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(statusColor, shape = RoundedCornerShape(3.dp))
              )
              Text(
                text = displayLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = statusColor
              )
            }
          }
        }

      // Date & Time row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CalendarToday,
          contentDescription = null,
          tint = PrimaryIndigoLight,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "${event.date} • ${event.timeSlot}",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryIndigoLight
        )
      }

      // Venue & Zone row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
          tint = AccentAmber,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "${event.venue} (${event.areaZone})",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Client attribution
      if (event.clientName.isNotBlank()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Client: ${event.clientName}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

      // Footer: Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Room SQLite: ${event.id.take(8)}...",
          fontSize = 10.sp,
          color = TextMutedDark
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(32.dp)
              .testTag("delete_event_${event.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete Event",
              tint = CrimsonAlert,
              modifier = Modifier.size(18.dp)
            )
          }

          TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.testTag("view_details_${event.id}")
          ) {
            Text("Details", fontSize = 11.sp, color = PrimaryIndigoLight, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
}

/**
 * Dialog to add a new event into the Room database.
 */
@Composable
fun AddEventDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, date: String, venue: String, status: String, zone: String, client: String, time: String, desc: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var date by remember { mutableStateOf("15 Nov 2026") }
  var venue by remember { mutableStateOf("") }
  var status by remember { mutableStateOf("Upcoming") }
  var areaZone by remember { mutableStateOf("Koramangala") }
  var clientName by remember { mutableStateOf("Corporate Partner") }
  var timeSlot by remember { mutableStateOf("09:00 - 18:00") }
  var description by remember { mutableStateOf("") }

  val statusOptions = listOf("Upcoming", "In-Progress", "Completed", "Confirmed", "Planning")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = PrimaryIndigoLight)
        Text("Add New Event to Room DB", fontWeight = FontWeight.Bold, fontSize = 18.sp)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Event Name *") },
          placeholder = { Text("e.g. Bangalore Startup Expo 2026") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_name")
        )

        OutlinedTextField(
          value = venue,
          onValueChange = { venue = it },
          label = { Text("Venue Location *") },
          placeholder = { Text("e.g. Palace Grounds, Tripuravasini") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_venue")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date *") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_event_date")
          )
          OutlinedTextField(
            value = timeSlot,
            onValueChange = { timeSlot = it },
            label = { Text("Time Slot") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_event_time")
          )
        }

        // Status Selection
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Event Status *", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            statusOptions.forEach { opt ->
              val isSelected = status == opt
              FilterChip(
                selected = isSelected,
                onClick = { status = opt },
                label = { Text(opt, fontSize = 10.sp) },
                modifier = Modifier.testTag("select_status_$opt")
              )
            }
          }
        }

        OutlinedTextField(
          value = areaZone,
          onValueChange = { areaZone = it },
          label = { Text("Area Zone") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_zone")
        )

        OutlinedTextField(
          value = clientName,
          onValueChange = { clientName = it },
          label = { Text("Client / Organizer") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_client")
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          minLines = 2,
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_desc")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && venue.isNotBlank()) {
            onConfirm(name, date, venue, status, areaZone, clientName, timeSlot, description)
          }
        },
        enabled = name.isNotBlank() && venue.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
        modifier = Modifier.testTag("confirm_add_event_btn")
      ) {
        Text("Save to Room DB")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_add_event_btn")
      ) {
        Text("Cancel")
      }
    }
  )
}

/**
 * ModalBottomSheet content displaying full event details including venue address, zone, and notes/description.
 */
@Composable
fun EventDetailsBottomSheetContent(
  event: Event,
  onDismiss: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val normalizedStatus = event.status.trim()
  val (statusColor, statusIcon, displayLabel) = when (normalizedStatus.uppercase()) {
    "LIVE", "IN-PROGRESS", "IN_PROGRESS", "IN PROGRESS" -> Triple(CrimsonAlert, Icons.Default.Sensors, if (normalizedStatus.equals("LIVE", ignoreCase = true)) "LIVE NOW" else "IN-PROGRESS")
    "UPCOMING", "PLANNING", "SCHEDULED" -> Triple(AccentAmber, Icons.Default.Upcoming, if (normalizedStatus.equals("PLANNING", ignoreCase = true)) "PLANNING" else "UPCOMING")
    "CONFIRMED" -> Triple(EmeraldSuccess, Icons.Default.CheckCircle, "CONFIRMED")
    "COMPLETED", "SETTLED" -> Triple(PrimaryIndigoLight, Icons.Default.TaskAlt, "COMPLETED")
    else -> Triple(AccentCyan, Icons.Default.Event, normalizedStatus.uppercase())
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .padding(bottom = 36.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Row: Status Icon, Title, and Badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f)),
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = statusIcon,
              contentDescription = displayLabel,
              tint = statusColor,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Column {
          Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Event Registry Details",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = statusColor.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
      ) {
        Text(
          text = displayLabel,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = statusColor,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

    // Venue Address & Location Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.3f)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("venue_details_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = AccentAmber,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "VENUE & ADDRESS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AccentAmber
          )
        }

        Text(
          text = event.venue,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationCity,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Zone: ${event.areaZone}, Bengaluru • Karnataka, India",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Schedule & Timing Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.25f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = PrimaryIndigoLight,
            modifier = Modifier.size(20.dp)
          )
          Column {
            Text(
              text = "Date: ${event.date}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Operating Hours: ${event.timeSlot}",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (event.clientName.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = AccentCyan.copy(alpha = 0.12f)
          ) {
            Text(
              text = event.clientName,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = AccentCyan,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // Event Notes / Description Section
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("event_notes_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = PrimaryIndigoLight,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "EVENT NOTES & OPERATIONAL BRIEF",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryIndigoLight
          )
        }

        Text(
          text = if (event.description.isNotBlank()) {
            event.description
          } else {
            "No specific operational notes recorded. Event managed under standard operating procedures (SOP)."
          },
          style = MaterialTheme.typography.bodyMedium,
          color = if (event.description.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Database Technical Metadata
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = DarkSurfaceBase,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SQLite PrimaryKey:",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = event.id,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryIndigoLight
        )
      }
    }

    // Action Buttons: Delete and Close
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onDelete,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
        border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("bottom_sheet_delete_event_btn")
      ) {
        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Delete Event", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("bottom_sheet_close_btn")
      ) {
        Text("Close", fontWeight = FontWeight.Bold)
      }
    }
  }
}
