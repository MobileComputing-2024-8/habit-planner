package com.example.habit_planner

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.habit_planner.databinding.ActivityRunningBinding

class PlayRoutineActivity:AppCompatActivity() {
    lateinit var binding: ActivityRunningBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityRunningBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val routineName= intent.getStringExtra("routine").toString()
        Log.d("TAG",routineName)
    }
}
