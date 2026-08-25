package com.dertefter.data.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME_SEED_COLOR = stringPreferencesKey("theme_seed_color")
        val DARK_THEME_STATUS = stringPreferencesKey("dark_theme_status")
    }

    override val currentThemeSeedColor: Flow<ThemeSeedColor> = dataStore.data
        .map { preferences ->
            val colorName = preferences[PreferencesKeys.THEME_SEED_COLOR] ?: ThemeSeedColor.PURPLE.name
            try {
                ThemeSeedColor.valueOf(colorName)
            } catch (_: Exception) {
                ThemeSeedColor.PURPLE
            }
        }

    override suspend fun setThemeSeedColor(seedColor: ThemeSeedColor) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_SEED_COLOR] = seedColor.name
        }
    }

    override val darkThemeStatus: Flow<DarkThemeStatus> = dataStore.data
        .map { preferences ->
            val statusName = preferences[PreferencesKeys.DARK_THEME_STATUS] ?: DarkThemeStatus.AUTO.name
            try {
                DarkThemeStatus.valueOf(statusName)
            } catch (_: Exception) {
                DarkThemeStatus.AUTO
            }
        }

    override suspend fun setDarkThemeStatus(darkThemeStatus: DarkThemeStatus) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_STATUS] = darkThemeStatus.name
        }
    }

    override suspend fun restoreDefaultTheme() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_SEED_COLOR] = ThemeSeedColor.PURPLE.name
            preferences[PreferencesKeys.DARK_THEME_STATUS] = DarkThemeStatus.AUTO.name
        }
    }

    override val availableThemeColors: List<ThemeSeedColor> = ThemeSeedColor.entries
}
