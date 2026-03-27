package com.dimento.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimento.app.core.CsvExporter
import com.dimento.app.core.ServiceLocator
import com.dimento.app.presentation.create.CreateEventScreen
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(this)
        maybeAskNotificationPermission()
        setContent {
            DiMentoTheme {
                DiMentoAppRoot()
            }
        }
    }

    private fun maybeAskNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
private fun DiMentoAppRoot() {
    val context = LocalContext.current
    val container = remember { ServiceLocator.container }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val groupsViewModel: GroupsViewModel = viewModel(
        factory = simpleFactory {
            GroupsViewModel(
                observeGroupSummariesUseCase = container.observeGroupSummariesUseCase,
                createGroupUseCase = container.createGroupUseCase
            )
        }
    )
    val createEventSharedViewModel: CreateEventSharedViewModel = viewModel(
        factory = simpleFactory {
            CreateEventSharedViewModel(createEventUseCase = container.createEventUseCase)
        }
    )

    NavHost(
        navController = navController,
        startDestination = DiMentoRoute.Groups.route
    ) {
        composable(DiMentoRoute.Groups.route) {
            GroupsScreen(
                viewModel = groupsViewModel,
                onOpenGroup = { navController.navigate(DiMentoRoute.GroupTimeline.create(it)) },
                onCreateEventFromFab = {
                    createEventSharedViewModel.setSourceGroupId(null)
                    navController.navigate(DiMentoRoute.CreateEvent.create(groupId = null))
                },
                onSearch = { navController.navigate(DiMentoRoute.Search.create(groupId = null)) },
                onExportCsv = {
                    scope.launch {
                        runCatching {
                            val export = container.exportEventsCsvUseCase(System.currentTimeMillis())
                            checkNotNull(CsvExporter(context).export(export.fileName, export.content)) {
                                "Export failed."
                            }
                        }.onSuccess {
                            Toast.makeText(context, "CSV exported to Downloads", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, it.message ?: "Export failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        composable(
            route = DiMentoRoute.GroupTimeline.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            val timelineViewModel: GroupTimelineViewModel = viewModel(
                factory = GroupTimelineViewModel.Factory(
                    groupId = groupId,
                    observeTimelineUseCase = container.observeTimelineUseCase,
                    observeGroupsUseCase = container.observeGroupsUseCase,
                    getGroupUseCase = container.getGroupUseCase,
                    createEventUseCase = container.createEventUseCase,
                    forwardEventUseCase = container.forwardEventUseCase,
                    markEventCompleteUseCase = container.markEventCompleteUseCase,
                    deleteEventUseCase = container.deleteEventUseCase,
                    eventTypeResolver = container.eventTypeResolver
                )
            )
            GroupTimelineScreen(
                viewModel = timelineViewModel,
                onBack = { navController.popBackStack() },
                onCreateInGroup = {
                    createEventSharedViewModel.setSourceGroupId(it)
                    navController.navigate(DiMentoRoute.CreateEvent.create(groupId = it))
                },
                onSearchInGroup = { navController.navigate(DiMentoRoute.Search.create(groupId = it)) }
            )
        }

        composable(
            route = DiMentoRoute.CreateEvent.route,
            arguments = listOf(navArgument("groupId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { entry ->
            val arg = entry.arguments?.getLong("groupId") ?: -1L
            val resolvedGroupId = arg.takeIf { it > 0 }
            createEventSharedViewModel.setSourceGroupId(resolvedGroupId)
            CreateEventScreen(
                viewModel = createEventSharedViewModel,
                onBack = { navController.popBackStack() },
                onNextSelectGroup = { navController.navigate(DiMentoRoute.SelectGroup.route) },
                onSaveDirectToGroup = { groupId ->
                    createEventSharedViewModel.commit(groupId) {
                        navController.popBackStack()
                    }
                }
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

private fun <T : ViewModel> simpleFactory(create: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
}
