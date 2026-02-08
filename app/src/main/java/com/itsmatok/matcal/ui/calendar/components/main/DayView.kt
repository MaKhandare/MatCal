package com.itsmatok.matcal.ui.calendar.components.main

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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun DayView(
    modifier: Modifier = Modifier,
    selection: LocalDate?,
    events: Map<LocalDate, List<CalendarEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val selectedDate = selection ?: LocalDate.now()
    val dayEvents = remember(selectedDate, events) { selectedEventsForDate(events, selectedDate) }
    val eventsByHour = remember(dayEvents) { groupEventsByHour(dayEvents) }
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
