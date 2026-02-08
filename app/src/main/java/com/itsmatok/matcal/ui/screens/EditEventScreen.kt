package com.itsmatok.matcal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.itsmatok.matcal.data.calendar.events.RecurrenceType
import com.itsmatok.matcal.ui.calendar.components.forms.EventFormContent
import com.itsmatok.matcal.ui.calendar.components.forms.ReminderSelection
import com.itsmatok.matcal.ui.calendar.components.forms.TimePickerDialog
import com.itsmatok.matcal.viewmodels.CalendarViewModel
import androidx.core.content.ContextCompat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    viewModel: CalendarViewModel,
    eventId: Int,
    onNavigateBack: () -> Unit
) {

    val eventState by viewModel.getEventById(eventId).collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var endTime by remember {
        mutableStateOf(
            LocalTime.now().plusHours(1).withSecond(0).withNano(0)
        )
    }
    var recurrence by remember { mutableStateOf(RecurrenceType.NONE) }
    var reminderSelection by remember { mutableStateOf(ReminderSelection.NONE) }
    var customReminderMinutes by remember { mutableStateOf("10") }

    var dataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(eventState) {
        eventState?.let { event ->
            if (!dataLoaded) {
                title = event.title
                location = event.location ?: ""
                description = event.description ?: ""
                selectedDate = event.date
                startTime = event.startTime
                endTime = event.endTime
                recurrence = event.recurrenceType ?: RecurrenceType.NONE
                reminderSelection = ReminderSelection.fromMinutes(event.reminderMinutes)
                customReminderMinutes = event.reminderMinutes?.toString() ?: "10"
                dataLoaded = true
            }
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(
                context,
                "Enable notifications in system settings to receive reminders.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Event") },
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
            reminderSelection = reminderSelection,
            onReminderSelectionChange = { reminderSelection = it },
            customReminderMinutes = customReminderMinutes,
            onCustomReminderMinutesChange = { customReminderMinutes = it },
            buttonText = "Update",
            onSaveClick = {
                eventState?.let { event ->
                    val reminderMinutes = when (reminderSelection) {
                        ReminderSelection.NONE -> null
                        ReminderSelection.FIFTEEN_MINUTES -> 15
                        ReminderSelection.THIRTY_MINUTES -> 30
                        ReminderSelection.ONE_HOUR -> 60
                        ReminderSelection.CUSTOM -> customReminderMinutes.toIntOrNull()
                            ?.takeIf { it > 0 }
                    }

                    if (reminderMinutes != null &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    val updatedEvent = event.copy(
                        title = title,
                        date = selectedDate,
                        startTime = startTime,
                        endTime = endTime,
                        location = location.trim().ifEmpty { null },
                        description = description.trim().ifEmpty { null },
                        reminderMinutes = reminderMinutes,
                        recurrenceType = recurrence
                    )
                    viewModel.updateEvent(updatedEvent)
                    onNavigateBack()
                }
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
