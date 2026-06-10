package com.dimento.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dimento.app.core.CsvExporter
import com.dimento.app.core.ServiceLocator
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maybeAskNotificationPermission()
        enableEdgeToEdge()
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
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(800)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        DiMentoAppContent()
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(id = R.string.splash_tagline),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DiMentoAppContent() {
    val context = LocalContext.current
    val container = remember { ServiceLocator.container }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

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
                    onSearchInGroup = { navController.navigate(DiMentoRoute.Search.create(groupId = it)) }
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

private fun <T : ViewModel> simpleFactory(create: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
}
