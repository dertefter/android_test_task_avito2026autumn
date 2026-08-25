package com.dertefter.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.ai.repository.AiRepository
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.data.settings.dto.theme.ThemeSeedColor
import com.dertefter.data.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    val themeState: StateFlow<ThemeState> = combine(
        settingsRepository.currentThemeSeedColor,
        settingsRepository.darkThemeStatus
    ) { seedColor, darkThemeStatus ->
        ThemeState(seedColor, darkThemeStatus, settingsRepository.availableThemeColors)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState(ThemeSeedColor.PURPLE, DarkThemeStatus.AUTO, emptyList())
    )

    private val _balanceLoadingState = MutableStateFlow(false)
    private val _balanceErrorState = MutableStateFlow(false)

    val balanceState: StateFlow<BalanceState> = combine(
        aiRepository.balance,
        _balanceLoadingState,
        _balanceErrorState
    ) { balance, isLoading, isError ->
        BalanceState(balance, isLoading, isError)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BalanceState()
    )

    init {
        refreshBalance()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.SetDarkThemeStatus -> {
                viewModelScope.launch {
                    settingsRepository.setDarkThemeStatus(event.darkThemeStatus)
                }
            }
            is Event.SetThemeSeedColor -> {
                viewModelScope.launch {
                    settingsRepository.setThemeSeedColor(event.themeSeedColor)
                }
            }
            is Event.RefreshBalance -> {
                refreshBalance()
            }

            is Event.RestoreTheme -> {
                viewModelScope.launch {
                    settingsRepository.restoreDefaultTheme()
                }
            }


        }
    }

    private fun refreshBalance() {
        viewModelScope.launch {
            _balanceLoadingState.value = true
            _balanceErrorState.value = false
            aiRepository.updateBalance()
                .onFailure {
                    _balanceErrorState.value = true
                }
            _balanceLoadingState.value = false
        }
    }
}
