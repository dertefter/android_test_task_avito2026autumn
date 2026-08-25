package com.dertefter.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme
import com.dertefter.settings.presentation.component.BalanceCard
import com.dertefter.settings.presentation.component.ColorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onEvent: (Event) -> Unit,
    themeState: ThemeState,
    balanceState: BalanceState
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Настройки") },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
            ),
            modifier = Modifier.fillMaxSize()
        ) {

            item {
                BalanceCard(
                    balanceState = balanceState,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onRefresh = { onEvent(Event.RefreshBalance) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            }

            item {
                Text(
                    text = "Тема оформления",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                DarkThemeItem(
                    title = "Светлая",
                    icon = Icons.ThemeDay,
                    selected = themeState.darkThemeStatus == DarkThemeStatus.DAY,
                    onClick = { onEvent(Event.SetDarkThemeStatus(DarkThemeStatus.DAY)) }
                )
            }
            item {
                DarkThemeItem(
                    title = "Темная",
                    icon = Icons.ThemeNight,
                    selected = themeState.darkThemeStatus == DarkThemeStatus.NIGHT,
                    onClick = { onEvent(Event.SetDarkThemeStatus(DarkThemeStatus.NIGHT)) }
                )
            }
            item {
                DarkThemeItem(
                    title = "Системная",
                    icon = Icons.ThemeAuto,
                    selected = themeState.darkThemeStatus == DarkThemeStatus.AUTO,
                    onClick = { onEvent(Event.SetDarkThemeStatus(DarkThemeStatus.AUTO)) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            }

            item {
                Text(
                    text = "Цвет темы",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    themeState.availableThemeColors.forEach { color ->
                        ColorItem(
                            color = color,
                            isSelected = themeState.seedColor == color,
                            onClick = { onEvent(Event.SetThemeSeedColor(color)) }
                        )
                    }
                }
            }

            item {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    onClick = { onEvent(Event.RestoreTheme) }
                ) {
                    Text(
                        "Сбросить настройки темы"
                    )
                }
            }

        }
    }
}

@Composable
fun DarkThemeItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        trailingContent = {
            RadioButton(selected = selected, onClick = null)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}



@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TheTheme {
        SettingsScreen(
            onEvent = {},
            themeState = ThemeState(
                seedColor = ThemeSeedColor.PURPLE,
                darkThemeStatus = DarkThemeStatus.AUTO,
                availableThemeColors = listOf(
                    ThemeSeedColor.BLUE,
                    ThemeSeedColor.RED,
                    ThemeSeedColor.PURPLE,
                    ThemeSeedColor.GREEN
                )
            ),
            balanceState = BalanceState()
        )
    }
}
