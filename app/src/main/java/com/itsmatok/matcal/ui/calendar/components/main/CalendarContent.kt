package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CalendarContent(
    uiState: CalendarContentUiState,
    actions: CalendarContentActions
) {
    Scaffold(
        topBar = {
            CalendarTopAppBar(
                viewMode = uiState.viewMode,
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                onSearchActivate = actions.onSearchActivate,
                onSearchQueryChange = actions.onSearchQueryChange,
                onSearchClose = actions.onSearchClose,
                onViewModeChanged = actions.onViewModeChanged,
                onAddEventClicked = actions.onAddEventClicked,
                onLicenseClicked = actions.onLicenseClicked,
                onImportClicked = actions.onImportClicked,
                onRefreshClicked = actions.onRefreshClicked,
                onManageCalendarsClicked = actions.onManageCalendarsClicked
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isSearchActive) {
                EventSearchResultsSection(
                    modifier = Modifier.fillMaxSize(),
                    query = uiState.searchQuery,
                    results = uiState.searchResults,
                    onEventClicked = actions.onEventClicked
                )
            } else {
                when (uiState.viewMode) {
                    CalendarViewMode.AGENDA -> {
                        AgendaView(
                            modifier = Modifier.weight(1f),
                            state = uiState.state,
                            events = uiState.events,
                            selection = uiState.selection,
                            onDateSelected = actions.onDateSelected,
                            onEventClicked = actions.onEventClicked,
                            onMonthYearSelected = actions.onMonthYearSelected
                        )
                    }

                    CalendarViewMode.DAY -> {
                        DayView(
                            modifier = Modifier.weight(1f),
                            selection = uiState.selection,
                            events = uiState.events,
                            onDateSelected = actions.onDateSelected,
                            onHourClicked = actions.onDayHourClicked,
                            onEventClicked = actions.onEventClicked
                        )
                    }

                    CalendarViewMode.WEEK -> {
                        WeekView(
                            modifier = Modifier.weight(1f),
                            selection = uiState.selection,
                            events = uiState.events,
                            firstDayOfWeek = uiState.firstDayOfWeek,
                            onDateSelected = actions.onDateSelected,
                            onEventClicked = actions.onEventClicked
                        )
                    }
                }
            }
        }
    }
}
