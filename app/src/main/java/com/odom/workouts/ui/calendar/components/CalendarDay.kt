package com.odom.workouts.ui.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun CalendarDay(
  date: LocalDate,
  isSelected: Boolean,
  hasWorkout: Boolean,
  isToday: Boolean,
  isCurrentMonth: Boolean,
  onDateSelected: (LocalDate) -> Unit,
  modifier: Modifier = Modifier,
  intensity: Int = 0
) {
  val backgroundColor = when {
    isSelected -> MaterialTheme.colorScheme.primaryContainer
    isToday -> MaterialTheme.colorScheme.secondaryContainer
    else -> Color.Transparent
  }
  
  val textColor = when {
    !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
    isToday -> MaterialTheme.colorScheme.onSecondaryContainer
    else -> MaterialTheme.colorScheme.onSurface
  }

  // Intensity color mapping
  val intensityColor = when (intensity) {
    1 -> Color(0xFFE8F5E9) // Very Low - Light Green
    2 -> Color(0xFFA5D6A7) // Low - Green
    3 -> Color(0xFF66BB6A) // Medium - Medium Green
    4 -> Color(0xFF43A047) // High - Dark Green
    5 -> Color(0xFF1B5E20) // Very High - Deep Green
    else -> MaterialTheme.colorScheme.primary // Default dot color if intensity is 0 but hasWorkout is true
  }

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clickable { onDateSelected(date) }
      .then(
        if (isSelected || isToday) {
          Modifier.background(
            color = backgroundColor,
            shape = MaterialTheme.shapes.small
          )
        } else Modifier
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = date.dayOfMonth.toString(),
      color = textColor,
      fontSize = 14.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      textAlign = TextAlign.Center
    )
    
    // Workout indicator dot with intensity color
    if (hasWorkout && isCurrentMonth) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .background(
            color = intensityColor,
            shape = CircleShape
          )
          .align(Alignment.BottomCenter)
      )
    }
  }
}
