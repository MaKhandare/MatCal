package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun WeekView(
    modifier: Modifier = Modifier,
    selection: LocalDate?,
    events: Map<LocalDate, List<CalendarEvent>>,
    firstDayOfWeek: DayOfWeek,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val selectedDate = selection ?: LocalDate.now()
    val weekDays = remember(selectedDate, firstDayOfWeek) {
        weekDaysForSelection(selectedDate, firstDayOfWeek)
    }
    val weekRangeFormatter = remember { DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()) }
    val weekDayFormatter = remember { DateTimeFormatter.ofPattern("EEE", Locale.getDefault()) }
    val selectedEvents = remember(selectedDate, events) { selectedEventsForDate(events, selectedDate) }

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
