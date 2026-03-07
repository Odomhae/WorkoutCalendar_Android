package com.odom.workouts.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odom.workouts.db.GymRepository
import com.odom.workouts.db.UserPreferencesRepository
import com.odom.workouts.ui.SessionWrapper
import com.odom.workouts.utils.sortedListOfMuscleGroups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
  private val repo: GymRepository,
  private val prefsRepo: UserPreferencesRepository
) : ViewModel() {

  private val _selectedDate = MutableStateFlow(LocalDate.now())
  val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

  private val _currentMonth = MutableStateFlow(YearMonth.now())
  val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

  // Flow of dates that have workouts for the current month
  val workoutDatesForMonth: StateFlow<Set<LocalDate>> = currentMonth
    .flatMapLatest { month ->
      repo.getWorkoutDatesForMonth(month)
    }
    .stateIn(
      scope = viewModelScope,
      started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
      initialValue = emptySet()
    )

  // Workout data for selected date
  val workoutsForSelectedDate: StateFlow<List<SessionWrapper>> = selectedDate
    .flatMapLatest { date ->
      combine(
        repo.getSessionsForDate(date),
        repo.getAllSessionExercises(),
        prefsRepo.secondaryMuscleWeight
      ) { sessions, sessionExercises, secondaryWeight ->
        sessions.map { session ->
          val muscleGroups = sessionExercises
            .filter { it.sessionExercise.parentSessionId == session.sessionId }
            .sortedListOfMuscleGroups(secondaryWeight.toDouble())
          SessionWrapper(session, muscleGroups)
        }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  fun selectDate(date: LocalDate) {
    _selectedDate.value = date
    // Update current month if selected date is in different month
    if (YearMonth.from(date) != _currentMonth.value) {
      _currentMonth.value = YearMonth.from(date)
    }
  }

  fun navigateToPreviousMonth() {
    _currentMonth.value = _currentMonth.value.minusMonths(1)
  }

  fun navigateToNextMonth() {
    _currentMonth.value = _currentMonth.value.plusMonths(1)
  }

  fun goToToday() {
    val today = LocalDate.now()
    _selectedDate.value = today
    _currentMonth.value = YearMonth.from(today)
  }

  suspend fun createWorkoutForSelectedDate(): Long {
    return repo.createWorkoutForDate(_selectedDate.value)
  }
}
