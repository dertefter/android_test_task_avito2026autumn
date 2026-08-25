package com.dertefter.notes.errors

sealed class NoteError : Throwable() {
    class BlankTitle : NoteError()
}
