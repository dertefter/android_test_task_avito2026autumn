package com.dertefter.avito2026autumn.presentation

import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor

data class ThemeState(
    val seedColor: ThemeSeedColor,
    val darkThemeStatus: DarkThemeStatus
)
