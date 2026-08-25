package com.dertefter.tasks.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dertefter.design.components.search.TheSearchBar
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme
import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto
import com.dertefter.tasks.presentation.component.TaskInputItem
import com.dertefter.tasks.presentation.component.TaskItem
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState
) {

    var showSortMenu by remember { mutableStateOf(false) }
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)

    val searchBarElevation = topBarState.overlappedFraction * 12.dp

    Scaffold(
        topBar = {

            Column{
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text("Задачи")
                    },
                    actions = {

                        if (uiState.tasks.isNotEmpty()){
                            Box {
                                IconButton(onClick = { showSortMenu = true }) {
                                    Icon(
                                        imageVector = if (uiState.sortOrder == SortOrder.NEWEST_FIRST) Icons.SortDown else Icons.SortUp,
                                        contentDescription = "Сортировка"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("От новых к старым") },
                                        onClick = {
                                            onEvent(Event.ChangeSortOrder(SortOrder.NEWEST_FIRST))
                                            showSortMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("От старых к новым") },
                                        onClick = {
                                            onEvent(Event.ChangeSortOrder(SortOrder.OLDEST_FIRST))
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }


                    }
                )

                if (uiState.isCreatingTask) {
                    TaskInputItem(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        title = uiState.newTaskTitle,
                        onTitleChange = { onEvent(Event.UpdateNewTaskTitle(it)) },
                        onSave = { onEvent(Event.SaveNewTask) },
                        onCancel = { onEvent(Event.CancelCreateTask) },
                    )
                } else {
                    TheSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { onEvent(Event.UpdateSearchQuery(it)) },
                        onSearch = { onEvent(Event.SubmitSearch) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        elevation = searchBarElevation
                    )
                }



            }


        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isCreatingTask
            ) {
                FloatingActionButton(
                    onClick = {
                        onEvent(Event.CreateTask)
                    }
                ) {
                    Icon(
                        Icons.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { contentPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {

            items(
                items = uiState.tasks,
                key = { it.id }
            ){ task ->
                TaskItem(
                    modifier = Modifier.animateItem(),
                    task = task,
                    onClick = {
                        onEvent(
                            Event.ToggleTask(task.id)
                        )
                    },
                    onToggleClick = {
                        onEvent(
                            Event.ToggleTask(task.id)
                        )
                    }
                )
            }
        }

        if (uiState.tasks.isEmpty() && !uiState.isCreatingTask){
            Box(
                modifier = Modifier.padding(contentPadding).padding(horizontal = 12.dp).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    "Список пуст"
                )
            }

        }

    }
}

@Preview
@Composable
fun TasksScreenPreview() {
    TheTheme {

        val task = TaskDto(
            id = 1,
            title = "Задача 1",
            isCompleted = false,
            date = LocalDateTime.now(),
        )

        val tasks = listOf(task, task.copy(id = 2, title = "Задача 2", isCompleted = true))


        TasksScreen(
            {},
            uiState = UiState(
                tasks = tasks,
                sortOrder = SortOrder.NEWEST_FIRST
            )
            )

    }
}
