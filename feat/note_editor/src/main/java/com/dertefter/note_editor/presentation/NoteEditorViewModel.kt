package com.dertefter.note_editor.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dertefter.data.settings.dto.note_editor.NoteEditorStrategy
import com.dertefter.data.settings.repository.SettingsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _noteId = MutableStateFlow<Long?>(null)
    private val _title = MutableStateFlow("")
    private val _text = MutableStateFlow("")
    private val _imagePath = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<Throwable?>(null)

    private val _originalTitle = MutableStateFlow("")
    private val _originalText = MutableStateFlow("")
    private val _originalImagePath = MutableStateFlow<String?>(null)

    val uiState = combine(
        _noteId,
        _title,
        _text,
        _imagePath,
        _isLoading,
        _error,
        settingsRepository.noteEditorStrategy,
        _originalTitle,
        _originalText,
        _originalImagePath
    ) { params ->
        val noteId = params[0] as Long?
        val title = params[1] as String
        val text = params[2] as String
        val imagePath = params[3] as String?
        val isLoading = params[4] as Boolean
        val error = params[5] as Throwable?
        val strategy = params[6] as NoteEditorStrategy
        val originalTitle = params[7] as String
        val originalText = params[8] as String
        val originalImagePath = params[9] as String?

        val hasChanged = title != originalTitle ||
                text != originalText ||
                imagePath != originalImagePath

        val titleReady = title.isNotBlank()

        UiState(
            id = noteId,
            title = title,
            text = text,
            imagePath = imagePath,
            isLoading = isLoading,
            isSaveEnabled = hasChanged && titleReady,
            error = error,
            strategy = strategy
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

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
            _isLoading.value = true
            val note = notesRepository.getNoteById(noteId).first()
            if (note != null) {
                _originalTitle.value = note.title
                _originalText.value = note.text
                _originalImagePath.value = note.imagePath
                
                _noteId.value = note.id
                _title.value = note.title
                _text.value = note.text
                _imagePath.value = note.imagePath
            }
            _isLoading.value = false
        }
    }

    fun onEvent(event: Event) {
         when (event) {

             is Event.OnSelectScreenStrategy -> {
                 viewModelScope.launch {
                     settingsRepository.setNoteEditorStrategy(event.strategy)
                 }
             }

            Event.OnBack -> {
                navigator.navigateUp()
            }

            is Event.OnTitleChanged -> {
                _title.value = event.title
            }

            is Event.OnTextChanged -> {
                _text.value = event.text
            }

            is Event.OnImageChanged -> {
                _imagePath.value = event.imagePath
            }

            Event.OnSaveNote -> {
                saveNote()
            }

            Event.OnDismissError -> {
                _error.value = null
            }

            is Event.OnSpeechRecognized -> {
                if (event.target == RecordTarget.TITLE) {
                    _title.update { (it + " " + event.text).trim() }
                } else {
                    _text.update { (it + " " + event.text).trim() }
                }
            }

            is Event.OnSpeechRecognitionError -> {
                _error.value = Exception(event.message)
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            notesRepository.saveNote(
                title = _title.value,
                text = _text.value,
                imagePath = _imagePath.value,
                noteId = _noteId.value
            ).onSuccess {
                navigator.navigateUp()
            }.onFailure { throwable ->
                _error.value = throwable
            }
        }
    }
}
