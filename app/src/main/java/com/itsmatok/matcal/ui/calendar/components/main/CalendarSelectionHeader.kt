package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarSelectionHeader(selection: LocalDate?) {
    val dateText = if (selection == null) {
        stringResource(R.string.calendar_no_date_selected)
    } else {
        stringResource(
            R.string.format_day_of_week_date,
            selection.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            selection.dayOfMonth
        )
    }

    Text(
        text = dateText,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
