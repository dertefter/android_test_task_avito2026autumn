package com.dertefter.data.ai.repository

import kotlinx.coroutines.flow.Flow

interface AiRepository {

    val balance: Flow<Int?>

    suspend fun updateBalance(): Result<Int>

}
