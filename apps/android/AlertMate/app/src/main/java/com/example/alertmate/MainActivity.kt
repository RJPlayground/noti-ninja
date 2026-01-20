package com.example.alertmate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.alertmate.ui.theme.AlertMateTheme

class MainActivity : ComponentActivity() {

    private val isNotificationPermissionGranted = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        NotificationRepository.init(applicationContext)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isNotificationPermissionGranted.value = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AlertMateTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val counts by NotificationRepository.notificationCounts.collectAsState()
                    val serverUrl by NotificationRepository.serverUrl.collectAsState()
                    
                    MainScreen(
                        isListenerEnabled = isNotificationPermissionGranted.value,
                        notificationCounts = counts,
                        serverUrl = serverUrl,
                        onServerUrlChange = { NotificationRepository.updateServerUrl(it) },
                        onEnableListenerClicked = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isNotificationPermissionGranted.value = isNotificationListenerEnabled()
    }

    private fun isNotificationListenerEnabled(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }
}

@Composable
fun MainScreen(
    isListenerEnabled: Boolean,
    notificationCounts: Map<String, Int>,
    serverUrl: String,
    onServerUrlChange: (String) -> Unit,
    onEnableListenerClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to AlertMate!", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = serverUrl,
            onValueChange = onServerUrlChange,
            label = { Text("Server URL (e.g., http://192.168.1.10:8082/ingest)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter server ingest URL") }
        )
        
        if (serverUrl.isBlank()) {
            Text(
                "Server sending is disabled (URL is empty)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isListenerEnabled) {
            Text("The app needs Notification Listener permission to work.", modifier = Modifier.padding(bottom = 8.dp))
            Button(onClick = onEnableListenerClicked) {
                Text("Enable Notification Listener")
            }
        } else {
            Text("Notification Listener is ENABLED", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Intercepted Notifications:", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(8.dp))

        if (notificationCounts.isEmpty()) {
            Text("No notifications intercepted yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(notificationCounts.toList()) { (packageName, count) ->
                    NotificationCountItem(packageName, count)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NotificationCountItem(packageName: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = packageName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
