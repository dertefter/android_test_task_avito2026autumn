package com.dertefter.note_editor.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var originalTitle: String = ""
    private var originalText: String = ""
    private var originalImagePath: String? = null

    init {
        val noteId = try {
            savedStateHandle.toRoute<Routes.NoteDetail>().noteId
        } catch (_: Exception) {
            null
        }

        if (noteId != null) {
            loadNote(noteId)
        }
    }

    private fun loadNote(noteId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = notesRepository.getNoteById(noteId).first()
            if (note != null) {
                originalTitle = note.title
                originalText = note.text
                originalImagePath = note.imagePath
                _uiState.update {
                    it.copy(
                        id = note.id,
                        title = note.title,
                        text = note.text,
                        imagePath = note.imagePath,
                        isLoading = false,
                        isSaveEnabled = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateSaveEnabled() {
        _uiState.update { state ->
            val hasChanged = state.title != originalTitle ||
                    state.text != originalText ||
                    state.imagePath != originalImagePath

            val titleReady = state.title.isNotBlank()

            state.copy(isSaveEnabled = (hasChanged && titleReady) )
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnBack -> {
                navigator.navigateUp()
            }

            is Event.OnTitleChanged -> {
                _uiState.update {
                    it.copy(
                        title = event.title
                    )
                }
                updateSaveEnabled()
            }

            is Event.OnTextChanged -> {
                _uiState.update {
                    it.copy(text = event.text)
                }
                updateSaveEnabled()
            }

            is Event.OnImageChanged -> {
                _uiState.update {
                    it.copy(imagePath = event.imagePath)
                }
                updateSaveEnabled()
            }

            Event.OnSaveNote -> {
                saveNote()
            }

            Event.OnDismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun saveNote() {
        val state = _uiState.value

        viewModelScope.launch {
            notesRepository.saveNote(
                title = state.title,
                text = state.text,
                imagePath = state.imagePath,
                noteId = state.id
            ).onSuccess {
                navigator.navigateUp()
            }.onFailure { throwable ->
                _uiState.update { it.copy(error = throwable) }
            }
        }
    }
}
