package com.example.habit_planner

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.habit_planner.databinding.ActivityPlayRoutineBinding

class PlayRoutineActivity: AppCompatActivity() {
    lateinit var binding: ActivityPlayRoutineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayRoutineBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.test.text = intent.getStringExtra("routine_name")
    }
}