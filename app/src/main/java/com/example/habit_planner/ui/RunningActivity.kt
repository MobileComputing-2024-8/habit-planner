package com.example.habit_planner.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.example.habit_planner.BuildConfig
import com.example.habit_planner.ChatGPTClient
import com.example.habit_planner.R
import com.example.habit_planner.data.Task
import com.example.habit_planner.databinding.ActivityRunningBinding
import com.example.habit_planner.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class RunningActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityRunningBinding

    private var currentTaskIndex = 0
    private lateinit var tasks: List<Task>
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isPaused: Boolean = false
    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false
    val OPENAI_KEY = BuildConfig.OPENAI_API_KEY
    val client = ChatGPTClient(OPENAI_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        tasks = intent.getParcelableArrayListExtra<Task>("TASKS") ?: emptyList()
        startTask(currentTaskIndex)

        binding.pausebtn.setOnClickListener {
            pauseTimer()
            binding.pausebtn.visibility=View.INVISIBLE
            binding.playbtn.visibility= View.VISIBLE
        }

        binding.playbtn.setOnClickListener{
            resumeTimer()
            binding.playbtn.visibility=View.INVISIBLE
            binding.pausebtn.visibility= View.VISIBLE
        }

        binding.checkbtn.setOnClickListener {
            finishRoutine()
        }

        binding.nextTaskbtn.setOnClickListener {
            nextTask()
        }

        binding.timeCircle.setOnLongClickListener {
            stopRoutine()
            true
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                isTtsInitialized = true
                Log.d("TTS", "TTS Initialized successfully")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
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

        val userPrompt = "${task.description}, ${task.time}"
        client.sendMessage("gpt-3.5-turbo", userPrompt, object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    val message = chatResponse?.choices?.get(0)?.message?.content
                    speakOut(message)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RunningActivity", "Response failed: $errorBody")
                    handleError(response.code())
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                Log.e("RunningActivity", "Error: ${t.message}")
                Toast.makeText(this@RunningActivity, "오류가 발생했습니다. 인터넷 연결을 확인하세요.", Toast.LENGTH_SHORT).show()
            }
        })

        startTimer()
    }

    private fun handleError(code: Int) {
        when (code) {
            429 -> {
                Toast.makeText(this, "API 요청이 너무 많습니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_LONG).show()
            }
            401 -> {
                Toast.makeText(this, "API 키가 유효하지 않습니다. 설정을 확인하세요.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "오류가 발생했습니다. 다시 시도하세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun speakOut(message: String?) {
        if (isTtsInitialized && message != null) {
            Log.d("TTS", "Speaking out: $message")
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            Log.e("TTS", "TTS not initialized or message is null")
        }
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
        tts.stop()
    }

    private fun resumeTimer() {
        startTimer()
        isPaused = false

    }

    private fun nextTask() {
        countDownTimer?.cancel()
        currentTaskIndex++
        tts.stop()
        tts.shutdown()
        startTask(currentTaskIndex)
    }

    private fun finishRoutine() {
        val intent = Intent(this, DoneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun stopRoutine(){
        val intent = Intent(this, RoutineListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        tts.stop()
        tts.shutdown()
    }
}
