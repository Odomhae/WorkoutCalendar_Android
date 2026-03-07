package com.odom.workouts.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutCalendar(
  selectedDate: LocalDate,
  currentMonth: YearMonth,
  workoutDates: Set<LocalDate>,
  onDateSelected: (LocalDate) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Day headers
    CalendarDayHeaders()
    
    // Calendar grid
    CalendarDaysGrid(
      selectedDate = selectedDate,
      currentMonth = currentMonth,
      workoutDates = workoutDates,
      onDateSelected = onDateSelected
    )
  }
}

@Composable
private fun CalendarDayHeaders() {
  val daysOfWeek = DayOfWeek.values()
  
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    daysOfWeek.forEach { dayOfWeek ->
      Text(
        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .weight(1f)
          .padding(vertical = 8.dp)
      )
    }
  }
}

@Composable
private fun CalendarDaysGrid(
  selectedDate: LocalDate,
  currentMonth: YearMonth,
  workoutDates: Set<LocalDate>,
  onDateSelected: (LocalDate) -> Unit
) {
  val firstDayOfMonth = currentMonth.atDay(1)
  val lastDayOfMonth = currentMonth.atEndOfMonth()
  val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust for Sunday start
  val daysInMonth = lastDayOfMonth.dayOfMonth
  val today = LocalDate.now()
  
  // Calculate total weeks needed
  val totalCells = firstDayOfWeek + daysInMonth
  val weeksNeeded = (totalCells + 6) / 7 // Round up to nearest week
  
  Column {
    repeat(weeksNeeded) { weekIndex ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        repeat(7) { dayIndex ->
          val cellNumber = weekIndex * 7 + dayIndex
          val dayOfMonth = cellNumber - firstDayOfWeek + 1
          
          if (cellNumber >= firstDayOfWeek && dayOfMonth <= daysInMonth) {
            val date = currentMonth.atDay(dayOfMonth)
            val isSelected = date.isEqual(selectedDate)
            val isToday = date.isEqual(today)
            val hasWorkout = workoutDates.contains(date)
            val isCurrentMonth = YearMonth.from(date) == currentMonth
            
            CalendarDay(
              date = date,
              isSelected = isSelected,
              hasWorkout = hasWorkout,
              isToday = isToday,
              isCurrentMonth = isCurrentMonth,
              onDateSelected = onDateSelected,
              modifier = Modifier.weight(1f)
            )
          } else {
            // Empty cell for days outside current month
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}
