package com.itsmatok.matcal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    onMenuClicked: () -> Unit = {},
    onAddEventClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "menu"
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Force Refresh"
                )
            }
            IconButton(onClick = onAddEventClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event"
                )
            }
        }
    )
}