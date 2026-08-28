package com.dertefter.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.ai.repository.AiRepository
import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto
import com.dertefter.tasks.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
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
class TasksViewModel @Inject constructor(
    private val repository: TasksRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _submittedQuery = MutableStateFlow("")
    private val _isSearchVisible = MutableStateFlow(false)
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    private val _isCreatingTask = MutableStateFlow(false)
    private val _newTaskTitle = MutableStateFlow("")
    private val _generationStatus = MutableStateFlow<GenerationStatus?>(null)

    private var generationJob: Job? = null
    private var lastRecognizedText: String? = null

    private val _tasks = combine(_submittedQuery, _sortOrder) { query, sortOrder ->
        query to sortOrder
    }.flatMapLatest { (query, sortOrder) ->
        repository.getTasks(query, sortOrder)
    }

    val uiState = combine(
        _tasks,
        _searchQuery,
        _isSearchVisible,
        _sortOrder,
        _isCreatingTask,
        _newTaskTitle,
        _generationStatus
    ) { args ->
        UiState(
            tasks = args[0] as List<TaskDto>,
            searchQuery = args[1] as String,
            isSearchVisible = args[2] as Boolean,
            sortOrder = args[3] as SortOrder,
            isCreatingTask = args[4] as Boolean,
            newTaskTitle = args[5] as String,
            generationStatus = args[6] as GenerationStatus?
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    fun onEvent(event: Event) {
        when (event) {
            Event.CreateTask -> {
                _isCreatingTask.value = true
                _newTaskTitle.value = ""
            }
            is Event.UpdateNewTaskTitle -> {
                _newTaskTitle.value = event.title
            }
            Event.SaveNewTask -> {
                val title = _newTaskTitle.value.trim()
                if (title.isNotEmpty()) {
                    viewModelScope.launch {
                        repository.saveTask(title)
                        _isCreatingTask.value = false
                        _newTaskTitle.value = ""
                    }
                } else {
                    _isCreatingTask.value = false
                }
            }
            Event.CancelCreateTask -> {
                _isCreatingTask.value = false
                _newTaskTitle.value = ""
            }
            is Event.DeleteTask -> {
                viewModelScope.launch {
                    repository.deleteTask(event.taskId)
                }
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
            is Event.ToggleTask -> {
                viewModelScope.launch {
                    repository.toggleTaskCompletion(event.taskId)
                }
            }

            is Event.StartAiGeneration -> {
                startGeneration(event.recognizedText)
            }
            Event.RetryAiGeneration -> {
                lastRecognizedText?.let { startGeneration(it) }
            }
            Event.CancelAiGeneration -> {
                generationJob?.cancel()
                _generationStatus.value = null
            }
        }
    }

    private fun startGeneration(recognizedText: String) {
        lastRecognizedText = recognizedText
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            _generationStatus.value = GenerationStatus.LOADING
            aiRepository.generateTask(recognizedText)
                .onSuccess { generatedTitle ->
                    _generationStatus.value = null
                    _isCreatingTask.value = true
                    _newTaskTitle.value = generatedTitle
                }
                .onFailure {
                    _generationStatus.value = GenerationStatus.FAILED
                }
        }
    }
}
