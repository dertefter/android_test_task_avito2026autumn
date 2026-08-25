package com.dertefter.data.ai.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dertefter.data.ai.di.AiApiDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AiLocalDataSource @Inject constructor(
    @AiApiDataStore private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("ai_access_token")
        val EXPIRES_AT = longPreferencesKey("ai_expires_at")
        val BALANCE = intPreferencesKey("ai_balance")
    }

    val accessToken: Flow<String?> = dataStore.data.map { it[PreferencesKeys.ACCESS_TOKEN] }
    val expiresAt: Flow<Long?> = dataStore.data.map { it[PreferencesKeys.EXPIRES_AT] }
    val balance: Flow<Int?> = dataStore.data.map { it[PreferencesKeys.BALANCE] }

    suspend fun saveToken(token: String, expiresAt: Long) {
        dataStore.edit {
            it[PreferencesKeys.ACCESS_TOKEN] = token
            it[PreferencesKeys.EXPIRES_AT] = expiresAt
        }
    }

    suspend fun saveBalance(balance: Int) {
        dataStore.edit {
            it[PreferencesKeys.BALANCE] = balance
        }
    }
}
