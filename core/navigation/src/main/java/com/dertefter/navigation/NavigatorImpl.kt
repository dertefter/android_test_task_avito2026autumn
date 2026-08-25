package com.dertefter.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorImpl @Inject constructor() : Navigator {

    private val _navigationActions = MutableSharedFlow<NavigationAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val navigationActions = _navigationActions.asSharedFlow()

    override fun navigate(route: Routes) {
        _navigationActions.tryEmit(NavigationAction.Navigate(route))
    }

    override fun navigateUp() {
        _navigationActions.tryEmit(NavigationAction.NavigateUp)
    }

    override fun navigateAndClearBackStack(route: Routes, popupTo: Routes, inclusive: Boolean) {
        _navigationActions.tryEmit(NavigationAction.NavigateAndClearBackStack(route, popupTo, inclusive))
    }
}
