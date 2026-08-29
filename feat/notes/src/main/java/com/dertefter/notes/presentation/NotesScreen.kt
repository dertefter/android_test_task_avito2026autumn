package com.dertefter.notes.presentation

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.dertefter.notes.dto.NoteDto
import com.dertefter.notes.dto.SortOrder
import com.dertefter.notes.presentation.component.NoteItem
import java.time.LocalDateTime
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.notes) {
        if (uiState.notes.isEmpty()){
            onEvent(Event.ToggleDeleteMode(false))
        }
    }

    val topBarState = rememberTopAppBarState()

    var showSortMenu by remember { mutableStateOf(false) }
    var noteIdToDelete by remember { mutableStateOf<Long?>(null) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)


    val searchBarElevation = topBarState.overlappedFraction * 12.dp

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column() {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text("Заметки")
                    },
                    actions = {

                        if (uiState.notes.isNotEmpty() || uiState.isDeleteMode) {
                            IconButton(
                                onClick = {
                                    onEvent(Event.ToggleDeleteMode())
                                }
                            ) {
                                Icon(
                                    imageVector = if (uiState.isDeleteMode) Icons.DeleteFilled else Icons.Delete,
                                    contentDescription = "Режим удаления",
                                    tint = if (uiState.isDeleteMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (uiState.notes.isNotEmpty()){
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

                TheSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { onEvent(Event.UpdateSearchQuery(it)) },
                    onSearch = { onEvent(Event.SubmitSearch) },
                    elevation = searchBarElevation,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )
            }



        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(Event.CreateNote)
                }
            ) {
                Icon(
                    Icons.Add,
                    contentDescription = null
                )
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
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = uiState.notes,
                key = { it.id }
            ){ note ->
                NoteItem(
                    modifier = Modifier.animateItem(),
                    note = note,
                    isDeleteMode = uiState.isDeleteMode,
                    onDeleteClick = {
                        noteIdToDelete = note.id
                    },
                    onClick = {
                        onEvent(
                            Event.OpenNoteDetail(note.id)
                        )
                    },
                    onShareClick = {
                        shareNote(context, note.title, note.text)
                    }
                )
            }
        }

        if (uiState.notes.isEmpty()){
            Box(
                modifier = Modifier.padding(contentPadding).padding(horizontal = 12.dp).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    "Список пуст"
                )
            }

        }

        if (noteIdToDelete != null) {
            AlertDialog(
                onDismissRequest = { noteIdToDelete = null },
                title = { Text("Точно удалить?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            noteIdToDelete?.let { onEvent(Event.DeleteNote(it)) }
                            noteIdToDelete = null
                        }
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteIdToDelete = null }) {
                        Text("Нет")
                    }
                }
            )
        }

    }
}

@Preview
@Composable
fun NotesScreenPreview() {
    TheTheme {

        val note = NoteDto(
            id = 2,
            title = "Ляляля",
            text = "Ляляля",
            date = LocalDateTime.now(),
            imagePath = null,
        )

        val notes = listOf(note, note, note)


        NotesScreen(
            {},
            uiState = UiState(
                notes = notes,
                sortOrder = SortOrder.NEWEST_FIRST
            )
            )

    }
}

private fun shareNote(context: Context, title: String, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$text")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
