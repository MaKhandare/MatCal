package com.itsmatok.matcal.ui.calendar.components.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailsContent(event: CalendarEvent, modifier: Modifier = Modifier) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .verticalScroll(scrollState)
    ) {

        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        EventDetailItem(
            icon = Icons.Default.Event,
            title = "Date",
            subtitle = "${event.date.format(dateFormatter)}",
        )

        EventDetailItem(
            icon = Icons.Outlined.Schedule,
            title = "Time",
            subtitle = "${event.startTime.format(timeFormatter)} - ${
                event.endTime.format(
                    timeFormatter
                )
            }",
        )

        if (event.location?.isNotBlank() == true) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            EventDetailItem(
                icon = Icons.Outlined.LocationOn,
                title = "Location",
                subtitle = event.location,
            )
        }

        if (event.source?.isNotBlank() == true) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            EventDetailItem(
                icon = Icons.Default.Source,
                title = "Source",
                subtitle = event.source,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(24.dp))

        EventDetailItem(
            icon = Icons.Outlined.Description,
            title = "Description",
            subtitle = if (!event.description.isNullOrBlank()) event.description else "No description provided.",
            alignIconTop = true
        )
    }
}
