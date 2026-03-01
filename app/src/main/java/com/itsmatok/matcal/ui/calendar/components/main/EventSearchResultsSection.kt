package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.R
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun EventSearchResultsSection(
    modifier: Modifier = Modifier,
    query: String,
    results: List<CalendarEvent>,
    onEventClicked: (Int) -> Unit
) {
    val isQueryBlank = query.isBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.search_results),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        when {
            isQueryBlank -> {
                PlaceholderMessage(stringResource(R.string.search_type_hint))
            }

            results.isEmpty() -> {
                PlaceholderMessage(stringResource(R.string.search_no_results))
            }

            else -> {
                Text(
                    text = stringResource(R.string.search_result_count, results.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = results,
                        key = { event -> event.id }
                    ) { event ->
                        SearchResultRow(
                            event = event,
                            onEventClicked = onEventClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    event: CalendarEvent,
    onEventClicked: (Int) -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d yyyy", Locale.getDefault()) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }

    androidx.compose.material3.Card(
        onClick = { onEventClicked(event.id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(
                    R.string.search_result_datetime,
                    event.date.format(dateFormatter),
                    event.startTime.format(timeFormatter),
                    event.endTime.format(timeFormatter)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun PlaceholderMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
