package com.itsmatok.matcal.ui.calendar.components.forms

enum class ReminderSelection(val label: String, val minutes: Int?) {
    NONE("No reminder", null),
    FIFTEEN_MINUTES("15 minutes before", 15),
    THIRTY_MINUTES("30 minutes before", 30),
    ONE_HOUR("1 hour before", 60),
    CUSTOM("Custom", null);

    companion object {
        fun fromMinutes(minutes: Int?): ReminderSelection {
            return when (minutes) {
                null -> NONE
                15 -> FIFTEEN_MINUTES
                30 -> THIRTY_MINUTES
                60 -> ONE_HOUR
                else -> CUSTOM
            }
        }
    }
}
