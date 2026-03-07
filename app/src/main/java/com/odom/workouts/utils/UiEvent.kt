package com.odom.workouts.utils

import com.odom.workouts.ui.SetWrapper

sealed class UiEvent {
  data class OpenWebsite(val url: String) : UiEvent()
  data class Navigate(val route: String, val popBackStack: Boolean = false) : UiEvent()
  data class FileCreated(val fileName: String) : UiEvent()
  data class SetCreated(val set: SetWrapper) : UiEvent()

  object ToggleTimer : UiEvent()
  object ResetTimer : UiEvent()
  object IncrementTimer : UiEvent()
  object DecrementTimer : UiEvent()
}
