package com.dertefter.tasks.errors

sealed class TaskError : Throwable() {
    class BlankTitle : TaskError()
}
