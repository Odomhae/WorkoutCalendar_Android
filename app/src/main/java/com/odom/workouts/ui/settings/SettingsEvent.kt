package com.odom.workouts.ui.settings

import com.odom.workouts.utils.Event

sealed class SettingsEvent : Event {
  object ClearDatabase: SettingsEvent()
}
