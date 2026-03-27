package com.dimento.app.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimento.app.domain.usecase.ObserveGroupsUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectGroupScreen(
    observeGroupsUseCase: ObserveGroupsUseCase,
    viewModel: CreateEventSharedViewModel,
    onBack: () -> Unit,
    onSent: (Long) -> Unit
) {
    val groupsFlow = remember(observeGroupsUseCase) { observeGroupsUseCase() }
    val groups by groupsFlow.collectAsState(initial = emptyList())
    var selectedGroupId by remember(groups) { mutableLongStateOf(groups.firstOrNull()?.id ?: -1L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Group") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = groups, key = { it.id }) { group ->
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGroupId == group.id,
                            onClick = { selectedGroupId = group.id }
                        )
                        Text(text = group.name)
                    }
                }
            }
            Button(
                onClick = { if (selectedGroupId > 0) viewModel.commit(selectedGroupId) { onSent(selectedGroupId) } },
                enabled = selectedGroupId > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
            }
        }
    }
}
