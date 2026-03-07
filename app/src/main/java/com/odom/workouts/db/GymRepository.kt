package com.odom.workouts.db

import android.content.Context
import com.odom.workouts.db.entities.*
import com.odom.workouts.ui.DatabaseModel
import com.odom.workouts.ui.ExerciseWrapper
import com.odom.workouts.ui.SessionWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.collections.filter
import kotlin.collections.mapNotNull
import kotlin.collections.sortedByDescending


class GymRepository(
  private val dao: GymDAO,
  private val database: GymDatabase
) {
  private val DB_NAME = "gym_database.db"

  fun checkpoint() {
    // This merges the -wal file into the .db file without closing the connection
    database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use {
      it.moveToFirst()
    }
  }

  fun checkpointAndClose() {
    if (database.isOpen) {
      database.close()
    }
  }

  fun getDatabaseFile(context: Context): File {
    return context.getDatabasePath(DB_NAME)
  }

  fun getSessionById(sessionId: Long) = dao.getSessionById(sessionId)
  fun getSetById(setId: Long) = dao.getSetById(setId)

  fun getAllSessions() = dao.getAllSessions()

  fun getAllSets() = dao.getAllSets()
  fun getAllExercises() = dao.getAllExercises()
  fun getAllExercisesWithSessionCount() = dao.getAllExercisesWithSessionCount()
  fun getLastSession() = dao.getLastSession()

  fun getAllSessionExercises() = dao.getAllSessionExercises()

  suspend fun updateSessionExercises(exercises: List<SessionExercise>) =
    dao.updateSessionExercises(exercises)

  @OptIn(ExperimentalCoroutinesApi::class)
  fun getExercisesForSession(session: Flow<Session>): Flow<List<SessionExerciseWithExercise>> {
    return session.flatMapLatest {
      dao.getExercisesForSession(it.sessionId)
    }
  }

  suspend fun getHistoryForExercise(exercise: Exercise): List<Pair<SessionWrapper, ExerciseWrapper>> {
    return withContext(Dispatchers.IO) {
      val allSessionExercises = getAllSessionExercises().first()
      val relevantSessionExercises = allSessionExercises.filter { it.exercise.id == exercise.id }

      relevantSessionExercises
        .map { sessionExercise ->
          getSessionById(sessionExercise.sessionExercise.parentSessionId).let { session ->
            val sets =
              getSetsForExercise(sessionExercise.sessionExercise.sessionExerciseId).first()
            val sessionWrapper = SessionWrapper(session, emptyList())
            val exerciseWrapper = ExerciseWrapper(
              sessionExercise = sessionExercise.sessionExercise, exercise = exercise, sets = sets
            )

            sessionWrapper to exerciseWrapper
          }
        }
        .sortedByDescending { it.first.session.start }
    }
  }

  fun getExercisesForSession(session: Session): Flow<List<SessionExerciseWithExercise>> {
    Timber.d("Retrieving exercises for session: $session")
    return dao.getExercisesForSession(session.sessionId)
  }

  fun getSetsForExercise(sessionExerciseId: Long) = dao.getSetsForExercise(sessionExerciseId)


  suspend fun insertExercise(exercise: Exercise) = dao.insertExercise(exercise)

  suspend fun insertSession(session: Session) = dao.insertSession(session)

  suspend fun removeSession(session: Session) = dao.removeSession(session)

  suspend fun updateSession(session: Session) = dao.updateSession(session)

  suspend fun insertSessionExercise(sessionExercise: SessionExercise): Long {
    return withContext(Dispatchers.IO) {

      val session = getSessionById(sessionExercise.parentSessionId)
      val exerciseOrder =
        getExercisesForSession(session).first().maxOfOrNull { it.sessionExercise.exerciseOrder }
          ?.let {
            it + 1
          } ?: 0

      dao.insertSessionExercise(sessionExercise.copy(exerciseOrder = exerciseOrder))
    }
  }

  suspend fun removeSessionExercise(sessionExercise: SessionExercise) =
    dao.removeSessionExercise(sessionExercise)

  suspend fun insertSet(gymSet: GymSet) = dao.insertSet(gymSet)

  suspend fun updateSet(set: GymSet) = dao.updateSet(set)
  suspend fun deleteSet(set: GymSet) = dao.deleteSet(set)

  suspend fun createSet(sessionExercise: SessionExercise) =
    dao.insertSet(GymSet(parentSessionExerciseId = sessionExercise.sessionExerciseId))

  fun getDatabaseModel() =
    DatabaseModel(
      sessions = dao.getSessionList(),
      exercises = dao.getExerciseList(),
      sessionExercises = dao.getSessionExerciseList(),
      sets = dao.getSetList()
    )

  suspend fun clearDatabase() {
    dao.clearSessions()
    dao.clearSessionExercises()
    dao.clearExercises()
    dao.clearSets()
  }

  // Calendar-specific methods
  fun getWorkoutDatesForMonth(yearMonth: java.time.YearMonth): kotlinx.coroutines.flow.Flow<Set<java.time.LocalDate>> {
    val startDate = yearMonth.atDay(1).toString()
    val endDate = yearMonth.atEndOfMonth().toString()
    
    return dao.getWorkoutDatesInRange(startDate, endDate)
      .map { dateStrings ->
        dateStrings.mapNotNull { dateString ->
          try {
            java.time.LocalDate.parse(dateString)
          } catch (e: Exception) {
            null
          }
        }.toSet()
      }
  }

  fun getSessionsForDate(date: java.time.LocalDate): kotlinx.coroutines.flow.Flow<List<com.odom.workouts.db.entities.Session>> {
    return dao.getSessionsForDate(date.toString())
  }

  suspend fun getWorkoutCountForDate(date: java.time.LocalDate): Int {
    return dao.getWorkoutCountForDate(date.toString())
  }

  suspend fun createWorkoutForDate(date: java.time.LocalDate): Long {
    val sessionStart = date.atStartOfDay()
    val session = com.odom.workouts.db.entities.Session(start = sessionStart)
    return insertSession(session)
  }
}
