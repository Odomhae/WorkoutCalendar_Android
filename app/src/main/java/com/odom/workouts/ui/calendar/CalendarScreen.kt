package com.odom.workouts.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.odom.workouts.ui.calendar.components.CalendarHeader
import com.odom.workouts.ui.calendar.components.DailyWorkoutSummary
import com.odom.workouts.ui.calendar.components.WorkoutCalendar
import com.odom.workouts.utils.Routes
import com.odom.workouts.utils.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
  onNavigate: (UiEvent) -> Unit,
  modifier: Modifier = Modifier
) {
  val viewModel: CalendarViewModel = hiltViewModel()
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
  val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
  val workoutDates by viewModel.workoutDatesForMonth.collectAsStateWithLifecycle()
  val workouts by viewModel.workoutsForSelectedDate.collectAsStateWithLifecycle()
  
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
  
  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Workout Diary",
            fontWeight = FontWeight.Bold
          )
        },
        scrollBehavior = scrollBehavior
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Calendar section
      Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
      ) {
        Column {
          CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { viewModel.navigateToPreviousMonth() },
            onNextMonth = { viewModel.navigateToNextMonth() },
            onToday = { viewModel.goToToday() }
          )
          
          WorkoutCalendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            workoutDates = workoutDates,
            onDateSelected = { date -> viewModel.selectDate(date) }
          )
        }
      }
      
      // Daily workout summary
      DailyWorkoutSummary(
        date = selectedDate,
        workouts = workouts,
        onAddWorkout = {
          viewModel.viewModelScope.launch {
            val sessionId = viewModel.createWorkoutForSelectedDate()
            if (sessionId > 0) {
              onNavigate(UiEvent.Navigate("${Routes.SESSION}/$sessionId"))
            }
          }
        },
        onWorkoutClick = { session ->
          onNavigate(UiEvent.Navigate("${Routes.SESSION}/${session.sessionId}"))
        },
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
