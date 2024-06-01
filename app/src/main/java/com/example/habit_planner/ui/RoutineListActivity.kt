package com.example.habit_planner.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit_planner.MyApp
import com.example.habit_planner.R
import com.example.habit_planner.databinding.ActivityRoutineListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RoutineListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutineListBinding
    private val routineViewModel: RoutineViewModel by viewModels {
        RoutineViewModelFactory((application as MyApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutineListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            routineViewModel.getRoutines().collect { routines ->
                binding.recyclerView.adapter = RoutineListAdapter(routines, { routine ->
                    val intent = Intent(this@RoutineListActivity, AddRoutineActivity::class.java).apply {
                        putExtra("ROUTINE_ID", routine.id)
                    }
                    startActivity(intent)
                }, { routine ->
                    startRunningActivity(routine.id)
                }, { routine ->
                    lifecycleScope.launch {
                        routineViewModel.deleteRoutine(routine)
                    }
                })
            }
        }

        binding.addRoutineButton.setOnClickListener {
            startActivity(Intent(this, AddRoutineActivity::class.java))
        }
    }

    private fun startRunningActivity(routineId: Int) {
        lifecycleScope.launch {
            routineViewModel.getTasksForRoutine(routineId).collect { tasks ->
                val intent = Intent(this@RoutineListActivity, RunningActivity::class.java).apply {
                    putParcelableArrayListExtra("TASKS", ArrayList(tasks))
                }
                startActivity(intent)
            }
        }
    }
}
