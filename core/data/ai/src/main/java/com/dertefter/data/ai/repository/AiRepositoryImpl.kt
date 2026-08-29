package com.dertefter.data.ai.repository

import com.dertefter.data.ai.api.AiApi
import com.dertefter.data.ai.api.AuthApi
import com.dertefter.data.ai.datastore.AiLocalDataSource
import com.dertefter.data.ai.dto.ChatRequest
import com.dertefter.data.ai.dto.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val aiApi: AiApi,
    private val localDataSource: AiLocalDataSource
) : AiRepository {

    private val authKey = "MDFhMDNlN2EtOGU5ZS03NmQ4LWI4OWMtZjM5ZDhjMDdhNjNjOjk0YjU1YzZlLWE0ODMtNDc1Yy04ZDc4LWZiNDkzODY4ODM1Nw=="

    override val balance: Flow<Int?> = localDataSource.balance

    override suspend fun updateBalance(): Result<Int> = runCatching {
        val token = getValidToken()
        val response = aiApi.getBalance("Bearer $token")
        val gigaChatBalance = response.balance.find { it.usage == "GigaChat" }?.value ?: 0
        localDataSource.saveBalance(gigaChatBalance)
        gigaChatBalance
    }

    override suspend fun generateTask(input: String): Result<String> = runCatching {
        val requestText = """
            Ты должен составить короткий текст задачи на основе того, что сказал пользователь.
            Например, пользователь говорит: "создай задачу покормить кота", а ты пишешь - "Покормить кота"
        """.trimIndent()

        val token = getValidToken()
        val request = ChatRequest(
            model = "GigaChat-2-Max",
            messages = listOf(
                Message(role = "system", content = requestText),
                Message(role = "user", content = input)
            )
        )
        val response = aiApi.chatCompletions("Bearer $token", request)
        response.choices.firstOrNull()?.message?.content ?: throw Exception("Empty response from AI")
    }

    private suspend fun getValidToken(): String {
        val currentToken = localDataSource.accessToken.first()
        val expiresAt = localDataSource.expiresAt.first() ?: 0L
        
        if (currentToken != null && System.currentTimeMillis() < expiresAt) {
            return currentToken
        }

        val response = authApi.getToken(
            authKey = "Basic $authKey",
            rqUid = UUID.randomUUID().toString(),
            scope = "GIGACHAT_API_PERS"
        )
        
        localDataSource.saveToken(response.accessToken, response.expiresAt)
        return response.accessToken
    }
}
