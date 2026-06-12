package com.dimento.app.presentation.groups

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dimento.app.R
import com.dimento.app.core.ImageStore
import com.dimento.app.core.ValidationConstants
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.presentation.components.DateHeader
import com.dimento.app.presentation.components.EventBubble
import com.dimento.app.presentation.components.EventComposerBar
import com.dimento.app.presentation.components.GroupIconView
import com.dimento.app.presentation.components.GroupItem
import com.dimento.app.presentation.components.getVectorIconByName
import com.dimento.app.presentation.create.CreateEventSharedViewModel
import com.dimento.app.presentation.theme.AppBarStyles
import com.dimento.app.presentation.theme.getSubtleSurfaceColor
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel,
    eventDraftViewModel: CreateEventSharedViewModel,
    onOpenGroup: (Long) -> Unit,
    onCreateEventRequested: () -> Unit,
    onExportGroupCsv: (Long) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val message by viewModel.message.collectAsState()
    val selectedGroupIds by viewModel.selectedGroupIds.collectAsState()
    val draft by eventDraftViewModel.draft.collectAsState()
    val context = LocalContext.current
    
    val snackbars = remember { SnackbarHostState() }
    var showCreateGroup by remember { mutableStateOf(false) }
    var showRenameGroup by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupIcon by remember { mutableStateOf<String?>(null) }
    var groupDescription by remember { mutableStateOf<String?>(null) }
    
    val isSelectionMode = selectedGroupIds.isNotEmpty()
    val selectedGroup = if (selectedGroupIds.size == 1) {
        groups.find { it.groupId == selectedGroupIds.first() }
    } else null

    var showSearchField by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showSearchField) {
        if (showSearchField) {
            searchFocusRequester.requestFocus()
        }
    }

    val undoLabel = stringResource(R.string.undo)

    BackHandler(enabled = isSelectionMode || showSearchField) {
        if (isSelectionMode) {
            viewModel.clearSelection()
        } else if (showSearchField) {
            showSearchField = false
            viewModel.onQueryChange("")
        }
    }

    LaunchedEffect(message) {
        message?.let {
            val result = snackbars.showSnackbar(
                message = it,
                actionLabel = if (it.contains("deleted", ignoreCase = true)) undoLabel else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
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
                            when {
                                selectedGroupIds.size > 1 -> stringResource(R.string.selected_count, selectedGroupIds.size)
                                selectedGroup != null -> selectedGroup.name
                                showSearchField -> stringResource(R.string.search_memories_title)
                                else -> stringResource(R.string.app_name)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        if (isSelectionMode || showSearchField) {
                            IconButton(
                                onClick = {
                                    if (isSelectionMode) {
                                        viewModel.clearSelection()
                                    } else {
                                        showSearchField = false
                                        viewModel.onQueryChange("")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                            }
                        }
                    },
                    actions = {
                        if (isSelectionMode) {
                            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                if (selectedGroupIds.size == 1 && selectedGroup != null) {
                                    GroupHeaderAction(
                                        icon = Icons.Default.FileDownload,
                                        contentDescription = stringResource(id = R.string.export_group),
                                        onClick = { onExportGroupCsv(selectedGroup.groupId) }
                                    )
                                    GroupHeaderAction(
                                        icon = Icons.Default.Edit,
                                        contentDescription = stringResource(id = R.string.edit_group_action),
                                        onClick = {
                                            groupName = selectedGroup.name
                                            groupIcon = selectedGroup.icon
                                            groupDescription = selectedGroup.description
                                            showRenameGroup = true
                                        }
                                    )
                                }
                                GroupHeaderAction(
                                    icon = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.delete_group),
                                    onClick = {
                                        viewModel.deleteSelectedGroups()
                                    }
                                )
                            }
                        } else if (!showSearchField) {
                            IconButton(onClick = { showSearchField = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.search)
                                )
                            }
                        }
                    },
                    colors = AppBarStyles.defaultColors()
                )

                if (showSearchField) {
                    SearchIsland(
                        query = query,
                        onQueryChange = {
                            if (isSelectionMode) viewModel.clearSelection()
                            viewModel.onQueryChange(it)
                        },
                        focusRequester = searchFocusRequester
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    groupName = ""
                    groupIcon = null
                    groupDescription = null
                    showCreateGroup = true
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(imageVector = Icons.Default.CreateNewFolder, contentDescription = stringResource(id = R.string.new_group))
            }
        },
        bottomBar = {
            val openDateTimePicker = {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = if (draft.hasCustomDateTime) {
                        draft.eventDateMillis
                    } else {
                        System.currentTimeMillis()
                    }
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
                                eventDraftViewModel.updateEventDateMillis(calendar.timeInMillis)
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

            EventComposerBar(
                text = draft.text,
                selectedDateMillis = draft.eventDateMillis,
                hasCustomDateTime = draft.hasCustomDateTime,
                onTextChange = eventDraftViewModel::updateText,
                onPickDateTime = openDateTimePicker,
                onClearDateTime = eventDraftViewModel::clearDateTime,
                onSend = {
                    if (draft.text.isNotBlank()) {
                        eventDraftViewModel.setSourceGroupId(null)
                        onCreateEventRequested()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbars) }
    ) { inner ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (showCreateGroup) {
                GroupNameDialog(
                    title = stringResource(id = R.string.create_group_title),
                    actionLabel = stringResource(id = R.string.create_label),
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
                    title = stringResource(id = R.string.edit_group_title),
                    actionLabel = stringResource(id = R.string.save_label),
                    initialName = groupName,
                    initialIcon = groupIcon,
                    initialDescription = groupDescription,
                    onDismiss = { showRenameGroup = false },
                    onConfirm = { name, icon, description ->
                        viewModel.renameGroup(selectedGroup.groupId, name, icon, description)
                        showRenameGroup = false
                        viewModel.clearSelection()
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
                        val isSelected = summary.groupId in selectedGroupIds
                        GroupItem(
                            summary = summary,
                            selected = isSelected,
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(summary.groupId)
                                } else {
                                    onOpenGroup(summary.groupId)
                                }
                            },
                            onLongClick = {
                                if (!isSelectionMode) {
                                    viewModel.enterSelectionMode(summary.groupId)
                                }
                            }
                        )
                    }

                    if (groups.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.no_groups_yet),
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
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
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

private fun LazyListScope.searchContent(
    results: SearchResult,
    onOpenGroup: (Long) -> Unit
) {
    if (results.groups.isNotEmpty()) {
        item("groups_header") {
            SearchSectionHeader(text = stringResource(id = R.string.groups_label))
        }
        items(items = results.groups, key = { "group_${it.id}" }) { group ->
            GroupItem(
                summary = com.dimento.app.domain.model.GroupSummary(
                    groupId = group.id,
                    name = group.name,
                    description = group.description,
                    lastMessage = null,
                    lastEventDateMillis = null,
                    hasFutureEvents = false,
                    icon = group.icon
                ),
                onClick = { onOpenGroup(group.id) }
            )
        }
    }

    if (results.matchedEvents.isNotEmpty()) {
        val nowMillis = System.currentTimeMillis()
        val grouped = results.matchedEvents.groupBy { it.groupName }
        grouped.forEach { (groupName, events) ->
            item("events_header_$groupName") {
                SearchSectionHeader(text = groupName)
            }
            items(items = events, key = { "event_${it.event.id}" }) { result ->
                val type = com.dimento.app.domain.util.EventTypeResolver().resolve(result.event.eventDateMillis, nowMillis)
                EventBubble(
                    event = result.event,
                    type = type,
                    onClick = { onOpenGroup(result.event.groupId) }
                )
            }
        }
    }

    if (results.groups.isEmpty() && results.matchedEvents.isEmpty()) {
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
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
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
private fun GroupFormContent(
    name: String,
    description: String,
    icon: String?,
    cliparts: List<String>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIconChange: (String?) -> Unit,
    onPickImage: () -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                GroupIconView(
                    name = name,
                    icon = icon,
                    size = 88.dp,
                    fontSize = 24.sp
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPickImage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            val listState = rememberLazyListState()
            val chunked = cliparts.chunked(2)

            Box(
                modifier = Modifier
                    .height(88.dp)
                    .weight(1f)
            ) {
                LazyRow(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 24.dp)
                ) {
                    items(chunked) { columnItems ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            columnItems.forEach { clipart ->
                                val vector = getVectorIconByName(clipart)
                                val isSelected = icon == "vector:$clipart"

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            2.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable {
                                            onIconChange(
                                                if (isSelected) null else "vector:$clipart"
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = vector,
                                        contentDescription = clipart,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (columnItems.size == 1) {
                                Spacer(modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                }

                val showFade by remember {
                    derivedStateOf { listState.canScrollForward }
                }

                if (showFade) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(24.dp)
                            .align(Alignment.CenterEnd)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }
        }

        val maxName = ValidationConstants.MAX_GROUP_NAME_LENGTH
        val showNameCounter = name.length > (maxName * 0.75)

        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = name,
                onValueChange = {
                    if (it.length <= maxName) onNameChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 12.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    if (name.isEmpty()) {
                        Text(
                            stringResource(R.string.group_name_placeholder),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )

            if (showNameCounter) {
                Text(
                    "${name.length}/$maxName",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (name.length >= maxName)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                )
            }
        }

        val maxDesc = ValidationConstants.MAX_GROUP_DESCRIPTION_LENGTH
        val showDescCounter = description.length > (maxDesc * 0.75)

        Box {
            BasicTextField(
                value = description,
                onValueChange = {
                    if (it.length <= maxDesc) onDescriptionChange(it)
                },
                minLines = 1,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    if (description.isEmpty()) {
                        Text(
                            stringResource(R.string.description_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )

            if (showDescCounter) {
                Text(
                    "${description.length}/$maxDesc",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (description.length >= maxDesc)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun GroupNameDialog(
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

    val ctx = LocalContext.current

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            ImageStore.saveUriToAppImages(ctx, it)?.let { savedPath ->
                icon = savedPath
            }
        }
    }

    val cliparts = listOf(
        "work", "office", "machine", "bike", "car",
        "person", "group", "team", "building", "real_estate", "tools"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                GroupFormContent(
                    name = name,
                    description = description,
                    icon = icon,
                    cliparts = cliparts,
                    onNameChange = { name = it },
                    onDescriptionChange = { description = it },
                    onIconChange = { icon = it },
                    onPickImage = { pickerLauncher.launch("image/*") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = { onConfirm(name, icon, description) },
                        enabled = name.isNotBlank()
                    ) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}
