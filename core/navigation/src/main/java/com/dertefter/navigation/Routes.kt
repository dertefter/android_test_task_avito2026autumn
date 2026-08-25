package com.dertefter.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {

    @Serializable
    data object NotesGraph : Routes

    @Serializable
    data object TasksGraph : Routes

    @Serializable
    data object SettingsGraph : Routes

    @Serializable
    data object Notes : Routes

    @Serializable
    data object Tasks : Routes

    @Serializable
    data class NoteDetail(val noteId: Long) : Routes

    @Serializable
    data object NewNote : Routes

    @Serializable
    data object Settings : Routes

}
