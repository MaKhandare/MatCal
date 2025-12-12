package com.itsmatok.matcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.itsmatok.matcal.navigation.NavGraph
import com.itsmatok.matcal.ui.theme.MatCalTheme
import com.itsmatok.matcal.viewmodels.CalendarViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MatCalTheme {
                val navController = rememberNavController()
                val calendarViewModel: CalendarViewModel = viewModel<CalendarViewModel>()

                NavGraph(
                    navController = navController,
                    viewModel = calendarViewModel
                )
            }
        }
    }
}