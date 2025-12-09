package com.itsmatok.matcal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.CalendarEvent
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.LocalDate

@Composable
fun CalendarContent(
    state: CalendarState,
    events: Map<LocalDate, List<CalendarEvent>>,
    selection: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onAddEventClicked: () -> Unit,
    onLicenseClicked: () -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val visibleMonth = state.firstVisibleMonth.yearMonth
    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }

    Scaffold(
        topBar = {
            CalendarTopAppBar(
                onAddEventClicked = onAddEventClicked,
                onLicenseClicked = onLicenseClicked
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            CalendarHeader(
                yearMonth = visibleMonth,
                daysOfWeek = daysOfWeek
            )

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    CalendarDayCell(
                        day = day,
                        isSelected = selection == day.date,
                        isToday = day.date == today,
                        events = events[day.date] ?: emptyList(),
                        onClick = { onDateSelected(it.date) }
                    )
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = DividerDefaults.color
            )

            CalendarSelectionHeader(selection = selection)

            val selectedEvents =
                selection?.let { events[it] }?.sortedBy { it.startTime } ?: emptyList()
            CalendarEventList(events = selectedEvents, onEventClicked = onEventClicked)
        }
    }
}
