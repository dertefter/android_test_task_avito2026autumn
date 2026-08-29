package com.dertefter.note_editor.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.dertefter.data.settings.dto.note_editor.NoteEditorStrategy
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

    val permissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    var permissionsGranted by remember {
        mutableStateOf(checkGalleryPermissions(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        permissionsGranted = checkGalleryPermissions(context)
        if (!permissionsGranted) {
            val activity = context as? Activity
            val stillShouldShowRationale = permissionsToRequest.any {
                activity?.let { act -> ActivityCompat.shouldShowRequestPermissionRationale(act, it) } ?: false
            }
            if (!stillShouldShowRationale) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
    }

    var showGallerySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showGallerySheet) {
        ModalBottomSheet(
            onDismissRequest = { showGallerySheet = false },
            sheetState = sheetState
        ) {
            GalleryBottomSheetContent(
                permissionsGranted = permissionsGranted,
                onImageSelected = { uri ->
                    onEvent(Event.OnImageChanged(uri.toString()))
                    showGallerySheet = false
                },
                onGrantPermissions = {
                    permissionLauncher.launch(permissionsToRequest)
                },
            )
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
                },
                actions = {
                    IconButton(onClick = { shareNote(context, uiState.title, uiState.text) }) {
                        Icon(
                            imageVector = AppIcons.Share,
                            contentDescription = stringResource(R.string.note_editor_share_button_description)
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
                color = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Стратегия работы экрана")

                    Row() {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onEvent(Event.OnSelectScreenStrategy(NoteEditorStrategy.WITH_PERMISSIONS)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.strategy == NoteEditorStrategy.WITH_PERMISSIONS) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ){
                            Text("С разрешениями")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onEvent(Event.OnSelectScreenStrategy(NoteEditorStrategy.WITHOUT_PERMISSIONS)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.strategy == NoteEditorStrategy.WITHOUT_PERMISSIONS) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ){
                            Text("Без разрешений")
                        }
                    }

                }
            }


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
                                if (uiState.strategy == NoteEditorStrategy.WITH_PERMISSIONS) {
                                    showGallerySheet = true
                                    permissionsGranted = checkGalleryPermissions(context)
                                } else {
                                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
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

private fun shareNote(context: Context, title: String, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$text")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
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
