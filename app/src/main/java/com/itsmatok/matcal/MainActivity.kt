package com.itsmatok.matcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.itsmatok.matcal.ui.screens.AddEventScreen
import com.itsmatok.matcal.ui.screens.CalendarScreen
import com.itsmatok.matcal.ui.screens.LicenseScreen
import com.itsmatok.matcal.ui.theme.MatCalTheme
import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Serializable
object License

@Serializable
object AddEvent

@Serializable
data class EventDetails(val eventId: Int)

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
                            onAddEventClicked = { navController.navigate(AddEvent) },
                            onLicenseClicked = { navController.navigate(License) },
                            onEventClicked = { eventId ->
                                navController.navigate(EventDetails(eventId))
                            }
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

                    composable<EventDetails> { backStackEntry ->
                        val route: EventDetails = backStackEntry.toRoute()

                        CalendarEventDetailsScreen(
                            eventId = route.eventId,
                            onNavigateBack = { navController.navigateUp() },
                            onEditEvent = { navController.navigate(AddEvent) }
                        )
                    }
                }
            }
        }
    }
}