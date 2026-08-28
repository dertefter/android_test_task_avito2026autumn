package com.dertefter.data.ai.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<Message>,
    @SerialName("stream") val stream: Boolean = false,
    @SerialName("update_interval") val updateInterval: Int = 0
)

@Serializable
data class Message(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String
)
