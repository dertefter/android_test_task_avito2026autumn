package com.dertefter.tasks.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme
import com.dertefter.tasks.presentation.GenerationStatus

@Composable
fun TaskGenerationStatusCard(
    modifier: Modifier = Modifier,
    onRe: () -> Unit = {},
    onCancel: () -> Unit = {},
    status: GenerationStatus
) {

    val containerColor by animateColorAsState(
        when (status){
            GenerationStatus.LOADING -> MaterialTheme.colorScheme.secondary
            GenerationStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
        }
    )


    val contentColor by animateColorAsState(
        when (status){
            GenerationStatus.LOADING -> MaterialTheme.colorScheme.onSecondary
            GenerationStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
        }
    )

    val title = when (status){
        GenerationStatus.LOADING -> "Генерация задачи..."
        GenerationStatus.FAILED -> "Ошибка"
    }

    val subtitle = when (status){
        GenerationStatus.LOADING -> "ИИ составит текст задачи"
        GenerationStatus.FAILED -> "Нажмите, чтобы повторить попытку"
    }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(
                enabled = status == GenerationStatus.FAILED,
                onClick = onRe
            )
            .background(containerColor)
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AnimatedVisibility(
            visible =  status == GenerationStatus.LOADING,
        ) {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }


        Column(
            Modifier.weight(1f),
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f)
            )
        }


        Icon(
            Icons.Close,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.clickable( onClick = onCancel)
        )

    }
}

@Preview
@Composable
fun TaskGenerationStatusCardPreview() {
    TheTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TaskGenerationStatusCard(status = GenerationStatus.LOADING)
            Spacer(modifier = Modifier.height(16.dp))
            TaskGenerationStatusCard(status = GenerationStatus.FAILED)
        }
    }
}
