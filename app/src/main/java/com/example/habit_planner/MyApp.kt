package com.example.habit_planner

import android.app.Application
import androidx.room.Room
import com.example.habit_planner.data.AppDatabase
import com.example.habit_planner.data.RoutineRepository

class MyApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "routine_DB")
            .build()
    }
    val repository: RoutineRepository by lazy {
        RoutineRepository(database.routineDao())
    }
}
