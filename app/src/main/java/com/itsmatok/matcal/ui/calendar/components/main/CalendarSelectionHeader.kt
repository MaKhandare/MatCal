package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarSelectionHeader(selection: LocalDate?) {
    val dateText = remember(selection) {
        selection?.let {
            "${it.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${it.dayOfMonth}"
        } ?: "No date selected"
    }

    Text(
        text = dateText,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
