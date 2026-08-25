package com.dertefter.settings.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.TheTheme

@Composable
fun ColorItem(
    color: ThemeSeedColor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displayColor = Color(color.hex)

    val border by animateDpAsState(
        if (isSelected) { 4.dp } else 0.dp
    )
    val borderPadding by animateDpAsState(
        if (isSelected) { 8.dp } else 0.dp
    )



    Box(
        modifier = Modifier
            .size(52.dp)
            .then(
                if (isSelected) {
                    Modifier
                        .border(border, displayColor, CircleShape)
                        .padding(borderPadding)

                } else Modifier
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(displayColor),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSelected
        ) {
            Icon(
                Icons.Palette,
                tint = Color.White,
                contentDescription = null
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ColorItemPreview() {
    TheTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorItem(
                color = ThemeSeedColor.BLUE,
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorItemSelectedPreview() {
    TheTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColorItem(
                color = ThemeSeedColor.BLUE,
                isSelected = true,
                onClick = {}
            )
        }
    }
}
