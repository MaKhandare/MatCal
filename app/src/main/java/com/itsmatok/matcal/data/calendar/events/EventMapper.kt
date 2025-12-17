package com.itsmatok.matcal.data.calendar.events

import biweekly.component.VEvent
import java.time.ZoneId

object EventMapper {

    fun mapVEventToCalendarEvent(
        vEvent: VEvent,
        url: String,
        calendarName: String
    ): CalendarEvent? {
        val startDate = vEvent.dateStart?.value ?: return null
        val endDate = vEvent.dateEnd?.value ?: vEvent.dateStart.value

        val zoneId = ZoneId.systemDefault()
        val localStartDate = startDate.toInstant().atZone(zoneId).toLocalDate()
        val localStartTime = startDate.toInstant().atZone(zoneId).toLocalTime()
        val localEndTime = endDate.toInstant().atZone(zoneId).toLocalTime()

        return CalendarEvent(
            date = localStartDate,
            startTime = localStartTime,
            endTime = localEndTime,
            title = vEvent.summary?.value?.trim() ?: "No Title",
            location = vEvent.location?.value?.trim(),
            description = vEvent.description?.value?.trim(),
            source = calendarName,
            sourceUrl = url,
            iCalUid = vEvent.uid?.value,
            recurrenceType = parseRecurrenceRule(vEvent)
        )
    }

    private fun parseRecurrenceRule(vEvent: VEvent): RecurrenceType {
        // NOTE: this does not handle more complex RRULEs like UNTIL or INTERVAL
        // not sure if i am going to bother with that
        val rrule = vEvent.recurrenceRule?.value
        return when (rrule?.frequency?.name) {
            "DAILY" -> RecurrenceType.DAILY
            "WEEKLY" -> RecurrenceType.WEEKLY
            "MONTHLY" -> RecurrenceType.MONTHLY
            "YEARLY" -> RecurrenceType.YEARLY
            else -> RecurrenceType.NONE
        }
    }
}