package com.itsmatok.matcal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationManagerCompat
import com.itsmatok.matcal.notifications.EXTRA_EVENT_ID
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.itsmatok.matcal.navigation.NavGraph
import com.itsmatok.matcal.ui.theme.MatCalTheme
import com.itsmatok.matcal.viewmodels.CalendarViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelNotificationFromIntent(intent)
        enableEdgeToEdge()
        setContent {
            MatCalTheme {
                val navController = rememberNavController()
                val calendarViewModel: CalendarViewModel = viewModel<CalendarViewModel>()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        viewModel = calendarViewModel
                    )
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        cancelNotificationFromIntent(intent)
    }

    private fun cancelNotificationFromIntent(intent: Intent?) {
        val notificationId = intent?.getIntExtra(EXTRA_EVENT_ID, -1) ?: -1
        if (notificationId != -1) {
            NotificationManagerCompat.from(this).cancel(notificationId)
        }
    }
}