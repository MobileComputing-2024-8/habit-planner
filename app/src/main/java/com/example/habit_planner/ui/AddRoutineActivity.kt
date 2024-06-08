package com.example.habit_planner.ui

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habit_planner.MyApp
import com.example.habit_planner.R
import com.example.habit_planner.data.Routine
import com.example.habit_planner.data.Task
import com.example.habit_planner.databinding.ActivityAddRoutineBinding
import com.example.habit_planner.databinding.TaskItemBinding
import com.example.habit_planner.ui.AlarmReceiver.Companion.CHANNEL_ID
import kotlinx.coroutines.launch
import java.util.Calendar

class AddRoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRoutineBinding
    private val routineViewModel: RoutineViewModel by viewModels {
        RoutineViewModelFactory((application as MyApp).repository)
    }

    private val tasks = mutableListOf<Task>()
    private var routineId: Int? = null
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRoutineBinding.inflate(layoutInflater)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setContentView(binding.root)

        // 업 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        createNotificationChannel()

        routineId = intent.getIntExtra("ROUTINE_ID", -1).takeIf { it != -1 }

        routineId?.let {
            loadRoutineData(it)
        }

        binding.routineStartTime.setOnClickListener {
            showTimePickerDialog(binding.routineStartTime)
        }

        binding.addTaskButton.setOnClickListener {
            if (tasks.size < 10) {
                addTaskItem()
            } else {
                Toast.makeText(this, "할 일은 최대 10개까지 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.addRoutineButton.setOnClickListener {
            val routineName = binding.routineName.text.toString()
            val routineStartTime = binding.routineStartTime.text.toString()

            when {
                routineName.isEmpty() -> {
                    Toast.makeText(this, "루틴 이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                }
                routineStartTime.isEmpty() -> {
                    Toast.makeText(this, "루틴 시작 시간을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                }
                tasks.isEmpty() -> {
                    Toast.makeText(this, "할 일 없는 루틴은 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    routineId?.let { cancelAlarm(it) } //(편집시) 기존 알람 삭제
                    val routine = Routine(id = routineId ?: 0, name = routineName, startTime = routineStartTime)
                    tasks.clear()
                    for (i in 0 until binding.taskContainer.childCount) {
                        val taskView = binding.taskContainer.getChildAt(i)
                        val spinnerTask = taskView.findViewById<Spinner>(R.id.spinnerTask)
                        val customTaskDescription = taskView.findViewById<EditText>(R.id.customTaskDescription)
                        val taskDescription = if (customTaskDescription.visibility == View.VISIBLE) {
                            customTaskDescription.text.toString()
                        } else {
                            spinnerTask.selectedItem.toString()
                        }
                        val timeText = taskView.findViewById<TextView>(R.id.timeText).text.toString()
                        tasks.add(Task(0, routine.id, taskDescription, timeText))
                    }
                    routineViewModel.addRoutine(routine, tasks)
                    setAlarm(routineName, routineStartTime) // 새로운 알림 설정

                    val intent = Intent(this, RoutineListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }


    }
    private fun setAlarm(routineName: String, startTime: String) {
        val (hour, minute) = startTime.split(":").map { it.toInt() }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1) // 다음 날 같은 시간으로 설정
        }

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ROUTINE_NAME", routineName)
        }

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(routineId: Int, routineName: String, triggerTime: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ROUTINE_NAME", routineName)
            putExtra("ROUTINE_ID", routineId)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, routineId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun getPendingIntent(routineId: Int): PendingIntent {
        val intent = Intent(this, RoutineListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ROUTINE_ID", routineId)
        }
        return PendingIntent.getActivity(this, routineId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun cancelAlarm(routineId: Int) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, routineId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
    private fun loadRoutineData(routineId: Int) {
        lifecycleScope.launch {
            routineViewModel.getRoutines().collect { routines ->
                routines.find { it.id == routineId }?.let { routine ->
                    binding.routineName.text = Editable.Factory.getInstance().newEditable(routine.name)
                    binding.routineStartTime.text = Editable.Factory.getInstance().newEditable(routine.startTime)
                    loadTaskData(routineId)
                }
            }
        }
    }

    private fun loadTaskData(routineId: Int) {
        lifecycleScope.launch {
            routineViewModel.getTasksForRoutine(routineId).collect { tasks ->
                this@AddRoutineActivity.tasks.clear()
                this@AddRoutineActivity.tasks.addAll(tasks)
                tasks.forEach { task ->
                    addTaskItem(task)
                }
            }
        }
    }

    private fun showTimePickerDialog(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeTextView.text = formattedTime
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun addTaskItem(task: Task? = null) {
        val taskBinding = TaskItemBinding.inflate(LayoutInflater.from(this))
        val taskItemView = taskBinding.root

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.task_items,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        taskBinding.spinnerTask.adapter = adapter

        task?.let {
            val taskDescriptions = resources.getStringArray(R.array.task_items)
            val position = taskDescriptions.indexOf(it.description)
            if (position >= 0) {
                taskBinding.spinnerTask.setSelection(position)
            } else {
                // Custom task case
                taskBinding.customTaskDescription.setText(it.description)
                taskBinding.customTaskDescription.visibility = View.VISIBLE

                val customAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(it.description))
                customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                taskBinding.spinnerTask.adapter = customAdapter
                taskBinding.spinnerTask.setSelection(0)
            }
            taskBinding.timeText.text = it.time
        }

        taskBinding.plusButton.setOnClickListener {
            val timeText = taskBinding.timeText
            var time = timeText.text.toString().replace("분", "").toInt()
            time += 1
            timeText.text = "${time}분"
        }

        taskBinding.minusButton.setOnClickListener {
            val timeText = taskBinding.timeText
            var time = timeText.text.toString().replace("분", "").toInt()
            if (time > 1) {
                time -= 1
                timeText.text = "${time}분"
            }
        }

        taskBinding.deleteButton.setOnClickListener {
            binding.taskContainer.removeView(taskItemView)
            tasks.remove(task)
            task?.let { cancelAlarm(it.routineId) }  // 해당 루틴의 알람 취소
        }

        taskBinding.spinnerTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTask = parent?.getItemAtPosition(position).toString()
                if (selectedTask == "+추가") {
                    showCustomTaskDialog(taskBinding.spinnerTask, taskBinding.customTaskDescription)
                } else {
                    taskBinding.customTaskDescription.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // 새로운 할 일을 tasks 리스트에 추가
        if (task == null) {
            val newTask = Task(0, routineId ?: 0, "", "")
            tasks.add(newTask)
        }

        binding.taskContainer.addView(taskItemView)
    }

    private fun showCustomTaskDialog(spinnerTask: Spinner, customTaskDescription: EditText) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("할 일 추가")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("추가") { dialog, _ ->
            val customTask = input.text.toString()
            if (customTask.isNotEmpty()) {
                customTaskDescription.setText(customTask)
                customTaskDescription.visibility = View.VISIBLE

                // Spinner의 선택 항목을 사용자 입력 값으로 설정
                val customAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(customTask))
                customAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTask.adapter = customAdapter
                spinnerTask.setSelection(0)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}