package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Event
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data model representing a slice of the Event Status Distribution chart.
 */
data class StatusSlice(
  val status: String,
  val label: String,
  val count: Int,
  val percentage: Float,
  val color: Color,
  val icon: ImageVector,
  val description: String
)

/**
 * EventStatisticsScreen visualizes the distribution of events by status
 * (Upcoming, In-Progress, Completed) with an animated interactive Pie/Donut Chart,
 * dynamic legend, percentage breakdowns, and analytical metrics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventStatisticsScreen(
  viewModel: EventViewModel,
  onNavigateBack: () -> Unit = {},
  onFilterByStatus: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val events by viewModel.events.collectAsStateWithLifecycle()

  // Calculate status counts & distribution
  val upcomingCount = remember(events) {
    events.count {
      it.status.equals("Upcoming", ignoreCase = true) ||
        it.status.equals("CONFIRMED", ignoreCase = true) ||
        it.status.equals("PLANNING", ignoreCase = true) ||
        it.status.equals("SCHEDULED", ignoreCase = true)
    }
  }

  val inProgressCount = remember(events) {
    events.count {
      it.status.equals("In-Progress", ignoreCase = true) ||
        it.status.equals("LIVE", ignoreCase = true) ||
        it.status.equals("IN_PROGRESS", ignoreCase = true) ||
        it.status.equals("IN PROGRESS", ignoreCase = true)
    }
  }

  val completedCount = remember(events) {
    events.count {
      it.status.equals("Completed", ignoreCase = true) ||
        it.status.equals("FINANCIAL_SETTLED", ignoreCase = true) ||
        it.status.equals("SETTLED", ignoreCase = true)
    }
  }

  val totalCount = events.size

  val slices = remember(upcomingCount, inProgressCount, completedCount, totalCount) {
    if (totalCount == 0) {
      emptyList()
    } else {
      listOf(
        StatusSlice(
          status = "Upcoming",
          label = "Upcoming",
          count = upcomingCount,
          percentage = (upcomingCount.toFloat() / totalCount) * 100f,
          color = AccentAmber,
          icon = Icons.Default.Upcoming,
          description = "Scheduled, confirmed, and planning pipeline"
        ),
        StatusSlice(
          status = "In-Progress",
          label = "In-Progress",
          count = inProgressCount,
          percentage = (inProgressCount.toFloat() / totalCount) * 100f,
          color = CrimsonAlert,
          icon = Icons.Default.Sensors,
          description = "Actively live on site with real-time operations"
        ),
        StatusSlice(
          status = "Completed",
          label = "Completed",
          count = completedCount,
          percentage = (completedCount.toFloat() / totalCount) * 100f,
          color = PrimaryIndigoLight,
          icon = Icons.Default.TaskAlt,
          description = "Successfully executed & financial reconciliation"
        )
      )
    }
  }

  var selectedSlice by remember { mutableStateOf<StatusSlice?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = DarkSurfaceBase,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Event Analytics",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Black,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Status Distribution & Room SQLite Metrics",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("stats_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        actions = {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = PrimaryIndigo.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.4f)),
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(EmeraldSuccess, shape = CircleShape)
              )
              Text(
                text = "$totalCount Events Stored",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryIndigoLight
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = DarkSurfaceElevated
        ),
        modifier = Modifier.testTag("event_stats_top_bar")
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
    ) {
      // Main Pie Chart Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = BorderStroke(1.dp, PrimaryIndigoLight.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pie_chart_card")
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                  imageVector = Icons.Default.PieChart,
                  contentDescription = null,
                  tint = PrimaryIndigoLight,
                  modifier = Modifier.size(22.dp)
                )
                Text(
                  text = "STATUS BREAKDOWN",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp,
                  color = PrimaryIndigoLight
                )
              }

              Text(
                text = "Tap slices for details",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            if (totalCount == 0) {
              // Empty State
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.EventBusy,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(48.dp)
                )
                Text(
                  text = "No Events in Room Database",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Add events to visualize the distribution chart.",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            } else {
              // Interactive Animated Pie/Donut Chart
              EventStatusPieChart(
                slices = slices,
                selectedSlice = selectedSlice,
                onSliceSelected = { slice ->
                  selectedSlice = if (selectedSlice == slice) null else slice
                },
                modifier = Modifier
                  .size(260.dp)
                  .testTag("event_status_pie_chart")
              )

              // Active Selected Slice Banner
              selectedSlice?.let { slice ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = slice.color.copy(alpha = 0.15f),
                  border = BorderStroke(1.dp, slice.color.copy(alpha = 0.4f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterByStatus(slice.status) }
                    .testTag("selected_slice_card")
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Icon(
                        imageVector = slice.icon,
                        contentDescription = null,
                        tint = slice.color,
                        modifier = Modifier.size(24.dp)
                      )
                      Column {
                        Text(
                          text = "${slice.label}: ${slice.count} events (${String.format("%.1f", slice.percentage)}%)",
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          text = slice.description,
                          fontSize = 11.sp,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Icon(
                      imageVector = Icons.Default.ChevronRight,
                      contentDescription = "Filter",
                      tint = slice.color
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Status Legend & Distribution Cards
      item {
        Text(
          text = "CATEGORY DISTRIBUTION",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      items(slices.size) { index ->
        val slice = slices[index]
        val isSelected = selectedSlice == slice

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) slice.color else slice.color.copy(alpha = 0.25f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              selectedSlice = if (isSelected) null else slice
            }
            .testTag("status_legend_item_${slice.status}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = slice.color.copy(alpha = 0.18f),
                modifier = Modifier.size(40.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = slice.icon,
                    contentDescription = null,
                    tint = slice.color,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = slice.label,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = slice.description,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Progress Bar representation
                LinearProgressIndicator(
                  progress = { if (totalCount > 0) slice.count.toFloat() / totalCount else 0f },
                  color = slice.color,
                  trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                  modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                )
              }
            }

            Column(
              horizontalAlignment = Alignment.End,
              verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              Text(
                text = "${slice.count}",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = slice.color
              )
              Text(
                text = "${String.format("%.1f", slice.percentage)}%",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Operational Insights Metric Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("operational_insights_card")
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "DATABASE METRICS & HEALTH",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = AccentCyan
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              StatPill(
                title = "Total Events",
                value = "$totalCount",
                subtitle = "SQLite records",
                color = PrimaryIndigoLight,
                modifier = Modifier.weight(1f)
              )
              StatPill(
                title = "Active Live",
                value = "$inProgressCount",
                subtitle = "Real-time command",
                color = CrimsonAlert,
                modifier = Modifier.weight(1f)
              )
              StatPill(
                title = "Completed",
                value = "$completedCount",
                subtitle = "Reconciled",
                color = EmeraldSuccess,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Animated Interactive Pie / Donut Chart drawn on Android Canvas.
 * Supports sweep animation, highlighted slice selection, and center text.
 */
@Composable
fun EventStatusPieChart(
  slices: List<StatusSlice>,
  selectedSlice: StatusSlice?,
  onSliceSelected: (StatusSlice) -> Unit,
  modifier: Modifier = Modifier
) {
  val animationProgress = remember { Animatable(0f) }

  LaunchedEffect(slices) {
    animationProgress.snapTo(0f)
    animationProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
    )
  }

  val total = slices.sumOf { it.count }.toFloat().coerceAtLeast(1f)

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(slices) {
          detectTapGestures { offset ->
            val center = Offset(size.width / 2f, size.height / 2f)
            val dx = offset.x - center.x
            val dy = offset.y - center.y
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
            val outerRadius = (size.width.coerceAtMost(size.height) / 2f)
            val innerRadius = outerRadius * 0.55f

            if (distance in innerRadius..outerRadius) {
              var touchAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
              if (touchAngle < 0) touchAngle += 360f

              var currentAngle = -90f
              for (slice in slices) {
                val sweep = (slice.count / total) * 360f
                val start = (currentAngle + 360f) % 360f
                val end = (start + sweep) % 360f

                val isWithin = if (start < end) {
                  touchAngle in start..end
                } else {
                  touchAngle >= start || touchAngle <= end
                }

                if (isWithin) {
                  onSliceSelected(slice)
                  break
                }
                currentAngle += sweep
              }
            }
          }
        }
    ) {
      val canvasSize = size.minDimension
      val strokeWidth = canvasSize * 0.22f
      val radius = (canvasSize - strokeWidth) / 2f
      val center = Offset(size.width / 2f, size.height / 2f)

      var startAngle = -90f

      slices.forEach { slice ->
        val sweepAngle = ((slice.count / total) * 360f) * animationProgress.value
        val isSelected = selectedSlice == slice

        if (sweepAngle > 0f) {
          drawArc(
            color = if (isSelected) slice.color else slice.color.copy(alpha = 0.85f),
            startAngle = startAngle,
            sweepAngle = sweepAngle - 2f, // slight gap between slices
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
              width = if (isSelected) strokeWidth * 1.15f else strokeWidth,
              cap = StrokeCap.Round
            )
          )
        }
        startAngle += (slice.count / total) * 360f
      }
    }

    // Donut Center Content
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (selectedSlice != null) {
        Text(
          text = "${selectedSlice.count}",
          fontSize = 28.sp,
          fontWeight = FontWeight.Black,
          color = selectedSlice.color
        )
        Text(
          text = selectedSlice.label,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${String.format("%.1f", selectedSlice.percentage)}%",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      } else {
        Text(
          text = "${total.toInt()}",
          fontSize = 30.sp,
          fontWeight = FontWeight.Black,
          color = PrimaryIndigoLight
        )
        Text(
          text = "Total Events",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun StatPill(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
      Text(text = subtitle, fontSize = 9.sp, color = TextMutedDark)
    }
  }
}
