package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.R
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarHeader(
    yearMonth: YearMonth,
    daysOfWeek: List<DayOfWeek>,
    onMonthYearClick: () -> Unit = {}
) {
    val monthLabel = yearMonth.month.getDisplayName(
        TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )

    Column {
        Text(
            text = stringResource(R.string.format_month_year, monthLabel, yearMonth.year),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMonthYearClick() }
                .padding(bottom = 32.dp),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(
                        TextStyle.SHORT_STANDALONE,
                        Locale.getDefault()
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
