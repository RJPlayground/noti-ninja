package com.example.alertmate

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationRepository {
    private const val PREFS_NAME = "AlertMatePrefs"
    private const val KEY_SERVER_URL = "server_url"
    
    private val _notificationCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val notificationCounts: StateFlow<Map<String, Int>> = _notificationCounts.asStateFlow()

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _serverUrl.value = prefs?.getString(KEY_SERVER_URL, "") ?: ""
    }

    fun incrementCount(packageName: String) {
        _notificationCounts.update { currentMap ->
            val currentCount = currentMap[packageName] ?: 0
            currentMap + (packageName to (currentCount + 1))
        }
    }

    fun updateServerUrl(url: String) {
        _serverUrl.value = url
        prefs?.edit()?.putString(KEY_SERVER_URL, url)?.apply()
    }
}
