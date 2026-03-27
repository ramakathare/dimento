package com.dimento.app.presentation.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimento.app.presentation.components.DateHeader
import com.dimento.app.presentation.components.EventBubble
import com.dimento.app.presentation.components.InputBar
import com.dimento.app.presentation.model.TimelineItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTimelineScreen(
    viewModel: GroupTimelineViewModel,
    onBack: () -> Unit,
    onCreateInGroup: (Long) -> Unit,
    onSearchInGroup: (Long) -> Unit
) {
    val items by viewModel.timelineItems.collectAsState()
    val group by viewModel.group.collectAsState()
    val groups by viewModel.allGroups.collectAsState()
    val groupId = group?.id ?: return
    var forwardEventId by remember { mutableLongStateOf(-1L) }
    var destinationGroupId by remember(groups) { mutableLongStateOf(groups.firstOrNull { it.id != groupId }?.id ?: -1L) }

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
                ) { Text("Forward") }
            },
            dismissButton = { TextButton(onClick = { forwardEventId = -1L }) { Text("Cancel") } },
            title = { Text("Forward to group") },
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
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Group") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onSearchInGroup(groupId) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onCreateInGroup(groupId) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create event")
            }
        },
        bottomBar = {
            InputBar(onSend = viewModel::addQuickEvent)
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
                        text = "No memories in this group.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
