package com.dertefter.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.notes.dto.SortOrder
import com.dertefter.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val navigator: Navigator,
    private val repository: NotesRepository
) : ViewModel() {

    private val _isDeleteMode = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _submittedQuery = MutableStateFlow("")
    private val _isSearchVisible = MutableStateFlow(false)
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)

    private val _notes = combine(_submittedQuery, _sortOrder) { query, sortOrder ->
        query to sortOrder
    }.flatMapLatest { (query, sortOrder) ->
        repository.getNotes(query, sortOrder)
    }

    val uiState = combine(_notes, _isDeleteMode, _searchQuery, _isSearchVisible, _sortOrder) { notes, isDeleteMode, searchQuery, isSearchVisible, sortOrder ->
        UiState(
            notes = notes,
            isDeleteMode = isDeleteMode,
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            sortOrder = sortOrder
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    fun onEvent(event: Event) {
        when (event) {
            Event.CreateNote -> {
                navigator.navigate(Routes.NewNote)
            }
            is Event.DeleteNote -> {
                viewModelScope.launch {
                    repository.deleteNote(event.noteId)
                }
            }
            is Event.ToggleDeleteMode -> {
                _isDeleteMode.update { event.toggleTo ?: !it }
            }
            is Event.OpenNoteDetail -> {
                navigator.navigate(Routes.NoteDetail(event.noteId))
            }
            is Event.UpdateSearchQuery -> {
                _searchQuery.value = event.query
                if (event.query.isEmpty()) {
                    _submittedQuery.value = ""
                }
            }
            Event.SubmitSearch -> {
                _submittedQuery.value = _searchQuery.value
            }
            Event.ToggleSearch -> {
                _isSearchVisible.update { !it }
                if (!_isSearchVisible.value) {
                    _searchQuery.value = ""
                    _submittedQuery.value = ""
                }
            }
            is Event.ChangeSortOrder -> {
                _sortOrder.value = event.sortOrder
            }
        }
    }
}
