package com.dimento.app.presentation.groups

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimento.app.R
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.presentation.components.GroupItem
import com.dimento.app.presentation.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel,
    onOpenGroup: (Long) -> Unit,
    onCreateEventFromFab: () -> Unit,
    onExportGroupCsv: (Long) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbars = remember { SnackbarHostState() }
    var showCreateGroup by remember { mutableStateOf(false) }
    var showRenameGroup by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableLongStateOf(-1L) }

    val selectedGroup = groups.firstOrNull { it.groupId == selectedGroupId }
    val hasOverlayState = selectedGroup != null

    BackHandler(enabled = hasOverlayState || query.isNotBlank()) {
        if (selectedGroup != null) {
            selectedGroupId = -1L
        } else {
            viewModel.onQueryChange("")
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbars.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            if (selectedGroup != null) selectedGroup.name else stringResource(R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (hasOverlayState || query.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    if (selectedGroup != null) {
                                        selectedGroupId = -1L
                                    } else {
                                        viewModel.onQueryChange("")
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (selectedGroup != null) {
                            IconButton(onClick = { onExportGroupCsv(selectedGroup.groupId) }) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Export group"
                                )
                            }
                            IconButton(
                                onClick = {
                                    groupName = selectedGroup.name
                                    showRenameGroup = true
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit group")
                            }
                            IconButton(
                                onClick = {
                                    viewModel.deleteGroup(selectedGroup.groupId)
                                    selectedGroupId = -1L
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete group")
                            }
                        }
                    }
                )

                TextField(
                    value = query,
                    onValueChange = {
                        if (selectedGroup != null) selectedGroupId = -1L
                        viewModel.onQueryChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    placeholder = { Text("Search memories") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                )
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = { showCreateGroup = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = "New group")
                }
                FloatingActionButton(onClick = onCreateEventFromFab) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New event")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbars) }
    ) { inner ->
        if (showCreateGroup) {
            GroupNameDialog(
                title = "Create Group",
                actionLabel = "Create",
                initialValue = groupName,
                onDismiss = {
                    showCreateGroup = false
                    groupName = ""
                },
                onConfirm = { value ->
                    viewModel.createGroup(value)
                    groupName = ""
                    showCreateGroup = false
                }
            )
        }

        if (showRenameGroup && selectedGroup != null) {
            GroupNameDialog(
                title = "Edit Group",
                actionLabel = "Save",
                initialValue = groupName,
                onDismiss = { showRenameGroup = false },
                onConfirm = { value ->
                    viewModel.renameGroup(selectedGroup.groupId, value)
                    showRenameGroup = false
                    selectedGroupId = -1L
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (query.isBlank()) {
                items(items = groups, key = { it.groupId }) { summary ->
                    GroupItem(
                        summary = summary,
                        selected = summary.groupId == selectedGroupId,
                        onClick = {
                            if (selectedGroupId == summary.groupId) {
                                selectedGroupId = -1L
                            } else if (selectedGroupId != -1L) {
                                selectedGroupId = summary.groupId
                            } else {
                                onOpenGroup(summary.groupId)
                            }
                        },
                        onLongClick = { selectedGroupId = summary.groupId }
                    )
                }
                if (groups.isEmpty()) {
                    item {
                        Text(
                            text = "No memory groups yet.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                searchContent(
                    results = results,
                    onOpenGroup = onOpenGroup
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.searchContent(
    results: SearchResult,
    onOpenGroup: (Long) -> Unit
) {
    if (results.groups.isNotEmpty()) {
        item("groups_header") {
            SearchSectionHeader(text = "Groups")
        }
        items(items = results.groups, key = { "group_${it.id}" }) { group ->
            SearchResultRow(
                title = group.name,
                subtitle = "Matched in group name",
                onClick = { onOpenGroup(group.id) }
            )
        }
    }

    if (results.matchedEvents.isNotEmpty()) {
        item("events_header") {
            SearchSectionHeader(text = "Events")
        }
        items(items = results.matchedEvents, key = { "event_${it.event.id}" }) { result ->
            SearchResultRow(
                title = result.groupName,
                subtitle = result.event.text,
                onClick = { onOpenGroup(result.event.groupId) }
            )
        }
    }

    if (results.groups.isEmpty() && results.matchedEvents.isEmpty()) {
        item("empty_search") {
            Text(
                text = "No matches found.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchSectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SearchResultRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GroupNameDialog(
    title: String,
    actionLabel: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text(actionLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(title) },
        text = {
            TextField(
                value = value,
                onValueChange = { value = it },
                placeholder = { Text("Group name") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            )
        }
    )
}
