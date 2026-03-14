package com.itsmatok.matcal.ui.calendar.components.main

import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.kizitonwose.calendar.compose.CalendarState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class CalendarContentUiState(
    val state: CalendarState,
    val events: Map<LocalDate, List<CalendarEvent>>,
    val searchResults: List<CalendarEvent>,
    val searchQuery: String,
    val isSearchActive: Boolean,
    val selection: LocalDate?,
    val firstDayOfWeek: DayOfWeek,
    val viewMode: CalendarViewMode
)

data class CalendarContentActions(
    val onViewModeChanged: (CalendarViewMode) -> Unit,
    val onSearchActivate: () -> Unit,
    val onSearchQueryChange: (String) -> Unit,
    val onSearchClose: () -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onAddEventClicked: () -> Unit,
    val onDayHourClicked: (LocalDate, Int) -> Unit,
    val onLicenseClicked: () -> Unit,
    val onEventClicked: (Int) -> Unit,
    val onImportClicked: () -> Unit,
    val onRefreshClicked: () -> Unit,
    val onManageCalendarsClicked: () -> Unit,
    val onMonthYearSelected: (YearMonth) -> Unit
)
