package com.dimento.app.presentation.groups

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dimento.app.R
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.presentation.components.GroupItem
import kotlin.math.absoluteValue

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
    var groupIcon by remember { mutableStateOf<String?>(null) }
    var groupDescription by remember { mutableStateOf<String?>(null) }
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
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            if (selectedGroup != null) selectedGroup.name else stringResource(R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
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
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (selectedGroup != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                GroupHeaderAction(
                                    icon = Icons.Default.FileDownload,
                                    contentDescription = "Export group",
                                    onClick = { onExportGroupCsv(selectedGroup.groupId) }
                                )
                                GroupHeaderAction(
                                    icon = Icons.Default.Edit,
                                    contentDescription = "Edit group",
                                    onClick = {
                                        groupName = selectedGroup.name
                                        groupIcon = selectedGroup.icon
                                        groupDescription = selectedGroup.description
                                        showRenameGroup = true
                                    }
                                )
                                GroupHeaderAction(
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Delete group",
                                    onClick = {
                                        viewModel.deleteGroup(selectedGroup.groupId)
                                        selectedGroupId = -1L
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )

                SearchIsland(
                    query = query,
                    onQueryChange = {
                        if (selectedGroup != null) selectedGroupId = -1L
                        viewModel.onQueryChange(it)
                    }
                )
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = {
                        groupName = ""
                        groupIcon = null
                        groupDescription = null
                        showCreateGroup = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(imageVector = Icons.Default.CreateNewFolder, contentDescription = "New group")
                }
                FloatingActionButton(onClick = onCreateEventFromFab) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New event")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbars) }
    ) { inner ->
        Box(modifier = Modifier.fillMaxSize()) {
                if (showCreateGroup) {
                    GroupNameDialog(
                        title = "Create Group",
                        actionLabel = "Create",
                        initialName = groupName,
                        initialIcon = groupIcon,
                        initialDescription = groupDescription,
                        onDismiss = {
                            showCreateGroup = false
                            groupDescription = null
                        },
                        onConfirm = { name, icon, description ->
                            viewModel.createGroup(name, icon, description)
                            showCreateGroup = false
                            groupDescription = null
                        }
                    )
                }

                if (showRenameGroup && selectedGroup != null) {
                    GroupNameDialog(
                        title = "Edit Group",
                        actionLabel = "Save",
                        initialName = groupName,
                        initialIcon = groupIcon,
                        initialDescription = groupDescription,
                        onDismiss = { showRenameGroup = false },
                        onConfirm = { name, icon, description ->
                            viewModel.renameGroup(selectedGroup.groupId, name, icon, description)
                            showRenameGroup = false
                            selectedGroupId = -1L
                        }
                    )
                }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
}

@Composable
private fun GroupHeaderAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(25.dp).padding(0.dp)
        )
    }
}

@Composable
private fun SearchIsland(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
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
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = "Search memories",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
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
}

@Composable
fun GroupIconView(
    name: String,
    icon: String?,
    size: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val backgroundColor = remember(name, isDarkTheme) {
        if (name.isBlank()) {
            if (isDarkTheme) Color(0xFF3F4945) else Color(0xFFE0E0E0)
        } else {
            val hash = name.hashCode()
            val hue = (hash.absoluteValue % 360).toFloat()
            Color.hsv(hue, 0.25f, if (isDarkTheme) 0.40f else 0.95f)
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            if (icon.startsWith("vector:")) {
                val iconName = icon.removePrefix("vector:")
                val vector = getVectorIconByName(iconName)
                Icon(
                    imageVector = vector,
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.6f),
                    tint = Color.DarkGray.copy(alpha = 0.8f)
                )
            } else {
                AsyncImage(
                    model = icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            val initials = remember(name) {
                if (name.isBlank()) "?"
                else name.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.take(1) }
                    .joinToString("")
                    .uppercase()
            }
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.DarkGray.copy(alpha = 0.8f)
            )
        }
    }
}

private fun getVectorIconByName(name: String): ImageVector {
    return when (name) {
        "work" -> Icons.Default.Work
        "office" -> Icons.Default.Apartment
        "machine" -> Icons.Default.PrecisionManufacturing
        "bike" -> Icons.AutoMirrored.Filled.DirectionsBike
        "car" -> Icons.Default.DirectionsCar
        "person" -> Icons.Default.Person
        "group" -> Icons.Default.Group
        "team" -> Icons.Default.Groups
        "building" -> Icons.Default.Apartment
        "real_estate" -> Icons.Default.RealEstateAgent
        "tools" -> Icons.Default.Build
        else -> Icons.Default.AddCircle
    }
}

@Composable
private fun GroupNameDialog(
    title: String,
    actionLabel: String,
    initialName: String,
    initialIcon: String?,
    initialDescription: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var icon by remember(initialIcon) { mutableStateOf(initialIcon) }
    var description by remember(initialDescription) { mutableStateOf(initialDescription ?: "") }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { icon = it.toString() }
    }

    val cliparts = listOf(
        "work", "office", "machine", "bike", "car", "person", "group", "team", "building", "real_estate", "tools"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(name, icon, description) }) {
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GroupIconView(name = name, icon = icon, size = 64.dp, fontSize = 20.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { pickerLauncher.launch("image/*") }) {
                            Text("Change Photo")
                        }
                        if (icon != null) {
                            TextButton(onClick = { icon = null }) {
                                Text("Remove", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Group name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                )

                val maxDescriptionChars = 200

                TextField(
                    value = description,
                    onValueChange = { if (it.length <= maxDescriptionChars) description = it },
                    placeholder = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                )
                Text(
                    text = "${description.length} / $maxDescriptionChars",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (description.length >= maxDescriptionChars) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.align(Alignment.End)
                )

                Text("Pick a clipart", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(cliparts) { clipart ->
                        val vector = getVectorIconByName(clipart)
                        val isSelected = icon == "vector:$clipart"
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    icon = if (isSelected) null else "vector:$clipart"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vector,
                                contentDescription = clipart,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    )
}
