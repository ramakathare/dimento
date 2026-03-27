package com.dimento.app.presentation.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimento.app.presentation.components.GroupItem
import com.dimento.app.presentation.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel,
    onOpenGroup: (Long) -> Unit,
    onCreateEventFromFab: () -> Unit,
    onSearch: () -> Unit,
    onExportCsv: () -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbars = remember { SnackbarHostState() }
    var showCreateGroup by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }

    LaunchedEffect(message) {
        message?.let {
            snackbars.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = { Text("Memories") },
                actions = {
                    IconButton(onClick = onExportCsv) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Export CSV")
                    }
                    IconButton(onClick = onSearch) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
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
            AlertDialog(
                onDismissRequest = { showCreateGroup = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.createGroup(groupName)
                            groupName = ""
                            showCreateGroup = false
                        }
                    ) { Text("Create") }
                },
                dismissButton = { TextButton(onClick = { showCreateGroup = false }) { Text("Cancel") } },
                title = { Text("Create Group") },
                text = {
                    TextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        placeholder = { Text("Group name") }
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(Surface),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = groups, key = { it.groupId }) { summary ->
                GroupItem(summary = summary, onClick = { onOpenGroup(summary.groupId) })
            }
            if (groups.isEmpty()) {
                item {
                    Text(
                        text = "No memory groups yet.",
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
