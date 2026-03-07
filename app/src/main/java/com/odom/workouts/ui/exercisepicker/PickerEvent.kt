package com.odom.workouts.ui.exercisepicker

import com.odom.workouts.db.entities.Exercise
import com.odom.workouts.utils.Event

sealed class PickerEvent : Event {
  data class OpenGuide(val exercise: Exercise) : PickerEvent()
  data class ToggleSelectExercise(val exercise: Exercise) : PickerEvent()
  data class ToggleSelectMuscle(val muscle: String) : PickerEvent()
  data class ToggleSelectEquipment(val equipment: String) : PickerEvent()
  data class UpdateSearchText(val text: String) : PickerEvent()
  object FilterSelected : PickerEvent()
  object DeselectFilters : PickerEvent()
  object AddExercises : PickerEvent()
}
