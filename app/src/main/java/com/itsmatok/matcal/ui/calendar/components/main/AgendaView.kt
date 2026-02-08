package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.LocalDate

@Composable
internal fun AgendaView(
    modifier: Modifier = Modifier,
    state: CalendarState,
    events: Map<LocalDate, List<CalendarEvent>>,
    selection: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val visibleMonth = state.firstVisibleMonth.yearMonth
    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }
    val selectedEvents = selection?.let { selectedEventsForDate(events, it) }.orEmpty()

    Column(modifier = modifier.fillMaxSize()) {
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

        Box(modifier = Modifier.weight(1f)) {
            CalendarEventList(events = selectedEvents, onEventClicked = onEventClicked)
        }
    }
}
