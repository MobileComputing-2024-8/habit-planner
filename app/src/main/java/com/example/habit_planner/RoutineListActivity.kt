package com.example.habit_planner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit_planner.databinding.ActivityRoutineListBinding

class RoutineListActivity :AppCompatActivity() {
    private lateinit var adapter: RoutineAdapter
    lateinit var binding: ActivityRoutineListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRoutineListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = RoutineAdapter(mutableListOf(), this::onEditRoutine)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 새 루틴 추가 버튼
        binding.addRoutineButton.setOnClickListener {
            val intent = Intent(this, AddRoutineActivity::class.java)
            startActivityForResult(intent, ADD_ROUTINE_REQUEST)
        }
    }

    private fun onEditRoutine(routineName: String, position: Int) {
        val intent = Intent(this, AddRoutineActivity::class.java)
        intent.putExtra("routine_name", routineName)
        intent.putExtra("routine_position", position)
        startActivityForResult(intent, EDIT_ROUTINE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val routineName = data?.getStringExtra("routine_name") ?: return
            when (requestCode) {
                ADD_ROUTINE_REQUEST -> {
                    adapter.addRoutine(routineName)
                }
                EDIT_ROUTINE_REQUEST -> {
                    val position = data.getIntExtra("routine_position", -1)
                    if (position != -1) {
                        adapter.updateRoutine(position, routineName)
                    }
                }
            }
        }
    }

    companion object {
        const val ADD_ROUTINE_REQUEST = 1
        const val EDIT_ROUTINE_REQUEST = 2
    }

    // 상태바에서 뒤로가기 버튼 눌렀을 때 로그 찍기 -> 로그 확인용
    override fun onSupportNavigateUp(): Boolean {
        val TAG = "Debug"
        Log.d(TAG,"Up Clicked")
        return super.onSupportNavigateUp()
    }
}