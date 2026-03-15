package com.odom.workouts.ui.calendar

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.odom.workouts.ui.calendar.components.CalendarHeader
import com.odom.workouts.ui.calendar.components.DailyWorkoutSummary
import com.odom.workouts.ui.calendar.components.WorkoutCalendar
import com.odom.workouts.ui.home.HomeEvent
import com.odom.workouts.ui.home.HomeViewModel
import com.odom.workouts.utils.Routes
import com.odom.workouts.utils.UiEvent
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarScreen(
  onNavigate: (UiEvent.Navigate) -> Unit,
  modifier: Modifier = Modifier,
  homeViewModel: HomeViewModel = hiltViewModel()
) {
  val viewModel: CalendarViewModel = hiltViewModel()
  val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
  val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
  val workoutDates by viewModel.workoutDatesForMonth.collectAsStateWithLifecycle()
  val workouts by viewModel.workoutsForSelectedDate.collectAsStateWithLifecycle()
  
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
  val greeting = homeViewModel.greeting
  val tagline by homeViewModel.tagline.collectAsState()

  LaunchedEffect(true) {
    homeViewModel.uiEvent.collect { event ->
      when (event) {
        is UiEvent.Navigate -> onNavigate(event)
        else -> Unit
      }
    }
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
      // todo jihoon    expandedHeight = expandedHeight,
        scrollBehavior = scrollBehavior,
        contentPadding = PaddingValues(0.dp),
        title = {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars) // Prevents overlapping status bar
              .padding(start = 8.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.Top
          ) {
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
              )
              IconButton(onClick = { homeViewModel.onEvent(HomeEvent.OpenSettings) }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
              }
            }
            Text(
              text = tagline,
              style = MaterialTheme.typography.labelLargeEmphasized,
              color = MaterialTheme.colorScheme.secondary,
            )

            // Using a Spacer with the expanded height effectively pushes
            // the content above it to the top of the internal layout box.
         // todo jihoon    Spacer(modifier = Modifier.height(expandedHeight))
          }
        }
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
        Column(
          modifier = Modifier
            .pointerInput(Unit) {
              var totalDragX = 0f
              detectDragGestures(
                onDragStart = { totalDragX = 0f },
                onDragEnd = {
                  val threshold = 100f
                  if (abs(totalDragX) > threshold) {
                    when {
                      totalDragX > 0 -> {
                        // Swipe right - previous month
                        viewModel.navigateToPreviousMonth()
                      }
                      totalDragX < 0 -> {
                        // Swipe left - next month
                        viewModel.navigateToNextMonth()
                      }
                    }
                  }
                }
              ) { _, dragAmount ->
                if (abs(dragAmount.x) > abs(dragAmount.y)) {
                  totalDragX += dragAmount.x
                }
              }
            }
        ) {
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
              onNavigate(UiEvent.Navigate("${Routes.SESSION}/$sessionId")) // EXERCISE_PICKER
            }
          }
        },
        onWorkoutClick = { sessionExercise ->
          onNavigate(UiEvent.Navigate("${Routes.SESSION}/${sessionExercise.parentSessionId}"))
        },
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
