package com.itsmatok.matcal.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.itsmatok.matcal.ui.calendar.components.main.CalendarContent
import com.itsmatok.matcal.ui.calendar.components.main.CalendarViewMode
import com.itsmatok.matcal.ui.calendar.components.main.ImportUrlDialog
import com.itsmatok.matcal.viewmodels.CalendarViewModel
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.YearMonth

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onAddEventClicked: () -> Unit,
    onLicenseClicked: () -> Unit,
    onEventClicked: (Int) -> Unit,
    onManageCalendarsClicked: () -> Unit
) {
    val events by viewModel.events.collectAsState(initial = emptyMap())
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val selection by viewModel.selectedDate.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val initialMonth = remember { YearMonth.from(selection) }

    var showImportDialog by remember { mutableStateOf(false) }
    var viewModeName by rememberSaveable { mutableStateOf(CalendarViewMode.AGENDA.name) }
    val viewMode = CalendarViewMode.entries.firstOrNull { it.name == viewModeName }
        ?: CalendarViewMode.AGENDA

    if (showImportDialog) {
        ImportUrlDialog(
            onDismiss = { showImportDialog = false },
            onConfirm = { url ->
                showImportDialog = false
                viewModel.importEventsFromUrl(url)
            }
        )
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = initialMonth,
        firstDayOfWeek = firstDayOfWeek
    )


    CalendarContent(
        state = state,
        events = events,
        selection = selection,
        firstDayOfWeek = firstDayOfWeek,
        viewMode = viewMode,
        onViewModeChanged = { viewModeName = it.name },
        onDateSelected = { clickedDate ->
            viewModel.onDateSelected(clickedDate)

            if (clickedDate.yearMonth != state.firstVisibleMonth.yearMonth) {
                coroutineScope.launch {
                    state.animateScrollToMonth(clickedDate.yearMonth)
                }
            }
        },
        onAddEventClicked = onAddEventClicked,
        onLicenseClicked = onLicenseClicked,
        onEventClicked = onEventClicked,
        onImportClicked = { showImportDialog = true },
        onRefreshClicked = { viewModel.refreshAllSchedules() },
        onManageCalendarsClicked = onManageCalendarsClicked
    )
}
