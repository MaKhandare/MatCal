package com.itsmatok.matcal.ui.calendar.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.itsmatok.matcal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDropdown(
    selectedReminder: ReminderSelection,
    onReminderSelected: (ReminderSelection) -> Unit,
    customReminderMinutes: String,
    onCustomReminderMinutesChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = stringResource(selectedReminder.labelRes),
            onValueChange = { },
            label = { Text(stringResource(R.string.event_form_notification)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ReminderSelection.entries.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(stringResource(selection.labelRes)) },
                    onClick = {
                        onReminderSelected(selection)
                        expanded = false
                    }
                )
            }
        }
    }

    if (selectedReminder == ReminderSelection.CUSTOM) {
        OutlinedTextField(
            value = customReminderMinutes,
            onValueChange = { value ->
                val filtered = value.filter(Char::isDigit).take(4)
                onCustomReminderMinutesChange(filtered)
            },
            label = { Text(stringResource(R.string.event_form_custom_reminder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
