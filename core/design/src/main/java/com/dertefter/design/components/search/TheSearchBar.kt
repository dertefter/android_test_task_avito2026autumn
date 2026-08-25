package com.dertefter.design.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme

@Composable
fun TheSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск...",
    elevation: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = CircleShape,
            )
            .padding(elevation)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        )
        FilledIconButton(
            onClick = onSearch,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Search,
                contentDescription = "Поиск"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TheSearchBarPreview() {
    TheTheme {
        TheSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}
