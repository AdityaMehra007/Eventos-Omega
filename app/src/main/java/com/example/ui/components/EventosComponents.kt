package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun EventosAppHeader(
  currentRole: UserRole,
  onRoleClick: () -> Unit,
  onAIAssistantClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(PrimaryIndigo, AccentCyan))
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Ω",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
          )
        }
        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "EVENTOS OMEGA",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(AccentAmber.copy(alpha = 0.2f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text(
                text = "BLR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentAmber
              )
            }
          }
          Text(
            text = "Event Economy Operating System",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // AI Command quick button
        IconButton(
          onClick = onAIAssistantClick,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(PrimaryIndigo.copy(alpha = 0.15f))
            .testTag("ai_assistant_top_btn")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Command Suite",
            tint = PrimaryIndigoLight,
            modifier = Modifier.size(20.dp)
          )
        }

        // Role Switcher pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onRoleClick)
            .testTag("role_switcher_pill")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess)
            )
            Text(
              text = currentRole.badge,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = "Switch View Role",
              modifier = Modifier.size(14.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = accentColor,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun StatusBadge(
  text: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(color.copy(alpha = 0.18f))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun BangaloreZoneChip(
  zone: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  FilterChip(
    selected = isSelected,
    onClick = onClick,
    label = {
      Text(text = zone, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    },
    leadingIcon = if (isSelected) {
      {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
          modifier = Modifier.size(14.dp)
        )
      }
    } else null,
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = PrimaryIndigo.copy(alpha = 0.25f),
      selectedLabelColor = PrimaryIndigoLight
    ),
    modifier = modifier
  )
}
