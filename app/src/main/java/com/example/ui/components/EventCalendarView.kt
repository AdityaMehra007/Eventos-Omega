package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Event
import com.example.ui.screens.EventCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Month-based grid calendar view layout for events.
 * Organizes events on a standard 7-column calendar grid (Sun - Sat) with
 * month switcher, event count badges on dates, and day drill-down selector.
 */
@Composable
fun EventCalendarView(
  events: List<Event>,
  onEventClick: (Event) -> Unit,
  onEventDelete: (Event) -> Unit,
  modifier: Modifier = Modifier
) {
  // Current displayed calendar month/year
  val calendar = remember { Calendar.getInstance().apply { set(Calendar.YEAR, 2026); set(Calendar.MONTH, Calendar.NOVEMBER); set(Calendar.DAY_OF_MONTH, 1) } }
  var currentYear by remember { mutableIntStateOf(2026) }
  var currentMonth by remember { mutableIntStateOf(Calendar.NOVEMBER) } // 0-indexed, November = 10
  var selectedDayNumber by remember { mutableIntStateOf(15) } // Default selected day

  // Format month and year label
  val monthYearLabel = remember(currentYear, currentMonth) {
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, currentYear)
      set(Calendar.MONTH, currentMonth)
      set(Calendar.DAY_OF_MONTH, 1)
    }
    SimpleDateFormat("MMMM yyyy", Locale.US).format(cal.time)
  }

  // Pre-index events by day of this month
  // Assumes date formats like "15 Nov 2026", "28 Nov 2026", "2026-11-15", etc.
  val eventsByDay = remember(events, currentYear, currentMonth) {
    val monthShortName = when (currentMonth) {
      Calendar.JANUARY -> "Jan"
      Calendar.FEBRUARY -> "Feb"
      Calendar.MARCH -> "Mar"
      Calendar.APRIL -> "Apr"
      Calendar.MAY -> "May"
      Calendar.JUNE -> "Jun"
      Calendar.JULY -> "Jul"
      Calendar.AUGUST -> "Aug"
      Calendar.SEPTEMBER -> "Sep"
      Calendar.OCTOBER -> "Oct"
      Calendar.NOVEMBER -> "Nov"
      Calendar.DECEMBER -> "Dec"
      else -> "Nov"
    }
    val monthNumString = String.format("%02d", currentMonth + 1)

    val map = mutableMapOf<Int, MutableList<Event>>()
    events.forEach { event ->
      val dateStr = event.date.trim()
      // Extract day number
      var day: Int? = null
      val parts = dateStr.split(" ", "-", "/")
      for (part in parts) {
        val num = part.toIntOrNull()
        if (num != null && num in 1..31) {
          // Verify month match if month appears in dateStr
          val hasMonthMatch = dateStr.contains(monthShortName, ignoreCase = true) ||
            dateStr.contains(monthNumString) ||
            dateStr.contains(SimpleDateFormat("MMMM", Locale.US).format(Calendar.getInstance().apply { set(Calendar.MONTH, currentMonth) }.time), ignoreCase = true)

          if (hasMonthMatch || (!dateStr.contains("Jan") && !dateStr.contains("Feb") && !dateStr.contains("Dec"))) {
            day = num
            break
          }
        }
      }
      if (day != null) {
        map.getOrPut(day) { mutableListOf() }.add(event)
      }
    }
    map
  }

  // Compute days in current month and first day of week
  val daysInMonth = remember(currentYear, currentMonth) {
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, currentYear)
      set(Calendar.MONTH, currentMonth)
      set(Calendar.DAY_OF_MONTH, 1)
    }
    cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  }

  val firstDayOfWeek = remember(currentYear, currentMonth) {
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, currentYear)
      set(Calendar.MONTH, currentMonth)
      set(Calendar.DAY_OF_MONTH, 1)
    }
    cal.get(Calendar.DAY_OF_WEEK) // Calendar.SUNDAY = 1
  }

  val selectedDayEvents = eventsByDay[selectedDayNumber] ?: emptyList()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("event_calendar_view_container"),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp)
  ) {
    // 1. Month Header & Navigation Switcher
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              if (currentMonth == Calendar.JANUARY) {
                currentMonth = Calendar.DECEMBER
                currentYear -= 1
              } else {
                currentMonth -= 1
              }
            },
            modifier = Modifier.testTag("cal_prev_month_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous Month",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = PrimaryIndigoLight,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = monthYearLabel,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(
            onClick = {
              if (currentMonth == Calendar.DECEMBER) {
                currentMonth = Calendar.JANUARY
                currentYear += 1
              } else {
                currentMonth += 1
              }
            },
            modifier = Modifier.testTag("cal_next_month_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next Month",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // 2. Day-of-Week Labels Header (Sun - Sat)
    item {
      val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        dayNames.forEach { dayName ->
          Text(
            text = dayName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (dayName == "Sun" || dayName == "Sat") AccentAmber else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. Calendar Grid of Days
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("month_grid_card")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Total slots = (firstDayOfWeek - 1) blanks + daysInMonth
          val totalSlots = (firstDayOfWeek - 1) + daysInMonth
          val rowsCount = (totalSlots + 6) / 7

          for (rowIndex in 0 until rowsCount) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              for (colIndex in 0 until 7) {
                val slotIndex = rowIndex * 7 + colIndex
                val dayNum = slotIndex - (firstDayOfWeek - 1) + 1

                if (dayNum in 1..daysInMonth) {
                  val dayEvents = eventsByDay[dayNum] ?: emptyList()
                  val isSelected = selectedDayNumber == dayNum
                  val hasEvents = dayEvents.isNotEmpty()

                  // Status indicator color for the highest priority event
                  val dotColor = when {
                    dayEvents.any { it.status.equals("Live", ignoreCase = true) || it.status.equals("In-Progress", ignoreCase = true) } -> CrimsonAlert
                    dayEvents.any { it.status.equals("Confirmed", ignoreCase = true) } -> EmeraldSuccess
                    dayEvents.any { it.status.equals("Upcoming", ignoreCase = true) || it.status.equals("Planning", ignoreCase = true) } -> AccentAmber
                    hasEvents -> PrimaryIndigoLight
                    else -> Color.Transparent
                  }

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .aspectRatio(1.05f)
                      .padding(2.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(
                        when {
                          isSelected -> PrimaryIndigo
                          hasEvents -> PrimaryIndigoLight.copy(alpha = 0.12f)
                          else -> Color.Transparent
                        }
                      )
                      .clickable { selectedDayNumber = dayNum }
                      .testTag("cal_day_$dayNum"),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center
                    ) {
                      Text(
                        text = "$dayNum",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected || hasEvents) FontWeight.Black else FontWeight.Normal,
                        color = when {
                          isSelected -> Color.White
                          hasEvents -> MaterialTheme.colorScheme.onSurface
                          else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                      )

                      // Event indicator dot or badge
                      if (hasEvents) {
                        Row(
                          horizontalArrangement = Arrangement.spacedBy(2.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.padding(top = 2.dp)
                        ) {
                          Box(
                            modifier = Modifier
                              .size(5.dp)
                              .background(if (isSelected) Color.White else dotColor, shape = CircleShape)
                          )
                          if (dayEvents.size > 1) {
                            Text(
                              text = "${dayEvents.size}",
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (isSelected) Color.White else PrimaryIndigoLight
                            )
                          }
                        }
                      } else {
                        Spacer(modifier = Modifier.height(7.dp))
                      }
                    }
                  }
                } else {
                  // Empty padding slot
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }
    }

    // 4. Selected Day Agenda Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            tint = PrimaryIndigoLight,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "AGENDA: $selectedDayNumber $monthYearLabel",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = PrimaryIndigoLight
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PrimaryIndigo.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.35f))
        ) {
          Text(
            text = "${selectedDayEvents.size} Scheduled",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryIndigoLight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // 5. Events for Selected Date
    if (selectedDayEvents.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_empty_day_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 28.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.EventAvailable,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.size(36.dp)
            )
            Text(
              text = "No Events on $selectedDayNumber $monthYearLabel",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Tap any date with event dots on the calendar grid to inspect its schedule.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(selectedDayEvents, key = { it.id }) { event ->
        EventCard(
          event = event,
          onClick = { onEventClick(event) },
          onDelete = { onEventDelete(event) }
        )
      }
    }
  }
}
