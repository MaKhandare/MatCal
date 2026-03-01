package com.itsmatok.matcal.ui.calendar.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.itsmatok.matcal.R
import com.itsmatok.matcal.data.calendar.events.RecurrenceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceDropdown(
    selectedRecurrence: RecurrenceType,
    onRecurrenceSelected: (RecurrenceType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = recurrenceLabel(selectedRecurrence),
            onValueChange = { },
            label = { Text(stringResource(R.string.event_form_repeating)) },
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
            RecurrenceType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(recurrenceLabel(type)) },
                    onClick = {
                        onRecurrenceSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun recurrenceLabel(type: RecurrenceType): String {
    val resId = when (type) {
        RecurrenceType.NONE -> R.string.recurrence_none
        RecurrenceType.DAILY -> R.string.recurrence_daily
        RecurrenceType.WEEKLY -> R.string.recurrence_weekly
        RecurrenceType.MONTHLY -> R.string.recurrence_monthly
        RecurrenceType.YEARLY -> R.string.recurrence_yearly
    }
    return stringResource(resId)
}
