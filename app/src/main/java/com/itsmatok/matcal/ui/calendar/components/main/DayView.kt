package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun DayView(
    modifier: Modifier = Modifier,
    selection: LocalDate?,
    events: Map<LocalDate, List<CalendarEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    onHourClicked: (LocalDate, Int) -> Unit,
    onEventClicked: (Int) -> Unit
) {
    val selectedDate = selection ?: LocalDate.now()
    val dayEvents = remember(selectedDate, events) { selectedEventsForDate(events, selectedDate) }
    val positionedEvents = remember(dayEvents) { positionDayEvents(dayEvents) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()) }
    val hourFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }
    val scrollState = rememberScrollState()
    val timelineHeight = DAY_HOUR_HEIGHT * HOURS_IN_DAY.toFloat()

    Column(modifier = modifier.fillMaxSize()) {
        DateNavigator(
            label = selectedDate.format(dateFormatter),
            onPrevious = { onDateSelected(selectedDate.minusDays(1)) },
            onNext = { onDateSelected(selectedDate.plusDays(1)) }
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 12.dp, end = 12.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .width(DAY_TIME_LABEL_WIDTH)
                    .height(timelineHeight)
            ) {
                repeat(HOURS_IN_DAY) { hour ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DAY_HOUR_HEIGHT),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = LocalTime.of(hour, 0).format(hourFormatter),
                            modifier = Modifier.padding(top = 6.dp),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(DAY_TIMELINE_GAP))

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(timelineHeight)
            ) {
                val density = LocalDensity.current
                val timelineHeightPx = with(density) { timelineHeight.toPx() }
                val timelineWidth = maxWidth
                val dividerColor = DividerDefaults.color

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedDate) {
                            detectTapGestures { tapOffset ->
                                val clampedY = tapOffset.y.coerceIn(0f, timelineHeightPx - 1f)
                                val tappedMinute = ((clampedY / timelineHeightPx) * MINUTES_PER_DAY).toInt()
                                val tappedHour = (tappedMinute / MINUTES_PER_HOUR)
                                    .coerceIn(0, HOURS_IN_DAY - 1)
                                onHourClicked(selectedDate, tappedHour)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val hourHeightPx = size.height / HOURS_IN_DAY
                        for (hour in 0..HOURS_IN_DAY) {
                            val y = hour * hourHeightPx
                            drawLine(
                                color = dividerColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y)
                            )
                        }
                    }
                }

                positionedEvents.forEach { positionedEvent ->
                    val laneCount = positionedEvent.laneCount.coerceAtLeast(1)
                    val laneWidth = laneWidth(
                        timelineWidth = timelineWidth,
                        laneCount = laneCount
                    )
                    val laneOffsetX = (laneWidth + DAY_EVENT_LANE_GAP) * positionedEvent.laneIndex.toFloat()

                    val top = timelineHeight * (positionedEvent.startMinutes / MINUTES_PER_DAY.toFloat())
                    val durationMinutes = (positionedEvent.endMinutes - positionedEvent.startMinutes)
                        .coerceAtLeast(1)
                    val rawHeight = timelineHeight * (durationMinutes / MINUTES_PER_DAY.toFloat())
                    val eventHeight = maxOf(DAY_MIN_EVENT_HEIGHT, rawHeight)

                    DayTimelineEventCard(
                        event = positionedEvent.event,
                        modifier = Modifier
                            .offset(x = laneOffsetX, y = top)
                            .width(laneWidth)
                            .height(eventHeight),
                        compact = eventHeight < DAY_COMPACT_EVENT_THRESHOLD,
                        onEventClicked = onEventClicked
                    )
                }
            }
        }
    }
}

private fun laneWidth(timelineWidth: Dp, laneCount: Int): Dp {
    val safeLaneCount = laneCount.coerceAtLeast(1)
    val totalGap = DAY_EVENT_LANE_GAP * (safeLaneCount - 1)
    val availableWidth = (timelineWidth - totalGap).coerceAtLeast(1.dp)
    return availableWidth / safeLaneCount
}

private const val HOURS_IN_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = HOURS_IN_DAY * MINUTES_PER_HOUR

private val DAY_HOUR_HEIGHT = 72.dp
private val DAY_TIME_LABEL_WIDTH = 56.dp
private val DAY_TIMELINE_GAP = 12.dp
private val DAY_EVENT_LANE_GAP = 6.dp
private val DAY_MIN_EVENT_HEIGHT = 28.dp
private val DAY_COMPACT_EVENT_THRESHOLD = 60.dp
