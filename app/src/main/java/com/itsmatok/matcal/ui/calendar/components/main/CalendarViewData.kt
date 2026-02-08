package com.itsmatok.matcal.ui.calendar.components.main

import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal fun selectedEventsForDate(
    events: Map<LocalDate, List<CalendarEvent>>,
    date: LocalDate
): List<CalendarEvent> = events[date].orEmpty().sortedBy { it.startTime }

internal fun groupEventsByHour(
    dayEvents: List<CalendarEvent>
): Map<Int, List<CalendarEvent>> = dayEvents.groupBy { it.startTime.hour }

internal fun weekDaysForSelection(
    selectedDate: LocalDate,
    firstDayOfWeek: DayOfWeek
): List<LocalDate> {
    val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    return List(7) { index -> weekStart.plusDays(index.toLong()) }
}
