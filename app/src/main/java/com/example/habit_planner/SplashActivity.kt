package com.example.yourapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.habit_planner.MainActivity
import com.example.habit_planner.R

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.splash)

    Handler().postDelayed({
      val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)
      finish()
    }, 1000) // 1초
  }
}
