package com.dertefter.tasks.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.theme.TheTheme
import com.dertefter.tasks.dto.TaskDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onToggleClick: (Boolean) -> Unit = {},
    task: TaskDto
) {
    val formattedDate = remember(task.date) {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.getDefault())
        task.date.format(formatter)
    }

    val containerColor by animateColorAsState(
        if (!task.isCompleted){
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    )

    val itemAlpha by animateFloatAsState(
        if (!task.isCompleted) 1f else 0.7f
    )

    val textColor by animateColorAsState(
        if (!task.isCompleted){
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .alpha(itemAlpha)
            .clickable(onClick = onClick)
            .background(containerColor)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onToggleClick,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = textColor
            )

            Text(
                text = if (task.isCompleted) "Выполнена" else "Не выполнена",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary
            )


            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
        }
    }
}

@Preview
@Composable
fun TaskItemPreview() {
    TheTheme {
        val tasks = listOf(
            TaskDto(
                id = 1,
                title = "Пример задачи",
                isCompleted = false,
                date = LocalDateTime.now()
            ),
            TaskDto(
                id = 1,
                title = "Пример задачи",
                isCompleted = false,
                date = LocalDateTime.now()
            ),
            TaskDto(
                id = 1,
                title = "Пример задачи",
                isCompleted = true,
                date = LocalDateTime.now()
            )
        )

        Column() {
            for (task in tasks){
                TaskItem(task = task)
            }
        }
    }
}
