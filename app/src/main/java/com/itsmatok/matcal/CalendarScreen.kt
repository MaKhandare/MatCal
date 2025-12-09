package com.itsmatok.matcal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itsmatok.matcal.data.CalendarEvent
import com.itsmatok.matcal.viewmodels.CalendarViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel<CalendarViewModel>()
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val events by calendarViewModel.events.collectAsState(initial = emptyMap<LocalDate, List<CalendarEvent>>())
    var selection by remember { mutableStateOf<LocalDate?>(null) }
    val today = remember { LocalDate.now() }
    val coroutineScope = rememberCoroutineScope()
    val daysOfWeek = daysOfWeek()

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val visibleMonth = state.firstVisibleMonth.yearMonth.month
    val visibleYear = state.firstVisibleMonth.yearMonth.year

    Scaffold(
        topBar = { CalendarTopAppBar() }

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CalendarTitle(visibleMonth, visibleYear)

            HorizontalCalendar(state = state, dayContent = { day ->
                val dayEvents = events[day.date] ?: emptyList()
                Day(
                    day,
                    isSelected = selection == day.date,
                    isToday = day.date == today,
                    events = dayEvents
                ) { clicked ->
                    selection = clicked.date
                    if (clicked.position != DayPosition.MonthDate) {
                        coroutineScope.launch {
                            state.animateScrollToMonth(clicked.date.yearMonth)
                        }
                    }
                }
            }, monthHeader = { CalendarDaysOfWeekTitle(daysOfWeek = daysOfWeek) })
        }

    }
}

@Composable
fun CalendarTitle(visibleMonth: Month?, visibleYear: Int) {
    Text(
        text = "$visibleMonth $visibleYear",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    isToday: Boolean = false,
    events: List<CalendarEvent>,
    onClick: (CalendarDay) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = MaterialTheme.shapes.large
            )
            .clickable(onClick = { onClick(day) }), contentAlignment = Alignment.Center
    ) {
        val textColor = when (day.position) {
            DayPosition.MonthDate -> Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Color.Gray
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                    color = if (isToday) MaterialTheme.colorScheme.onSurface else Color.Unspecified
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                events.take(3).forEach { event ->
                    val dotColor = MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
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
                    TextStyle.SHORT_STANDALONE, Locale.getDefault()
                ),
            )
        }
    }
}