package com.dertefter.note_editor.presentation

sealed interface Event {

    data object OnBack: Event
    data class OnTitleChanged(val title: String): Event
    data class OnTextChanged(val text: String): Event
    data class OnImageChanged(val imagePath: String?): Event
    data object OnSaveNote: Event
    data object OnDismissError: Event

    data class OnSpeechRecognized(val text: String, val target: RecordTarget): Event
    data class OnSpeechRecognitionError(val message: String): Event

}

enum class RecordTarget {
    TITLE,
    TEXT
}
