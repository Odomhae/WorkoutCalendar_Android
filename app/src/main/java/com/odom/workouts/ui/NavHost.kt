package com.odom.workouts.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.odom.workouts.ui.calendar.CalendarScreen
import com.odom.workouts.ui.exercisepicker.ExercisePickerScreen
import com.odom.workouts.ui.home.HomeScreen
import com.odom.workouts.ui.session.SessionScreen
import com.odom.workouts.ui.settings.SettingsScreen
import com.odom.workouts.utils.Routes
import com.odom.workouts.utils.UiEvent

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavHost(
  navController: NavHostController
) {

  SharedTransitionLayout {
    NavHost(
      navController = navController,
      startDestination = Routes.CALENDAR,
    ) {

      composable(Routes.CALENDAR) {
        CalendarScreen(
          onNavigate = { navController.navigationEvent(event = it ) }
        )
      }

      composable(Routes.HOME) {
        HomeScreen(
          onNavigate = { navController.navigationEvent(event = it) },
          animatedVisibilityScope = this@composable,
          sharedTransitionScope = this@SharedTransitionLayout
        )
      }
      composable(
        route = "${Routes.SESSION}/{session_id}",
        arguments = listOf(
          navArgument("session_id") {
            type = NavType.LongType
          }
        )
      ) {
        SessionScreen(
          onNavigate = { navController.navigationEvent(event = it) },
          animatedVisibilityScope = this@composable,
          sharedTransitionScope = this@SharedTransitionLayout
        )
      }
      composable(
        route = "${Routes.EXERCISE_PICKER}/{session_id}",
        arguments = listOf(
          navArgument("session_id") {
            type = NavType.LongType
          }
        )
      ) {
        ExercisePickerScreen(
          navController = navController,
          animatedVisibilityScope = this@composable,
          sharedTransitionScope = this@SharedTransitionLayout
        )
      }
      composable(Routes.SETTINGS) {
        SettingsScreen(
          onNavigate = { navController.navigationEvent(event = it) },
        )
      }
    }
  }
}

fun NavController.navigationEvent(event: UiEvent.Navigate) {
  navigate(event.route) {
    if (event.popBackStack) currentDestination?.route?.let { popUpTo(it) { inclusive = true } }
    launchSingleTop = true
  }
}
