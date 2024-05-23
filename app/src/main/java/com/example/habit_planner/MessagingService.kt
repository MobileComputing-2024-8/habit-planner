package com.example.habit_planner

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    // Handle FCM messages here.
    Log.d(TAG, "From: ${remoteMessage.from}")

    // Check if message contains a data payload.
    if (remoteMessage.data.isNotEmpty()) {
      Log.d(TAG, "Message data payload: ${remoteMessage.data}")
    }

    // Check if message contains a notification payload.
    remoteMessage.notification?.let {
      Log.d(TAG, "Message Notification Body: ${it.body}")
    }
  }

  override fun onNewToken(token: String) {
    Log.d(TAG, "Refreshed token: $token")
    sendRegistrationToServer(token)
  }

  private fun sendRegistrationToServer(token: String) {
    // Implement this method to send token to your app server.
  }

  companion object {
    private const val TAG = "MessagingService"
  }
}
