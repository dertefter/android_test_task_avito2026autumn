package com.dertefter.avito2026autumn.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.dertefter.navigation.NavigationAction
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.note_editor.NoteEditorRoute
import com.dertefter.notes.NotesRoute
import com.dertefter.settings.SettingsRoute
import com.dertefter.tasks.TasksRoute
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(navigator: Navigator) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        navigator.navigationActions.collectLatest { action ->
            when (action) {
                is NavigationAction.Navigate -> {
                    navController.navigate(action.route) {
                        launchSingleTop = true
                    }
                }
                is NavigationAction.NavigateUp -> {
                    navController.navigateUp()
                }
                is NavigationAction.NavigateAndClearBackStack -> {
                    navController.navigate(action.route) {
                        popUpTo(action.popupTo) {
                            inclusive = action.inclusive
                        }
                    }
                }
            }
        }
    }

    val items = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Tasks,
        BottomNavItem.Settings
    )

    fun Modifier.positionAwareImePadding() = composed {
        var consumePadding by remember { mutableIntStateOf(0) }
        onGloballyPositioned { coordinates ->
            val rootCoordinate = coordinates.findRootCoordinates()
            val bottom = coordinates.positionInWindow().y + coordinates.size.height

            consumePadding = (rootCoordinate.size.height - bottom).toInt()
        }
            .consumeWindowInsets(PaddingValues(bottom = (consumePadding / LocalDensity.current.density).dp))
            .imePadding()
    }

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.NotesGraph,
            modifier = Modifier
                .positionAwareImePadding()
                .consumeWindowInsets(WindowInsets.navigationBars)
                .weight(1f)
        ) {
            navigation<Routes.NotesGraph>(startDestination = Routes.Notes) {
                composable<Routes.Notes> {
                    NotesRoute()
                }

                composable<Routes.NewNote> {
                    NoteEditorRoute()
                }

                composable<Routes.NoteDetail> {
                    NoteEditorRoute()
                }
            }
            navigation<Routes.SettingsGraph>(startDestination = Routes.Settings) {
                composable<Routes.Settings> {
                    SettingsRoute()
                }
            }

            navigation<Routes.TasksGraph>(startDestination = Routes.Tasks) {
                composable<Routes.Tasks> {
                    TasksRoute()
                }
            }
        }

        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.graphRoute::class) } == true
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon() else item.unselectedIcon(),
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.graphRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}