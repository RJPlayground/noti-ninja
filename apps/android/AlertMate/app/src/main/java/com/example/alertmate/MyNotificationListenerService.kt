package com.example.alertmate

import android.app.Notification
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MyNotificationListenerService : NotificationListenerService() {

    private val TAG = "NotificationListener"
    private val client = OkHttpClient()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        // Updated to use the correct local IP for the physical device.
        private const val SERVER_URL = "http://192.168.68.124:8082/ingest"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn != null && sbn.packageName != applicationContext.packageName) {
            val notification = sbn.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

            // Create the nested payload object
            val payload = JSONObject()
            payload.put("title", title)
            payload.put("message", text)

            // Get the unique device ID
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            // Create the main notification object
            val notificationData = JSONObject()
            notificationData.put("id", sbn.key)
            notificationData.put("deviceId", deviceId)
            notificationData.put("source", sbn.packageName)
            notificationData.put("type", "notification")
            notificationData.put("timestamp", sbn.postTime)
            notificationData.put("payload", payload)

            Log.d(TAG, "Sending notification to server: ${notificationData.toString()}")
            sendNotificationToServer(notificationData.toString())
        }
    }

    private fun sendNotificationToServer(json: String) {
        serviceScope.launch {
            try {
                val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Failed to send notification to server: ${response.code} ${response.message}")
                    } else {
                        Log.d(TAG, "Notification sent to server successfully.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending notification to server. Ensure server is running at $SERVER_URL", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null) {
            Log.d(TAG, "Notification Removed: ${sbn.packageName}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
