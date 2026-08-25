package com.dertefter.settings.presentation

import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor

sealed interface Event {

    data class SetDarkThemeStatus(val darkThemeStatus: DarkThemeStatus) : Event

    data class SetThemeSeedColor(val themeSeedColor: ThemeSeedColor) : Event

    data object RefreshBalance : Event

    data object RestoreTheme : Event

}
