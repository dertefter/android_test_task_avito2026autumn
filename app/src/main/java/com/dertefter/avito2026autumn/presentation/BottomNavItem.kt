package com.dertefter.avito2026autumn.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.dertefter.design.icons.Icons
import com.dertefter.navigation.Routes

sealed class BottomNavItem(
    val graphRoute: Routes,
    val label: String,
    val unselectedIcon: @Composable () -> ImageVector,
    val selectedIcon: @Composable () -> ImageVector
) {
    data object Notes : BottomNavItem(
        graphRoute = Routes.NotesGraph,
        label = "Заметки",
        unselectedIcon = { Icons.ListAlt },
        selectedIcon = { Icons.ListAltFilled }
    )

    data object Tasks : BottomNavItem(
        graphRoute = Routes.TasksGraph,
        label = "Задачи",
        unselectedIcon = { Icons.ListAlt },
        selectedIcon = { Icons.ListAltFilled }
    )

    data object Settings : BottomNavItem(
        graphRoute = Routes.SettingsGraph,
        label = "Настройки",
        unselectedIcon = { Icons.Settings },
        selectedIcon = { Icons.SettingsFilled }
    )
}