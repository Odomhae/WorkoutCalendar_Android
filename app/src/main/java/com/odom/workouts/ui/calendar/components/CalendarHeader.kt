package com.odom.workouts.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarHeader(
  currentMonth: YearMonth,
  onPreviousMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onToday: () -> Unit,
  modifier: Modifier = Modifier
) {
  val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
  
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = onPreviousMonth) {
      Icon(
        imageVector = Icons.Default.ChevronLeft,
        contentDescription = "Previous month"
      )
    }
    
    Text(
      text = currentMonth.format(monthFormatter),
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold
    )
    
    IconButton(onClick = onNextMonth) {
      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = "Next month"
      )
    }
  }
  
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    Button(
      onClick = onToday,
      modifier = Modifier.padding(horizontal = 8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Today,
        contentDescription = "Go to today",
        modifier = Modifier.width(16.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text("Today")
    }
  }
}
