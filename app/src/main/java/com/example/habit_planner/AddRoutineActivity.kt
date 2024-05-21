package com.example.habit_planner

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.habit_planner.databinding.ActivityAddRoutineBinding
import com.example.habit_planner.databinding.TaskItemBinding
import java.util.Calendar

class AddRoutineActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddRoutineBinding
    private var taskCount = 0 // task 갯수 파악
    private val maxTasks = 10 // 최대 task(할 일)
    private val tasks = mutableListOf("물 마시기", "침구 정리", "샤워", "아침 식사", "+추가") // 할 일 리스트
    private var routinePosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoutineBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        val routineName = intent.getStringExtra("routine_name")
        routinePosition = intent.getIntExtra("routine_position", -1)

        if (routineName != null) {
            binding.routineName.setText(routineName)
            binding.addRoutineButton.text = "루틴 수정"
        }

        // 루틴 시작 시간 설정
        binding.routineStartTime.setOnClickListener {
            showTimePickerDialog()
        }

        // 할 일 추가 버튼
        binding.addTaskButton.setOnClickListener {
            if (taskCount < maxTasks) {
                addTaskView()
                taskCount++
            } else {
                // 할 일이 10개가 최대
                Toast.makeText(this, "최대 10개의 할 일을 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 루틴 추가 버튼
        binding.addRoutineButton.setOnClickListener {
            val routineName = binding.routineName.text.toString()
            val resultIntent = Intent()
            resultIntent.putExtra("routine_name", routineName)
            resultIntent.putExtra("routine_position", routinePosition)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    // 루틴 시작 시간 - timepicker 옵션
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.THEME_HOLO_LIGHT,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.routineStartTime.setText(time)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    // spiner관련 코드
    private fun setupSpinner(binding: TaskItemBinding) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tasks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTask.adapter = adapter

        binding.spinnerTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (tasks[position] == "+추가") {
                    // +추가 클릭 시 사용가가 입력해서 새로운 할 일 리스트에 추가 가능
                    showAddTaskDialog(binding.spinnerTask)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // +추가 클릭 시 사용자에게 입력 받는 Dialog
    private fun showAddTaskDialog(spinner: Spinner) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("새 할 일 추가")

        val input = EditText(this)
        input.hint = "할 일 입력"
        builder.setView(input)

        builder.setPositiveButton("추가") { dialog, _ ->
            val newTask = input.text.toString()
            if (newTask.isNotEmpty()) {
                tasks.add(tasks.size - 1, newTask)
                (spinner.adapter as ArrayAdapter<String>).notifyDataSetChanged()
                spinner.setSelection(tasks.indexOf(newTask))
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            spinner.setSelection(0)
            dialog.cancel()
        }

        builder.show()
    }

    // 할 일(task) 1개 추가
    private fun addTaskView() {
        val taskBinding = TaskItemBinding.inflate(LayoutInflater.from(this), binding.taskContainer, false)

        setupSpinner(taskBinding)
        // 현재는 분단위로 표시 -> 수정해야 함
        var taskTime = 1

        // 시간 감소
        taskBinding.minusButton.setOnClickListener {
            if (taskTime > 1) {
                taskTime--
                taskBinding.timeText.text = "${taskTime}분"
            }
        }
        // 시간 증가
        taskBinding.plusButton.setOnClickListener {
            taskTime++
            taskBinding.timeText.text = "${taskTime}분"
        }

        // 할 일(task) 1개 삭제
        taskBinding.deleteButton.setOnClickListener {
            binding.taskContainer.removeView(taskBinding.root)
            taskCount--
        }

        binding.taskContainer.addView(taskBinding.root)
    }

    // 상태바에서 뒤로가기 버튼 눌렀을 때 로그 찍기 -> 로그 확인용
    override fun onSupportNavigateUp(): Boolean {
        val TAG = "Debug"
        Log.d(TAG, "Up Clicked")
        return super.onSupportNavigateUp()
    }
}
