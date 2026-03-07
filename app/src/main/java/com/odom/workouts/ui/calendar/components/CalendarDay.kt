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
  modifier: Modifier = Modifier
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
    
    // Workout indicator dot
    if (hasWorkout && isCurrentMonth) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
          )
          .align(Alignment.BottomCenter)
      )
    }
  }
}
