package com.dertefter.tasks.dto

import java.time.LocalDateTime

data class TaskDto(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
    val date: LocalDateTime
)
