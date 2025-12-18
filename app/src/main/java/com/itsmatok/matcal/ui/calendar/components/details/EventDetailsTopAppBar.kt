package com.itsmatok.matcal.ui.calendar.components.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsTopAppBar(
    onNavigateBack: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    actionsEnabled: Boolean
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        },
        actions = {
            if (actionsEnabled) {
                IconButton(onClick = onEditClicked) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
