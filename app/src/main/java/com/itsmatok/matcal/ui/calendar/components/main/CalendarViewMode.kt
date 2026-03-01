package com.itsmatok.matcal.ui.calendar.components.main

import androidx.annotation.StringRes
import com.itsmatok.matcal.R

enum class CalendarViewMode(@param:StringRes val labelRes: Int) {
    AGENDA(R.string.calendar_view_mode_agenda),
    DAY(R.string.calendar_view_mode_day),
    WEEK(R.string.calendar_view_mode_week)
}
