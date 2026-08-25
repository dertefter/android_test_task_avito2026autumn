package com.dertefter.notes.dto

import java.time.LocalDateTime

data class NoteDto(
    val id: Long,
    val title: String,
    val text: String,
    val date: LocalDateTime,
    val imagePath: String? = null
)
