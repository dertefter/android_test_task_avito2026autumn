package com.dertefter.data.ai.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatResponse(
    @SerialName("choices") val choices: List<Choice>,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("usage") val usage: Usage,
    @SerialName("object") val obj: String
)

@Serializable
data class Choice(
    @SerialName("message") val message: Message,
    @SerialName("index") val index: Int,
    @SerialName("finish_reason") val finishReason: String
)

@Serializable
data class Usage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int
)
