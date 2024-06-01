package com.example.habit_planner.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.habit_planner.R
import com.example.habit_planner.data.Task
import com.example.habit_planner.databinding.ActivityRunningBinding

class RunningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding

    private var currentTaskIndex = 0
    private lateinit var tasks: List<Task>
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasks = intent.getParcelableArrayListExtra<Task>("TASKS") ?: emptyList()
        startTask(currentTaskIndex)

        binding.pausebtn.setOnClickListener {
            if (isPaused) {
                resumeTimer()
            } else {
                pauseTimer()
            }
        }

        binding.checkbtn.setOnClickListener {
            nextTask()
        }

        binding.nextTaskbtn.setOnClickListener {
            nextTask()
        }
    }

    private fun startTask(index: Int) {
        if (index >= tasks.size) {
            finishRoutine()
            return
        }

        val task = tasks[index]
        binding.titleTextView.text = task.description
        binding.setTime.text = "${task.time.replace("분", "")} 분"
        binding.progressIndicator.max = task.time.replace("분", "").toInt() * 60
        timeLeftInMillis = task.time.replace("분", "").toInt() * 60 * 1000.toLong()

        if (index + 1 < tasks.size) {
            binding.nextTaskText.text = tasks[index + 1].description
        } else {
            binding.nextTaskText.text = "없음"
        }

        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val seconds = (timeLeftInMillis / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                val timeFormatted = String.format("%02d:%02d", minutes, remainingSeconds)
                binding.timer.text = timeFormatted
                binding.progressIndicator.progress = seconds
            }

            override fun onFinish() {
                nextTask()
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isPaused = true
    }

    private fun resumeTimer() {
        startTimer()
        isPaused = false
    }

    private fun nextTask() {
        countDownTimer?.cancel()
        currentTaskIndex++
        startTask(currentTaskIndex)
    }

    private fun finishRoutine() {
        val intent = Intent(this, DoneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
