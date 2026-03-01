package com.itsmatok.matcal.ui.calendar.components.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import com.itsmatok.matcal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    viewMode: CalendarViewMode,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchActivate: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchClose: () -> Unit,
    onViewModeChanged: (CalendarViewMode) -> Unit,
    onAddEventClicked: () -> Unit,
    onLicenseClicked: () -> Unit,
    onImportClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onManageCalendarsClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.search_events)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.common_menu)
                    )
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(viewModeMenuLabel(CalendarViewMode.AGENDA, viewMode)) },
                        onClick = {
                            showMenu = false
                            onViewModeChanged(CalendarViewMode.AGENDA)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(viewModeMenuLabel(CalendarViewMode.DAY, viewMode)) },
                        onClick = {
                            showMenu = false
                            onViewModeChanged(CalendarViewMode.DAY)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(viewModeMenuLabel(CalendarViewMode.WEEK, viewMode)) },
                        onClick = {
                            showMenu = false
                            onViewModeChanged(CalendarViewMode.WEEK)
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(text = { Text(stringResource(R.string.screen_manage_calendars)) }, onClick = {
                        showMenu = false
                        onManageCalendarsClicked()
                    })

                    DropdownMenuItem(text = { Text(stringResource(R.string.calendar_import_from_url)) }, onClick = {
                        showMenu = false
                        onImportClicked()
                    })


                    DropdownMenuItem(text = { Text(stringResource(R.string.screen_open_source_licenses)) }, onClick = {
                        showMenu = false
                        onLicenseClicked()
                    })
                }
            }
        },
        actions = {
            if (isSearchActive) {
                IconButton(onClick = onSearchClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.search_close)
                    )
                }
            } else {
                IconButton(onClick = onSearchActivate) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_events)
                    )
                }

                IconButton(onClick = onRefreshClicked) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.calendar_force_refresh)
                    )
                }
                IconButton(onClick = onAddEventClicked) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.calendar_add_event)
                    )
                }
            }
        })
}

@Composable
private fun viewModeMenuLabel(
    mode: CalendarViewMode,
    currentMode: CalendarViewMode
): String {
    val modeLabel = stringResource(mode.labelRes)
    return if (mode == currentMode) {
        stringResource(R.string.calendar_view_mode_current, modeLabel)
    } else {
        modeLabel
    }
}
