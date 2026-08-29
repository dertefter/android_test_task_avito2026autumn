package com.dertefter.data.settings.repository

import com.dertefter.data.settings.dto.note_editor.NoteEditorStrategy
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val currentThemeSeedColor: Flow<ThemeSeedColor>

    suspend fun setThemeSeedColor(seedColor: ThemeSeedColor)

    val darkThemeStatus: Flow<DarkThemeStatus>

    suspend fun setDarkThemeStatus(darkThemeStatus: DarkThemeStatus)

    suspend fun restoreDefaultTheme()

    val availableThemeColors: List<ThemeSeedColor>

    val noteEditorStrategy: Flow<NoteEditorStrategy> // default is NoteEditorStrategy.WITHOUT_PERMISSIONS

    suspend fun setNoteEditorStrategy(strategy: NoteEditorStrategy)

}
