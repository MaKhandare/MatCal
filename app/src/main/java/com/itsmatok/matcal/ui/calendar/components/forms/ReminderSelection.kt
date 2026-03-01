package com.itsmatok.matcal.ui.calendar.components.forms

import androidx.annotation.StringRes
import com.itsmatok.matcal.R

enum class ReminderSelection(@param:StringRes val labelRes: Int, val minutes: Int?) {
    NONE(R.string.reminder_none, null),
    FIFTEEN_MINUTES(R.string.reminder_fifteen_minutes, 15),
    THIRTY_MINUTES(R.string.reminder_thirty_minutes, 30),
    ONE_HOUR(R.string.reminder_one_hour, 60),
    ONE_DAY(R.string.reminder_one_day, 1440),
    CUSTOM(R.string.reminder_custom, null);

    companion object {
        fun fromMinutes(minutes: Int?): ReminderSelection {
            return when (minutes) {
                null -> NONE
                15 -> FIFTEEN_MINUTES
                30 -> THIRTY_MINUTES
                60 -> ONE_HOUR
                1440 -> ONE_DAY
                else -> CUSTOM
            }
        }
    }
}
