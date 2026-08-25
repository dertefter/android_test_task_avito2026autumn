package com.dertefter.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings.presentation.SettingsScreen
import com.dertefter.settings.presentation.SettingsViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {

    val themeState by viewModel.themeState.collectAsState()
    val balanceState by viewModel.balanceState.collectAsState()

    SettingsScreen(
        themeState = themeState,
        balanceState = balanceState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )
}
