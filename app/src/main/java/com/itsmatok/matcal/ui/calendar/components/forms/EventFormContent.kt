package com.itsmatok.matcal.ui.calendar.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itsmatok.matcal.data.calendar.events.RecurrenceType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EventFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDate: LocalDate,
    onDateClick: () -> Unit,
    startTime: LocalTime,
    onStartTimeClick: () -> Unit,
    endTime: LocalTime,
    onEndTimeClick: () -> Unit,
    recurrence: RecurrenceType,
    onRecurrenceChange: (RecurrenceType) -> Unit,
    reminderSelection: ReminderSelection,
    onReminderSelectionChange: (ReminderSelection) -> Unit,
    customReminderMinutes: String,
    onCustomReminderMinutesChange: (String) -> Unit,
    buttonText: String,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val scrollState = rememberScrollState()
    val isCustomReminderValid = reminderSelection != ReminderSelection.CUSTOM ||
        customReminderMinutes.toIntOrNull()?.let { it > 0 } == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Event Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // date selector
        ReadOnlyField(
            value = selectedDate.format(dateFormatter),
            label = "Date",
            icon = Icons.Default.CalendarToday,
            onClick = onDateClick
        )

        // time selectors
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                ReadOnlyField(
                    value = startTime.format(timeFormatter),
                    label = "Start Time",
                    icon = Icons.Default.AccessTime,
                    onClick = onStartTimeClick
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                ReadOnlyField(
                    value = endTime.format(timeFormatter),
                    label = "End Time",
                    icon = Icons.Default.AccessTime,
                    onClick = onEndTimeClick
                )
            }
        }

        // repeating
        RecurrenceDropdown(
            selectedRecurrence = recurrence,
            onRecurrenceSelected = onRecurrenceChange
        )

        ReminderDropdown(
            selectedReminder = reminderSelection,
            onReminderSelected = onReminderSelectionChange,
            customReminderMinutes = customReminderMinutes,
            onCustomReminderMinutesChange = onCustomReminderMinutesChange
        )

        // location
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location (Optional)") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // description
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description (Optional)") },
            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && isCustomReminderValid,
            content = { Text(buttonText) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
