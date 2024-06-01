package com.example.habit_planner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoutineRepository(private val routineDao: RoutineDao) {
    suspend fun addRoutine(routine: Routine, tasks: List<Task>) {
        withContext(Dispatchers.IO) {
            val routineId = routineDao.insertRoutine(routine)
            tasks.forEach { task ->
                routineDao.insertTask(task.copy(routineId = routineId.toInt()))
            }
        }
    }

    fun getRoutines() = routineDao.getAllRoutines()

    fun getTasksForRoutine(routineId: Int) = routineDao.getTasksForRoutine(routineId)

    suspend fun deleteRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            routineDao.deleteRoutine(routine)
        }
    }
}
