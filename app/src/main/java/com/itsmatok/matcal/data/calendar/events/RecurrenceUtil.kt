package com.itsmatok.matcal.data.calendar.events

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object RecurrenceUtil {

    fun expandEvents(
        events: List<CalendarEvent>,
        centerDate: LocalDate = LocalDate.now(),
        yearsBuffer: Int = 10
    ): Map<LocalDate, MutableList<CalendarEvent>> {
        val resultMap = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()
        val startYear = centerDate.year - yearsBuffer
        val endYear = centerDate.year + yearsBuffer

        val rangeStartDate = LocalDate.of(startYear, 1, 1)
        val rangeEndDate = LocalDate.of(endYear, 12, 31)

        events.forEach { event ->
            if (event.recurrenceType == RecurrenceType.NONE) {
                resultMap.getOrPut(event.date) { mutableListOf() }.add(event)
            } else {
                generateOccurrences(
                    event,
                    rangeStartDate,
                    rangeEndDate
                ).forEach { (date, eventCopy) ->
                    resultMap.getOrPut(date) { mutableListOf() }.add(eventCopy)
                }
            }
        }
        return resultMap
    }

    private fun generateOccurrences(
        originalEvent: CalendarEvent,
        rangeStartDate: LocalDate,
        rangeEndDate: LocalDate
    ): Map<LocalDate, CalendarEvent> {
        val occurrences = mutableMapOf<LocalDate, CalendarEvent>()

        if (originalEvent.date.isAfter(rangeEndDate)) return occurrences

        var currentDate = originalEvent.date

        currentDate = when (originalEvent.recurrenceType) {
            RecurrenceType.DAILY -> {
                if (currentDate.isBefore(rangeStartDate)) rangeStartDate else currentDate
            }

            RecurrenceType.WEEKLY -> {
                if (currentDate.isBefore(rangeStartDate)) {
                    val weeks = ChronoUnit.WEEKS.between(currentDate, rangeStartDate)
                    currentDate.plusWeeks(weeks).let {
                        if (it.isBefore(rangeStartDate)) it.plusWeeks(1) else it
                    }
                } else currentDate
            }

            RecurrenceType.MONTHLY -> {
                if (currentDate.year < rangeStartDate.year) {
                    currentDate.withYear(rangeStartDate.year - 1)
                } else currentDate
            }

            RecurrenceType.YEARLY -> {
                if (currentDate.year < rangeStartDate.year) {
                    try {
                        currentDate.withYear(rangeStartDate.year)
                    } catch (e: Exception) {
                        currentDate.withYear(rangeStartDate.year).plusDays(1)
                    }
                } else currentDate
            }

            else -> currentDate
        }

        while (!currentDate.isAfter(rangeEndDate)) {
            if (!currentDate.isBefore(rangeStartDate)) {
                val validDate = if (originalEvent.recurrenceType == RecurrenceType.YEARLY) {
                    isValidYearlyDate(originalEvent.date, currentDate.year)
                } else currentDate

                if (validDate != null) {
                    occurrences[validDate] = originalEvent.copy(date = validDate)
                }
            }

            currentDate = when (originalEvent.recurrenceType) {
                RecurrenceType.DAILY -> currentDate.plusDays(1)
                RecurrenceType.WEEKLY -> currentDate.plusWeeks(1)
                RecurrenceType.MONTHLY -> currentDate.plusMonths(1)
                RecurrenceType.YEARLY -> currentDate.plusYears(1)
                else -> rangeEndDate.plusDays(1)
            }
        }

        return occurrences
    }

    private fun isValidYearlyDate(original: LocalDate, targetYear: Int): LocalDate? {
        return try {
            original.withYear(targetYear)
        } catch (e: Exception) {
            // if no leap year, use feb 28. maybe skip it?
            original.withYear(targetYear - 1).plusYears(1)
        }
    }
}