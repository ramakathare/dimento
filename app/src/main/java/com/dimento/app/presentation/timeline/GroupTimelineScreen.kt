package com.dimento.app.presentation.timeline

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dimento.app.presentation.components.DateHeader
import com.dimento.app.presentation.components.EventComposerBar
import com.dimento.app.presentation.components.EventBubble
import com.dimento.app.presentation.model.TimelineItem
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTimelineScreen(
    viewModel: GroupTimelineViewModel,
    onBack: () -> Unit,
    onSearchInGroup: (Long) -> Unit
) {
    val items by viewModel.timelineItems.collectAsState()
    val group by viewModel.group.collectAsState()
    val groups by viewModel.allGroups.collectAsState()
    val context = LocalContext.current
    val groupId = group?.id ?: return
    var forwardEventId by remember { mutableLongStateOf(-1L) }
    var destinationGroupId by remember(groups) { mutableLongStateOf(groups.firstOrNull { it.id != groupId }?.id ?: -1L) }
    var quickEventText by remember { mutableStateOf("") }
    var quickEventDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var quickEventHasCustomDateTime by remember { mutableStateOf(false) }

    val openDateTimePicker = {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = if (quickEventHasCustomDateTime) quickEventDateMillis else System.currentTimeMillis()
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        quickEventDateMillis = calendar.timeInMillis
                        quickEventHasCustomDateTime = true
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (forwardEventId > 0) {
            AlertDialog(
            onDismissRequest = { forwardEventId = -1L },
            confirmButton = {
                TextButton(
                    enabled = destinationGroupId > 0,
                    onClick = {
                        viewModel.forward(forwardEventId, destinationGroupId)
                        forwardEventId = -1L
                    }
                ) { Text(stringResource(id = com.dimento.app.R.string.forward)) }
            },
            dismissButton = { TextButton(onClick = { forwardEventId = -1L }) { Text(stringResource(id = com.dimento.app.R.string.cancel)) } },
            title = { Text(stringResource(id = com.dimento.app.R.string.forward_to_group)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    groups.filter { it.id != groupId }.forEach { destination ->
                        androidx.compose.foundation.layout.Row {
                            RadioButton(
                                selected = destinationGroupId == destination.id,
                                onClick = { destinationGroupId = destination.id }
                            )
                            Text(
                                text = destination.name,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
                TopAppBar(
                title = { Text(group?.name ?: stringResource(id = com.dimento.app.R.string.group_label)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = com.dimento.app.R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { onSearchInGroup(groupId) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = stringResource(id = com.dimento.app.R.string.search))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            EventComposerBar(
                text = quickEventText,
                selectedDateMillis = quickEventDateMillis,
                hasCustomDateTime = quickEventHasCustomDateTime,
                onTextChange = { quickEventText = it },
                onPickDateTime = openDateTimePicker,
                onSend = {
                    viewModel.addQuickEvent(
                        text = quickEventText,
                        eventDateMillis = if (quickEventHasCustomDateTime) quickEventDateMillis else System.currentTimeMillis()
                    )
                    quickEventText = ""
                    quickEventDateMillis = System.currentTimeMillis()
                    quickEventHasCustomDateTime = false
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = items, key = { item ->
                when (item) {
                    is TimelineItem.Header -> "header_${item.type}"
                    is TimelineItem.EventRow -> "event_${item.event.id}"
                }
            }) { item ->
                when (item) {
                    is TimelineItem.Header -> DateHeader(type = item.type)
                    is TimelineItem.EventRow -> EventBubble(
                        event = item.event,
                        type = item.type,
                        onMarkComplete = { viewModel.markComplete(item.event.id) },
                        onDelete = { viewModel.delete(item.event.id) },
                        onForward = {
                            forwardEventId = item.event.id
                            if (destinationGroupId <= 0) {
                                destinationGroupId = groups.firstOrNull { it.id != groupId }?.id ?: -1L
                            }
                        }
                    )
                }
            }

            if (items.isEmpty()) {
                item {
                    Text(
                        text = stringResource(id = com.dimento.app.R.string.no_memories_in_group),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
