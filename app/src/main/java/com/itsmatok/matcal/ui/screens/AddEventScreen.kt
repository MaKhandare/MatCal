package com.itsmatok.matcal.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.itsmatok.matcal.data.calendar.events.CalendarEvent
import com.itsmatok.matcal.data.calendar.events.RecurrenceType
import com.itsmatok.matcal.ui.calendar.components.forms.TimePickerDialog
import com.itsmatok.matcal.ui.calendar.components.forms.EventFormContent
import com.itsmatok.matcal.viewmodels.CalendarViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: CalendarViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf(RecurrenceType.NONE) }

    var startTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var endTime by remember {
        mutableStateOf(
            LocalTime.now().plusHours(1).withSecond(0).withNano(0)
        )
    }

    val currentSelection by viewModel.selectedDate.collectAsState()
    var selectedDate by remember { mutableStateOf(currentSelection) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        EventFormContent(
            modifier = Modifier.padding(innerPadding),
            title = title,
            onTitleChange = { title = it },
            location = location,
            onLocationChange = { location = it },
            description = description,
            onDescriptionChange = { description = it },
            selectedDate = selectedDate,
            onDateClick = { showDatePicker = true },
            startTime = startTime,
            onStartTimeClick = { showStartTimePicker = true },
            endTime = endTime,
            onEndTimeClick = { showEndTimePicker = true },
            recurrence = recurrence,
            onRecurrenceChange = { recurrence = it },
            buttonText = "Save",
            onSaveClick = {
                val newEvent = CalendarEvent(
                    id = 0,
                    date = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    title = title,
                    location = location.trim().ifEmpty { null },
                    description = description.trim().ifEmpty { null },
                    source = "Personal",
                    recurrenceType = recurrence
                )
                viewModel.addEvent(newEvent)
                onNavigateBack()
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { time ->
                startTime = time
                if (endTime.isBefore(time)) {
                    endTime = time.plusHours(1)
                }
                showStartTimePicker = false
            },
            initialTime = startTime
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { time ->
                endTime = time
                showEndTimePicker = false
            },
            initialTime = endTime
        )
    }
}