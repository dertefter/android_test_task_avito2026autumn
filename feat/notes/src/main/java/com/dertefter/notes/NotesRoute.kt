package com.dertefter.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.notes.presentation.NotesScreen
import com.dertefter.notes.presentation.NotesViewModel

@Composable
fun NotesRoute(
    viewModel: NotesViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsState()

    NotesScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}