package com.dimento.app.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimento.app.MainActivity
import com.dimento.app.R
import com.dimento.app.core.CsvExporter
import com.dimento.app.core.CsvImporter
import com.dimento.app.core.ServiceLocator
import com.dimento.app.notifications.EventNotificationScheduler
import com.dimento.app.presentation.components.AppBackground
import com.dimento.app.presentation.create.CreateEventSharedViewModel
import com.dimento.app.presentation.create.SelectGroupScreen
import com.dimento.app.presentation.groups.GroupsScreen
import com.dimento.app.presentation.groups.GroupsViewModel
import com.dimento.app.presentation.navigation.DiMentoRoute
import com.dimento.app.presentation.search.SearchScreen
import com.dimento.app.presentation.search.SearchViewModel
import com.dimento.app.presentation.theme.DiMentoTheme
import com.dimento.app.presentation.timeline.GroupTimelineScreen
import com.dimento.app.presentation.timeline.GroupTimelineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DiMentoAppContent() {
    val context = LocalContext.current
    val container = remember { ServiceLocator.container }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val csvImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    val csvImporter = CsvImporter(context)
                    val csvContent = csvImporter.readCsvContent(uri)
                    val (alarmsToCancel, alarmsToSchedule) = container.importEventsCsvUseCase(csvContent)
                    alarmsToCancel.forEach { EventNotificationScheduler.cancel(context, it) }
                    alarmsToSchedule.forEach { (eventId, eventDateMillis) ->
                        EventNotificationScheduler.schedule(context, eventId, eventDateMillis)
                    }
                }.onSuccess {
                    Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_LONG).show()
                }.onFailure { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.import_failed) + ": " + error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    val groupsViewModel: GroupsViewModel = viewModel(
        factory = simpleFactory {
            GroupsViewModel(
                observeGroupSummariesUseCase = container.observeGroupSummariesUseCase,
                createGroupUseCase = container.createGroupUseCase,
                renameGroupUseCase = container.renameGroupUseCase,
                deleteGroupUseCase = container.deleteGroupUseCase,
                searchMemoriesUseCase = container.searchMemoriesUseCase
            )
        }
    )
    val createEventSharedViewModel: CreateEventSharedViewModel = viewModel(
        factory = simpleFactory {
            CreateEventSharedViewModel(createEventUseCase = container.createEventUseCase)
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(Modifier.fillMaxSize())

        val activity = context as? MainActivity
        LaunchedEffect(activity?.sharedText) {
            val shared = activity?.sharedText ?: return@LaunchedEffect
            activity?.sharedText = null
            createEventSharedViewModel.updateText(shared)
            navController.navigate(DiMentoRoute.Groups.route) {
                popUpTo(DiMentoRoute.Groups.route) { inclusive = true }
            }
        }
        LaunchedEffect(activity?.pendingRescheduleEventId) {
            val eventId = activity?.pendingRescheduleEventId ?: return@LaunchedEffect
            activity?.pendingRescheduleEventId = -1L
            val event = kotlinx.coroutines.withContext(Dispatchers.IO) {
                runCatching { container.repository.getEvent(eventId) }.getOrNull()
            }
            if (event != null) {
                navController.navigate(DiMentoRoute.GroupTimeline.create(event.groupId, event.id))
            }
        }

        NavHost(
            navController = navController,
            startDestination = DiMentoRoute.Groups.route
        ) {
            composable(DiMentoRoute.Groups.route) {
                GroupsScreen(
                    viewModel = groupsViewModel,
                    onOpenGroup = { navController.navigate(DiMentoRoute.GroupTimeline.create(it)) },
                    eventDraftViewModel = createEventSharedViewModel,
                    onCreateEventRequested = {
                        navController.navigate(DiMentoRoute.SelectGroup.route)
                    },
                    onExportGroupCsv = { groupId ->
                        scope.launch {
                            runCatching {
                                val export = container.exportGroupEventsCsvUseCase(
                                    groupId = groupId,
                                    nowMillis = System.currentTimeMillis()
                                )
                                checkNotNull(CsvExporter(context).export(export.fileName, export.content)) {
                                    context.getString(R.string.export_failed)
                                }
                            }.onSuccess {
                                Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message ?: context.getString(R.string.export_failed), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onExportAllCsv = {
                        scope.launch {
                            runCatching {
                                val export = container.exportEventsCsvUseCase(
                                    nowMillis = System.currentTimeMillis()
                                )
                                checkNotNull(CsvExporter(context).export(export.fileName, export.content)) {
                                    context.getString(R.string.export_failed)
                                }
                            }.onSuccess {
                                Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message ?: context.getString(R.string.export_failed), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onImportCsv = { csvImportLauncher.launch("text/*") }
                )
            }

            composable(
                route = DiMentoRoute.GroupTimeline.route,
                arguments = listOf(
                    navArgument("groupId") { type = NavType.LongType },
                    navArgument("editEventId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
                val editEventId = backStackEntry.arguments?.getLong("editEventId") ?: -1L
                val timelineViewModel: GroupTimelineViewModel = viewModel(
                    factory = GroupTimelineViewModel.Factory(
                        groupId = groupId,
                        observeTimelineUseCase = container.observeTimelineUseCase,
                        observeGroupsUseCase = container.observeGroupsUseCase,
                        getGroupUseCase = container.getGroupUseCase,
                        createEventUseCase = container.createEventUseCase,
                        forwardEventUseCase = container.forwardEventUseCase,
                        deleteEventUseCase = container.deleteEventUseCase,
                        updateEventUseCase = container.updateEventUseCase,
                        searchMemoriesUseCase = container.searchMemoriesUseCase,
                        eventTypeResolver = container.eventTypeResolver
                    )
                )
                GroupTimelineScreen(
                    viewModel = timelineViewModel,
                    editEventId = editEventId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(DiMentoRoute.SelectGroup.route) {
                SelectGroupScreen(
                    observeGroupsUseCase = container.observeGroupsUseCase,
                    viewModel = createEventSharedViewModel,
                    onBack = { navController.popBackStack() },
                    onSent = { selectedGroupId ->
                        navController.popBackStack(DiMentoRoute.Groups.route, false)
                        navController.navigate(DiMentoRoute.GroupTimeline.create(selectedGroupId))
                    }
                )
            }

            composable(
                route = DiMentoRoute.Search.route,
                arguments = listOf(navArgument("groupId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { entry ->
                val arg = entry.arguments?.getLong("groupId") ?: -1L
                val groupId = arg.takeIf { it > 0 }
                val vm: SearchViewModel = viewModel(
                    key = "search_$groupId",
                    factory = simpleFactory {
                        SearchViewModel(groupId = groupId, searchMemoriesUseCase = container.searchMemoriesUseCase)
                    }
                )
                SearchScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onOpenGroup = { navController.navigate(DiMentoRoute.GroupTimeline.create(it)) }
                )
            }
        }
    }
}

fun <T : ViewModel> simpleFactory(create: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
}
