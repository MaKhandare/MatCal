package com.itsmatok.matcal.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itsmatok.matcal.ui.calendar.components.details.EventDeleteDialog
import com.itsmatok.matcal.ui.calendar.components.details.EventDetailsContent
import com.itsmatok.matcal.ui.calendar.components.details.EventDetailsTopAppBar
import com.itsmatok.matcal.viewmodels.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarEventDetailsScreen(
    viewModel: CalendarViewModel,
    eventId: Int,
    onNavigateBack: () -> Unit,
    onEditEvent: (Int) -> Unit
) {

    val eventState by viewModel.getEventById(eventId).collectAsState(initial = null)
    val event = eventState

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        EventDeleteDialog(
            onConfirm = {
                viewModel.deleteEvent(eventId)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            EventDetailsTopAppBar(
                onNavigateBack = onNavigateBack,
                onEditClicked = { onEditEvent(eventId) },
                onDeleteClicked = { showDeleteDialog = true },
                actionsEnabled = event != null
            )
        }
    ) { innerPadding ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EventDetailsContent(
                event = event,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}