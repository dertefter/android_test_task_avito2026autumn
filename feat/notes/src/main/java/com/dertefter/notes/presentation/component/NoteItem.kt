package com.dertefter.notes.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme
import com.dertefter.notes.dto.NoteDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    isDeleteMode: Boolean = false,
    note: NoteDto
) {
    val formattedDate = remember(note.date) {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.getDefault())
        note.date.format(formatter)
    }
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = !isDeleteMode, onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth(),
    ) {

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .height(140.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            note.imagePath?.let { imagePath ->
                AsyncImage(
                    model = imagePath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } ?: Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )


            this@Column.AnimatedVisibility(
                visible = isDeleteMode,
                modifier = Modifier
                    .align(Alignment.TopEnd),
                content = {
                    IconButton(
                        modifier = Modifier
                            .padding(4.dp),
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Delete,
                            contentDescription = "Удалить",
                        )
                    }
                }
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                onClick = onShareClick,
                enabled = !isDeleteMode,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Share,
                    contentDescription = "Поделиться"
                )
            }

        }


        Column (
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }


    }
}

@Preview
@Composable
fun NoteDtoPrev() {
    TheTheme {


        val note = NoteDto(
            id = 2,
            title = "Ляляля",
            text = "Ляляля",
            date = LocalDateTime.now(),
            imagePath = null,
        )

        NoteItem(note = note, isDeleteMode = true)

    }
}