package com.itsmatok.matcal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itsmatok.matcal.viewmodels.CalendarViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel<CalendarViewModel>(),
    onAddEventClicked: () -> Unit = {},
    onLicenseClicked: () -> Unit = {}
) {
    val events by viewModel.events.collectAsState(initial = emptyMap())
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var selection by remember { mutableStateOf<LocalDate?>(null) }
    val coroutineScope = rememberCoroutineScope()

    CalendarContent(
        state = state,
        events = events,
        selection = selection,
        onDateSelected = { clickedDate ->
            selection = clickedDate
            if (clickedDate.yearMonth != state.firstVisibleMonth.yearMonth) {
                coroutineScope.launch {
                    state.animateScrollToMonth(clickedDate.yearMonth)
                }
            }
        },
        onAddEventClicked = onAddEventClicked,
        onLicenseClicked = onLicenseClicked
    )
}