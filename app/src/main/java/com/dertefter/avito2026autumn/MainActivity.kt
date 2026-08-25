package com.dertefter.avito2026autumn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.dertefter.avito2026autumn.presentation.MainScreen
import com.dertefter.avito2026autumn.presentation.MainViewModel
import com.dertefter.data.settings.dto.theme.DarkThemeStatus
import com.dertefter.design.theme.TheTheme
import com.dertefter.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            val uiState by viewModel.themeState.collectAsState()

            val darkTheme = when (uiState.darkThemeStatus) {
                DarkThemeStatus.DAY -> false
                DarkThemeStatus.NIGHT -> true
                DarkThemeStatus.AUTO -> isSystemInDarkTheme()
            }

            val seedColor = Color(uiState.seedColor.hex)

            TheTheme(
                darkTheme = darkTheme,
                seedColor = seedColor
            ) {
                MainScreen(navigator)
            }
        }
    }
}




