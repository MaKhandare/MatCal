package com.itsmatok.matcal.ui.calendar.components.main

import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.kizitonwose.calendar.compose.CalendarState
import java.time.DayOfWeek
import java.time.LocalDate

data class CalendarContentUiState(
    val state: CalendarState,
    val events: Map<LocalDate, List<CalendarEvent>>,
    val selection: LocalDate?,
    val firstDayOfWeek: DayOfWeek,
    val viewMode: CalendarViewMode
)

data class CalendarContentActions(
    val onViewModeChanged: (CalendarViewMode) -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onAddEventClicked: () -> Unit,
    val onDayHourClicked: (LocalDate, Int) -> Unit,
    val onLicenseClicked: () -> Unit,
    val onEventClicked: (Int) -> Unit,
    val onImportClicked: () -> Unit,
    val onRefreshClicked: () -> Unit,
    val onManageCalendarsClicked: () -> Unit
)
