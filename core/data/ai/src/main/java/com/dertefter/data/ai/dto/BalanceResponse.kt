package com.dertefter.data.ai.dto

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val balance: List<BalanceItem>
)

@Serializable
data class BalanceItem(
    val usage: String,
    val value: Int
)
