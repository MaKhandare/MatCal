package com.itsmatok.matcal.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    onAddEventClicked: () -> Unit,
    onLicenseClicked: () -> Unit,
    onImportClicked: () -> Unit,
    onRefreshClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Menu"
                    )
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("Open Source Licenses") }, onClick = {
                        showMenu = false
                        onLicenseClicked()
                    })

                    DropdownMenuItem(text = { Text("Import from URL") }, onClick = {
                        showMenu = false
                        onImportClicked()
                    })
                }
            }
        },
        actions = {
            IconButton(onClick = onRefreshClicked) {
                Icon(
                    imageVector = Icons.Default.Refresh, contentDescription = "Force Refresh"
                )
            }
            IconButton(onClick = onAddEventClicked) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Event"
                )
            }
        })
}