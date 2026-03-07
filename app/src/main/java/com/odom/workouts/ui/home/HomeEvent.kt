package com.odom.workouts.ui.home

import com.odom.workouts.ui.SessionWrapper
import com.odom.workouts.utils.Event

sealed class HomeEvent : Event {
  data class SessionClicked(val sessionWrapper: SessionWrapper) : HomeEvent()
  object NewSession : HomeEvent()
  object OpenSettings : HomeEvent()
}