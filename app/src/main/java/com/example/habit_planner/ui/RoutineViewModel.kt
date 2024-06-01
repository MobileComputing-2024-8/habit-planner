package com.example.habit_planner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit_planner.data.Routine
import com.example.habit_planner.data.RoutineRepository
import com.example.habit_planner.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RoutineViewModel(private val repository: RoutineRepository) : ViewModel() {
    fun addRoutine(routine: Routine, tasks: List<Task>) {
        viewModelScope.launch {
            repository.addRoutine(routine, tasks)
        }
    }

    fun getRoutines(): Flow<List<Routine>> {
        return repository.getRoutines()
    }

    fun getTasksForRoutine(routineId: Int): Flow<List<Task>> {
        return repository.getTasksForRoutine(routineId)
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }
}
