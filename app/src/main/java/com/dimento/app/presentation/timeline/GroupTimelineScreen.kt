package com.dimento.app.presentation.timeline

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimento.app.R
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.notifications.EventNotificationScheduler
import com.dimento.app.presentation.components.DateHeader
import com.dimento.app.presentation.components.EventBubble
import com.dimento.app.presentation.components.EventComposerBar
import com.dimento.app.presentation.theme.AppBarStyles
import com.dimento.app.presentation.theme.getSubtleSurfaceColor
import com.dimento.app.presentation.model.TimelineItem
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTimelineScreen(
    viewModel: GroupTimelineViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.timelineItems.collectAsState()
    val group by viewModel.group.collectAsState()
    val groups by viewModel.allGroups.collectAsState()
    val selectedEventIds by viewModel.selectedEventIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val context = LocalContext.current
    val groupId = group?.id ?: return

    val isSelectionMode = selectedEventIds.isNotEmpty()

    var forwardEventId by remember { mutableLongStateOf(-1L) }
    var destinationGroupId by remember(groups) { mutableLongStateOf(groups.firstOrNull { it.id != groupId }?.id ?: -1L) }
    var quickEventText by remember { mutableStateOf("") }
    var quickEventDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var quickEventHasCustomDateTime by remember { mutableStateOf(false) }

    // Edit state: when non-null, the EventComposerBar is bound to this event
    data class EditingEvent(val id: Long, val text: String, val dateMillis: Long)
    var editingEvent by remember { mutableStateOf<EditingEvent?>(null) }

    // Handle deep link from notification "Reschedule" action
    LaunchedEffect(Unit) {
        val activity = context as? androidx.activity.ComponentActivity
        val editEventId = activity?.intent?.getLongExtra(
            com.dimento.app.notifications.NotificationActionReceiver.EXTRA_EDIT_EVENT_ID, -1L
        ) ?: -1L
        if (editEventId > 0) {
            val event = viewModel.findEvent(editEventId)
            if (event != null) {
                quickEventText = event.text
                quickEventDateMillis = event.eventDateMillis
                quickEventHasCustomDateTime = true
                editingEvent = EditingEvent(event.id, event.text, event.eventDateMillis)
                activity?.intent?.removeExtra(com.dimento.app.notifications.NotificationActionReceiver.EXTRA_EDIT_EVENT_ID)
            }
        }
    }

    var showSearchField by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showSearchField) {
        if (showSearchField) {
            searchFocusRequester.requestFocus()
        }
    }

    BackHandler(enabled = isSelectionMode || showSearchField) {
        if (isSelectionMode) {
            viewModel.clearSelection()
        } else if (showSearchField) {
            showSearchField = false
            viewModel.onSearchQueryChange("")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onScheduleNotification.collect { notification ->
            EventNotificationScheduler.schedule(context, notification.eventId, notification.eventDateMillis)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onCancelNotification.collect { eventId ->
            EventNotificationScheduler.cancel(context, eventId)
        }
    }

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
                ) {
                    Text(stringResource(id = com.dimento.app.R.string.forward))
                }
            },
            dismissButton = {
                TextButton(onClick = { forwardEventId = -1L }) {
                    Text(stringResource(id = com.dimento.app.R.string.cancel))
                }
            },
            title = { Text(stringResource(id = com.dimento.app.R.string.forward_to_group)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    groups.filter { it.id != groupId }.forEach { destination ->
                        Row {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = when {
                                selectedEventIds.size > 1 -> stringResource(id = com.dimento.app.R.string.selected_count, selectedEventIds.size)
                                isSelectionMode -> stringResource(id = com.dimento.app.R.string.common_selected)
                                showSearchField -> stringResource(id = com.dimento.app.R.string.search_memories_title)
                                else -> group?.name ?: stringResource(id = com.dimento.app.R.string.group_label)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (isSelectionMode || showSearchField) {
                            IconButton(onClick = {
                                if (isSelectionMode) viewModel.clearSelection()
                                else {
                                    showSearchField = false
                                    viewModel.onSearchQueryChange("")
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = com.dimento.app.R.string.back)
                                )
                            }
                        } else {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = com.dimento.app.R.string.back)
                                )
                            }
                        }
                    },
                    actions = {
                        if (isSelectionMode) {
                            if (selectedEventIds.size == 1) {
                                IconButton(onClick = {
                                    val event = items.filterIsInstance<TimelineItem.EventRow>()
                                        .find { it.event.id == selectedEventIds.first() }?.event
                                    if (event != null) {
                                        quickEventText = event.text
                                        quickEventDateMillis = event.eventDateMillis
                                        quickEventHasCustomDateTime = true
                                        editingEvent = EditingEvent(event.id, event.text, event.eventDateMillis)
                                    }
                                    viewModel.clearSelection()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(id = com.dimento.app.R.string.edit_event_action)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            IconButton(onClick = { viewModel.deleteSelectedEvents() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = com.dimento.app.R.string.delete_selected_events)
                                )
                            }
                        } else if (!showSearchField) {
                            IconButton(onClick = { showSearchField = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = com.dimento.app.R.string.search)
                                )
                            }
                        }
                    },
                    colors = AppBarStyles.defaultColors()
                )

                // Search bar (appears below top bar when search icon is clicked)
                if (showSearchField) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        focusRequester = searchFocusRequester
                    )
                }
            }
        },
        bottomBar = {
            if (!isSelectionMode && !showSearchField) {
                EventComposerBar(
                    text = quickEventText,
                    selectedDateMillis = quickEventDateMillis,
                    hasCustomDateTime = quickEventHasCustomDateTime,
                    isEditing = editingEvent != null,
                    onTextChange = { quickEventText = it },
                    onPickDateTime = openDateTimePicker,
                    onClearDateTime = {
                        quickEventHasCustomDateTime = false
                        quickEventDateMillis = System.currentTimeMillis()
                    },
                    onSend = {
                        val edit = editingEvent
                        if (edit != null) {
                            // Update existing event
                            viewModel.updateEvent(edit.id, quickEventText,
                                if (quickEventHasCustomDateTime) quickEventDateMillis else System.currentTimeMillis())
                            editingEvent = null
                        } else {
                            // Create new event
                            viewModel.addQuickEvent(
                                text = quickEventText,
                                eventDateMillis = if (quickEventHasCustomDateTime) quickEventDateMillis else System.currentTimeMillis()
                            )
                        }
                        quickEventText = ""
                        quickEventDateMillis = System.currentTimeMillis()
                        quickEventHasCustomDateTime = false
                    },
                    onCancelEdit = {
                        editingEvent = null
                        quickEventText = ""
                        quickEventDateMillis = System.currentTimeMillis()
                        quickEventHasCustomDateTime = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { inner ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (showSearchField && searchQuery.isNotBlank()) {
                // Search results
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    searchContent(
                        results = searchResults
                    )
                }
            } else {
                // Normal timeline
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(
                        items = items,
                        key = { item ->
                            when (item) {
                                is TimelineItem.Header -> "header_${item.type}"
                                is TimelineItem.EventRow -> "event_${item.event.id}"
                            }
                        }
                    ) { item ->
                        when (item) {
                            is TimelineItem.Header -> DateHeader(type = item.type)
                            is TimelineItem.EventRow -> {
                                val eventId = item.event.id
                                val isSelected = eventId in selectedEventIds
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    EventBubble(
                                        event = item.event,
                                        type = item.type,
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelectionMode) {
                                                viewModel.toggleSelection(eventId)
                                            }
                                        },
                                        onLongClick = {
                                            viewModel.enterSelectionMode(eventId)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
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
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = getSubtleSurfaceColor(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = stringResource(id = R.string.search_memories_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.searchContent(
    results: SearchResult
) {
    if (results.matchedEvents.isNotEmpty()) {
        val nowMillis = System.currentTimeMillis()
        items(items = results.matchedEvents, key = { "event_${it.event.id}" }) { result ->
            val type = com.dimento.app.domain.util.EventTypeResolver().resolve(result.event.eventDateMillis, nowMillis)
            EventBubble(
                event = result.event,
                type = type,
                onClick = { }
            )
        }
    }

    if (results.matchedEvents.isEmpty() && results.groups.isEmpty()) {
        item("empty_search") {
            Text(
                text = stringResource(id = R.string.no_matches_found),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
