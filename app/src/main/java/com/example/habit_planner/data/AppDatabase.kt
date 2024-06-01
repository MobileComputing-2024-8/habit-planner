package com.example.habit_planner.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Routine::class, Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}
