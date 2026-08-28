package com.dertefter.note_editor.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.dertefter.design.theme.TheTheme
import com.dertefter.note_editor.R
import com.dertefter.notes.errors.NoteError
import java.io.File
import com.dertefter.design.icons.Icons as AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit
) {

    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var currentSpeechTarget by remember { mutableStateOf<RecordTarget?>(null) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val text = data?.get(0)
                if (text != null && currentSpeechTarget != null) {
                    onEvent(Event.OnSpeechRecognized(text, currentSpeechTarget!!))
                }
            } else if (result.resultCode != Activity.RESULT_CANCELED) {
                onEvent(Event.OnSpeechRecognitionError("Speech recognition failed"))
            }
            currentSpeechTarget = null
        }
    )

    fun startSpeechRecognition(target: RecordTarget) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        try {
            currentSpeechTarget = target
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            onEvent(Event.OnSpeechRecognitionError("Could not start speech recognition"))
            currentSpeechTarget = null
        }
    }

    val blankTitleError = stringResource(R.string.note_editor_error_blank_title)
    val unknownError = stringResource(R.string.note_editor_error_unknown)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            val errorText = when (uiState.error) {
                is NoteError.BlankTitle -> blankTitleError
                else -> uiState.error.message ?: unknownError
            }
            snackbarHostState.showSnackbar(
                message = errorText
            )
            onEvent(Event.OnDismissError)
        }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempUri?.let { uri ->
                onEvent(Event.OnImageChanged(uri.toString()))
            }
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onEvent(Event.OnImageChanged(uri.toString()))
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(if (uiState.id == null) stringResource(R.string.note_editor_title_new) else stringResource(R.string.note_editor_title_edit))
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(Event.OnBack) }) {
                        Icon(
                            imageVector = AppIcons.ArrowBack,
                            contentDescription = stringResource(R.string.note_editor_back_button_description)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.isSaveEnabled,
            ) {
                FloatingActionButton(onClick = { onEvent(Event.OnSaveNote) }) {
                    Icon(imageVector = AppIcons.Save, contentDescription = stringResource(R.string.note_editor_save_button_description))
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            ) {
                uiState.imagePath?.let { imagePath ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        FilledIconButton(
                            onClick = { onEvent(Event.OnImageChanged(null)) },
                            modifier = Modifier.align(Alignment.TopEnd),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                        ) {
                            Icon(
                                imageVector = AppIcons.Delete,
                                contentDescription = stringResource(R.string.note_editor_delete_image_description),
                            )
                        }
                    }
                } ?: Box(
                    modifier = Modifier.padding(8.dp)
                ){
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = AppIcons.AddPhotoAlternate,
                            contentDescription = null
                        )
                        Text(stringResource(R.string.note_editor_image_missing))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        TextButton(
                            onClick = {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ) {
                            Text(stringResource(R.string.note_editor_add_from_gallery))
                        }

                        TextButton(
                            onClick = {
                                val uri = getTmpFileUri(context)
                                tempUri = uri
                                takePicture.launch(uri)
                            }
                        ) {
                            Text(stringResource(R.string.note_editor_take_photo))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { onEvent(Event.OnTitleChanged(it)) },
                label = { Text(stringResource(R.string.note_editor_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { startSpeechRecognition(RecordTarget.TITLE) }) {
                        Icon(
                            imageVector = AppIcons.Mic,
                            contentDescription = "Speech recognition"
                        )
                    }
                }
            )

            OutlinedTextField(
                value = uiState.text,
                onValueChange = { onEvent(Event.OnTextChanged(it)) },
                label = { Text(stringResource(R.string.note_editor_text_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { startSpeechRecognition(RecordTarget.TEXT) }) {
                        Icon(
                            imageVector = AppIcons.Mic,
                            contentDescription = "Speech recognition"
                        )
                    }
                }
            )
        }
    }
}

private fun getTmpFileUri(context: Context): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tmpFile)
}

@Preview
@Composable
fun NoteEditorScreenPreview() {
    TheTheme {
        NoteEditorScreen(
            uiState = UiState(
                title = "Title",
                text = "Text",
                isSaveEnabled = true
            ),
            onEvent = {}
        )
    }
}
