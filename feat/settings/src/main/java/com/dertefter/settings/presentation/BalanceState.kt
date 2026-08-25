package com.dertefter.settings.presentation

data class BalanceState(
    val balance: Int? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)