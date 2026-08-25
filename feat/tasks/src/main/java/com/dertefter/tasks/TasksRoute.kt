package com.dertefter.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.tasks.presentation.TasksScreen
import com.dertefter.tasks.presentation.TasksViewModel

@Composable
fun TasksRoute(
    viewModel: TasksViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    TasksScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}