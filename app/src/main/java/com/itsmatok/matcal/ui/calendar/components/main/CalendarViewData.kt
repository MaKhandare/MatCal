package com.itsmatok.matcal.ui.calendar.components.main

import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal fun selectedEventsForDate(
    events: Map<LocalDate, List<CalendarEvent>>,
    date: LocalDate
): List<CalendarEvent> = events[date].orEmpty().sortedBy { it.startTime }

internal data class DayPositionedEvent(
    val event: CalendarEvent,
    val startMinutes: Int,
    val endMinutes: Int,
    val laneIndex: Int,
    val laneCount: Int,
    val isZeroDuration: Boolean
)

internal fun positionDayEvents(
    dayEvents: List<CalendarEvent>
) : List<DayPositionedEvent> {
    if (dayEvents.isEmpty()) return emptyList()

    val normalizedEvents = dayEvents
        .map { it.toNormalizedDayEvent() }
        .sortedWith(
            compareBy<NormalizedDayEvent> { it.startMinutes }
                .thenBy { it.endMinutes }
                .thenBy { it.event.id }
        )

    val positionedEvents = mutableListOf<DayPositionedEvent>()
    val cluster = mutableListOf<NormalizedDayEvent>()
    var clusterMaxEnd = -1

    fun flushCluster() {
        if (cluster.isEmpty()) return
        positionedEvents += positionCluster(cluster)
        cluster.clear()
        clusterMaxEnd = -1
    }

    normalizedEvents.forEach { normalized ->
        if (cluster.isEmpty()) {
            cluster += normalized
            clusterMaxEnd = normalized.endMinutes
            return@forEach
        }

        if (normalized.startMinutes < clusterMaxEnd) {
            cluster += normalized
            clusterMaxEnd = maxOf(clusterMaxEnd, normalized.endMinutes)
        } else {
            flushCluster()
            cluster += normalized
            clusterMaxEnd = normalized.endMinutes
        }
    }
    flushCluster()

    return positionedEvents.sortedWith(
        compareBy<DayPositionedEvent> { it.startMinutes }
            .thenBy { it.laneIndex }
            .thenBy { it.endMinutes }
            .thenBy { it.event.id }
    )
}

private data class NormalizedDayEvent(
    val event: CalendarEvent,
    val startMinutes: Int,
    val endMinutes: Int,
    val isZeroDuration: Boolean
)

private fun CalendarEvent.toNormalizedDayEvent(): NormalizedDayEvent {
    val start = (startTime.hour * MINUTES_PER_HOUR + startTime.minute)
        .coerceIn(0, LAST_MINUTE_OF_DAY)
    val rawEnd = (endTime.hour * MINUTES_PER_HOUR + endTime.minute).coerceIn(0, MINUTES_PER_DAY)
    val isZeroDuration = rawEnd == start
    val end = if (rawEnd <= start) {
        (start + 1).coerceAtMost(MINUTES_PER_DAY)
    } else {
        rawEnd
    }

    return NormalizedDayEvent(
        event = this,
        startMinutes = start,
        endMinutes = end,
        isZeroDuration = isZeroDuration
    )
}

private fun positionCluster(clusterEvents: List<NormalizedDayEvent>): List<DayPositionedEvent> {
    val laneEndMinutes = mutableListOf<Int>()
    val assignedEvents = mutableListOf<Pair<NormalizedDayEvent, Int>>()

    clusterEvents.forEach { event ->
        val laneIndex = laneEndMinutes.indexOfFirst { laneEnd ->
            laneEnd <= event.startMinutes
        }.takeIf { it >= 0 } ?: run {
            laneEndMinutes += event.endMinutes
            laneEndMinutes.lastIndex
        }

        if (laneIndex < laneEndMinutes.size) {
            laneEndMinutes[laneIndex] = event.endMinutes
        }

        assignedEvents += event to laneIndex
    }

    val laneCount = laneEndMinutes.size.coerceAtLeast(1)
    return assignedEvents.map { (event, laneIndex) ->
        DayPositionedEvent(
            event = event.event,
            startMinutes = event.startMinutes,
            endMinutes = event.endMinutes,
            laneIndex = laneIndex,
            laneCount = laneCount,
            isZeroDuration = event.isZeroDuration
        )
    }
}

internal fun weekDaysForSelection(
    selectedDate: LocalDate,
    firstDayOfWeek: DayOfWeek
): List<LocalDate> {
    val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    return List(7) { index -> weekStart.plusDays(index.toLong()) }
}

private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
private const val LAST_MINUTE_OF_DAY = MINUTES_PER_DAY - 1
