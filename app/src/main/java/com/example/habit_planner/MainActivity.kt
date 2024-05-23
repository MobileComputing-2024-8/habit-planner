package com.example.habit_planner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.habit_planner.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    enableEdgeToEdge()
    setContentView(binding.root)
    askNotificationPermission()
    fetchFCMToken()

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    // 시작 버튼
    binding.startBtn.setOnClickListener {
      val intent = Intent(this, RoutineListActivity::class.java)
      startActivity(intent)
    }
  }

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { isGranted: Boolean ->
    if(isGranted) {
      Log.d("MainActivity", "Notification permission granted")
    } else {
      Log.d("MainActivity", "Notification permission denied")
    }
  }

  private fun askNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      when {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED -> {
          Log.d("MainActivity", "Notification permission already granted")
        }
        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
          showPermissionRationale()
        }
        else -> {
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }

  private fun showPermissionRationale() {
    AlertDialog.Builder(this)
      .setTitle("Notification Permission Needed")
      .setMessage("This app needs the Notification permission to send you updates.")
      .setPositiveButton("OK") { _, _ ->
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
      .setNegativeButton("No thanks") { dialog, _ ->
        dialog.dismiss()
      }
      .create()
      .show()
  }

  private fun fetchFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
        return@addOnCompleteListener
      }

      // Get new FCM registration token
      val token = task.result

    }
  }
}
