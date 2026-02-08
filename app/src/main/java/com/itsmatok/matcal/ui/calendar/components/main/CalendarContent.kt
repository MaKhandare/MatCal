package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun CalendarContent(
    state: CalendarState,
    events: Map<LocalDate, List<CalendarEvent>>,
    selection: LocalDate?,
    firstDayOfWeek: DayOfWeek,
    viewMode: CalendarViewMode,
    onViewModeChanged: (CalendarViewMode) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onAddEventClicked: () -> Unit,
    onLicenseClicked: () -> Unit,
    onEventClicked: (Int) -> Unit,
    onImportClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onManageCalendarsClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            CalendarTopAppBar(
                viewMode = viewMode,
                onViewModeChanged = onViewModeChanged,
                onAddEventClicked = onAddEventClicked,
                onLicenseClicked = onLicenseClicked,
                onImportClicked = onImportClicked,
                onRefreshClicked = onRefreshClicked,
                onManageCalendarsClicked = onManageCalendarsClicked
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewMode) {
                CalendarViewMode.AGENDA -> {
                    AgendaView(
                        modifier = Modifier.weight(1f),
                        state = state,
                        events = events,
                        selection = selection,
                        onDateSelected = onDateSelected,
                        onEventClicked = onEventClicked
                    )
                }

                CalendarViewMode.DAY -> {
                    DayView(
                        modifier = Modifier.weight(1f),
                        selection = selection,
                        events = events,
                        onDateSelected = onDateSelected,
                        onEventClicked = onEventClicked
                    )
                }

                CalendarViewMode.WEEK -> {
                    WeekView(
                        modifier = Modifier.weight(1f),
                        selection = selection,
                        events = events,
                        firstDayOfWeek = firstDayOfWeek,
                        onDateSelected = onDateSelected,
                        onEventClicked = onEventClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun AgendaView(
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
    val selectedEvents = selection?.let { events[it] }?.sortedBy { it.startTime } ?: emptyList()

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

@Composable
private fun DayView(
    modifier: Modifier = Modifier,
    selection: LocalDate?,
    events: Map<LocalDate, List<CalendarEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val selectedDate = selection ?: LocalDate.now()
    val dayEvents = remember(selectedDate, events) {
        events[selectedDate]?.sortedBy { it.startTime } ?: emptyList()
    }
    val eventsByHour = remember(dayEvents) { dayEvents.groupBy { it.startTime.hour } }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()) }
    val hourFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }

    Column(modifier = modifier.fillMaxSize()) {
        DateNavigator(
            label = selectedDate.format(dateFormatter),
            onPrevious = { onDateSelected(selectedDate.minusDays(1)) },
            onNext = { onDateSelected(selectedDate.plusDays(1)) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 16.dp)
        ) {
            items(24) { hour ->
                val hourEvents = eventsByHour[hour].orEmpty()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = LocalTime.of(hour, 0).format(hourFormatter),
                        modifier = Modifier
                            .width(56.dp)
                            .padding(top = 6.dp),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        HorizontalDivider(color = DividerDefaults.color)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (hourEvents.isEmpty()) {
                            Spacer(modifier = Modifier.height(36.dp))
                        } else {
                            hourEvents.forEach { event ->
                                DayTimelineEventCard(
                                    event = event,
                                    onEventClicked = onEventClicked
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayTimelineEventCard(
    event: CalendarEvent,
    onEventClicked: (Int) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { onEventClicked(event.id) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!event.location.isNullOrBlank()) {
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WeekView(
    modifier: Modifier = Modifier,
    selection: LocalDate?,
    events: Map<LocalDate, List<CalendarEvent>>,
    firstDayOfWeek: DayOfWeek,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val selectedDate = selection ?: LocalDate.now()
    val weekStart = remember(selectedDate, firstDayOfWeek) {
        selectedDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }
    val weekDays = remember(weekStart) {
        List(7) { index -> weekStart.plusDays(index.toLong()) }
    }
    val weekRangeFormatter = remember { DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()) }
    val weekDayFormatter = remember { DateTimeFormatter.ofPattern("EEE", Locale.getDefault()) }
    val selectedEvents = remember(selectedDate, events) {
        events[selectedDate]?.sortedBy { it.startTime } ?: emptyList()
    }

    Column(modifier = modifier.fillMaxSize()) {
        DateNavigator(
            label = "${weekDays.first().format(weekRangeFormatter)} - ${weekDays.last().format(weekRangeFormatter)}",
            onPrevious = { onDateSelected(selectedDate.minusWeeks(1)) },
            onNext = { onDateSelected(selectedDate.plusWeeks(1)) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            weekDays.forEach { date ->
                val isSelected = date == selectedDate
                val dayEvents = events[date].orEmpty()

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    onClick = { onDateSelected(date) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = date.format(weekDayFormatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )

                        if (dayEvents.isNotEmpty()) {
                            Text(
                                text = dayEvents.size.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = DividerDefaults.color
        )

        CalendarSelectionHeader(selection = selectedDate)

        Box(modifier = Modifier.weight(1f)) {
            CalendarEventList(events = selectedEvents, onEventClicked = onEventClicked)
        }
    }
}

@Composable
private fun DateNavigator(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous"
            )
        }

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next"
            )
        }
    }
}
