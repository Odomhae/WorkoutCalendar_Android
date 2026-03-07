package com.odom.workouts.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.odom.workouts.ui.SessionWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyWorkoutSummary(
  date: LocalDate,
  workouts: List<SessionWrapper>,
  onAddWorkout: () -> Unit,
  onWorkoutClick: (com.odom.workouts.db.entities.Session) -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
  
  Card(
    modifier = modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      // Date header
      Text(
        text = date.format(dateFormatter),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
      )
      
      if (workouts.isEmpty()) {
        EmptyWorkoutDay(onAddWorkout = onAddWorkout)
      } else {
        WorkoutDayList(
          workouts = workouts,
          onWorkoutClick = onWorkoutClick,
          onAddWorkout = onAddWorkout
        )
      }
    }
  }
}

@Composable
private fun EmptyWorkoutDay(
  onAddWorkout: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "No workouts scheduled for this day",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    Button(
      onClick = onAddWorkout
    ) {
      Text("Add Workout")
    }
  }
}

@Composable
private fun WorkoutDayList(
  workouts: List<SessionWrapper>,
  onWorkoutClick: (com.odom.workouts.db.entities.Session) -> Unit,
  onAddWorkout: () -> Unit
) {
  Column {
    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.heightIn(max = 300.dp)
    ) {
      items(workouts) { workout ->
        WorkoutDayItem(
          workout = workout,
          onWorkoutClick = onWorkoutClick
        )
      }
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Button(
      onClick = onAddWorkout,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Add Another Workout")
    }
  }
}

@Composable
private fun WorkoutDayItem(
  workout: SessionWrapper,
  onWorkoutClick: (com.odom.workouts.db.entities.Session) -> Unit
) {
  val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
  val duration = if (workout.session.end != null) {
    val duration = java.time.Duration.between(workout.session.start, workout.session.end)
    "${duration.toMinutes()} min"
  } else {
    "In progress"
  }

  
  Card(
    modifier = Modifier.fillMaxWidth(),
    onClick = { onWorkoutClick(workout.session) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = workout.session.start.format(timeFormatter),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium
        )
        
        Text(
          text = if (workout.muscleGroups.isNotEmpty()) {
            workout.muscleGroups.joinToString(", ") { it.toString() }
          } else {
            "No exercises recorded"
          },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      
      Text(
        text = duration,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
