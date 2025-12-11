package com.itsmatok.matcal.ui.calendar.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.events.CalendarEvent

@Composable
fun CalendarEventList(
    events: List<CalendarEvent>, onEventClicked: (Int) -> Unit
) {
    if (events.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "No events for this day.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(events) { event ->
                CalendarEventCard(event, onClick = { onEventClicked(event.id) })
            }
        }
    }
}
