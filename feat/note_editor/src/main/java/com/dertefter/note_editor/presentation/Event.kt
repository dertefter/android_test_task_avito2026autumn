package com.dertefter.note_editor.presentation

sealed interface Event {

    data object OnBack: Event
    data class OnTitleChanged(val title: String): Event
    data class OnTextChanged(val text: String): Event
    data class OnImageChanged(val imagePath: String?): Event
    data object OnSaveNote: Event
    data object OnDismissError: Event
}
