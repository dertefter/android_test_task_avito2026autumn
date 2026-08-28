package com.dertefter.data.ai.api

import com.dertefter.data.ai.dto.BalanceResponse
import com.dertefter.data.ai.dto.ChatRequest
import com.dertefter.data.ai.dto.ChatResponse
import com.dertefter.data.ai.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @FormUrlEncoded
    @POST("api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authKey: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String
    ): TokenResponse
}

interface AiApi {
    @GET("v1/balance")
    suspend fun getBalance(
        @Header("Authorization") bearerToken: String
    ): BalanceResponse

    @POST("v1/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") bearerToken: String,
        @Body request: ChatRequest
    ): ChatResponse
}
