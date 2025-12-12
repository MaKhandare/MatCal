package com.itsmatok.matcal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.itsmatok.matcal.ui.calendar.components.CalendarEventDetailsScreen
import com.itsmatok.matcal.ui.screens.AddEventScreen
import com.itsmatok.matcal.ui.screens.CalendarScreen
import com.itsmatok.matcal.ui.screens.LicenseScreen
import com.itsmatok.matcal.ui.screens.ManageCalendarsScreen
import com.itsmatok.matcal.viewmodels.CalendarViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CalendarViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Calendar
    ) {

        composable<Calendar> {
            CalendarScreen(
                viewModel = viewModel,
                onAddEventClicked = { navController.navigate(AddEvent) },
                onLicenseClicked = { navController.navigate(License) },
                onEventClicked = { eventId ->
                    navController.navigate(EventDetails(eventId))
                },
                onManageCalendarsClicked = { navController.navigate(ManageCalendars) }
            )
        }

        composable<AddEvent> {
            AddEventScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<License> {
            LicenseScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable<EventDetails> { backStackEntry ->
            val route: EventDetails = backStackEntry.toRoute()

            CalendarEventDetailsScreen(
                viewModel = viewModel,
                eventId = route.eventId,
                onNavigateBack = { navController.navigateUp() },
                onEditEvent = { navController.navigate(AddEvent) }
            )
        }

        composable<ManageCalendars> {
            ManageCalendarsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.navigateUp() })
        }

    }
}