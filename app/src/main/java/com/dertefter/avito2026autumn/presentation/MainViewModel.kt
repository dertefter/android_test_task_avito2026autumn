package com.dertefter.avito2026autumn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import com.dertefter.data.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: SettingsRepository
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = combine(
        repository.currentThemeSeedColor,
        repository.darkThemeStatus
    ) { seedColor, darkThemeStatus ->
        ThemeState(seedColor, darkThemeStatus)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState(ThemeSeedColor.PURPLE, DarkThemeStatus.AUTO)
    )
}
