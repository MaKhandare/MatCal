package com.itsmatok.matcal.data.calendar.events

import biweekly.component.VAlarm
import biweekly.component.VEvent
import biweekly.parameter.Related
import java.util.Date
import java.time.ZoneId
import kotlin.math.ceil

object EventMapper {

    fun mapVEventToCalendarEvent(
        vEvent: VEvent,
        url: String,
        calendarName: String
    ): CalendarEvent? {
        val startDate = vEvent.dateStart?.value ?: return null
        val endDate = vEvent.dateEnd?.value ?: vEvent.dateStart.value
        val isAllDayEvent = !startDate.hasTime()
        val parsedReminderMinutes = parseReminderMinutes(vEvent, startDate, endDate)
        val defaultReminderMinutes = if (vEvent.alarms.isEmpty()) {
            defaultReminderMinutes(isAllDayEvent)
        } else {
            null
        }

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
            reminderMinutes = parsedReminderMinutes ?: defaultReminderMinutes,
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

    private fun parseReminderMinutes(
        vEvent: VEvent,
        eventStart: Date,
        eventEnd: Date
    ): Int? {
        return vEvent.alarms
            .asSequence()
            .mapNotNull { alarm ->
                parseAlarmReminderMinutes(
                    alarm = alarm,
                    eventStart = eventStart,
                    eventEnd = eventEnd
                )
            }
            .filter { it > 0 }
            .minOrNull()
    }

    private fun parseAlarmReminderMinutes(
        alarm: VAlarm,
        eventStart: Date,
        eventEnd: Date
    ): Int? {
        val trigger = alarm.trigger ?: return null

        trigger.duration?.let { duration ->
            val related = trigger.related ?: Related.START
            val referenceDate = if (related == Related.END) eventEnd else eventStart
            val triggerDate = Date(referenceDate.time + duration.toMillis())
            return minutesBeforeStart(eventStart = eventStart, triggerDate = triggerDate)
        }

        trigger.date?.let { absoluteDate ->
            return minutesBeforeStart(eventStart = eventStart, triggerDate = absoluteDate)
        }

        return null
    }

    private fun minutesBeforeStart(eventStart: Date, triggerDate: Date): Int? {
        val diffMillis = eventStart.time - triggerDate.time
        if (diffMillis <= 0) return null
        return ceil(diffMillis / 60000.0).toInt().takeIf { it > 0 }
    }

    private fun defaultReminderMinutes(isAllDayEvent: Boolean): Int {
        return if (isAllDayEvent) 24 * 60 else 15
    }
}
