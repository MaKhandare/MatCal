package com.itsmatok.matcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itsmatok.matcal.ui.theme.MatCalTheme
import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Serializable
object License

@Serializable
object AddEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MatCalTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController, startDestination = Calendar
                ) {
                    composable<Calendar> {
                        CalendarScreen(
                            onAddEventClicked = { navController.navigate(AddEvent) }
                        )
                    }

                    composable<AddEvent> {
                        AddEventScreen(
                            onNavigateBack = { navController.navigate(Calendar) }
                        )
                    }

                    composable<License> {
                        LicenseScreen(onNavigateBack = { navController.navigate(Calendar) })
                    }
                }
            }
        }
    }
}