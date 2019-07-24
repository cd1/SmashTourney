package com.gmail.cristiandeives.smashtourney

enum class TaskState {
    IN_PROGRESS,
    SUCCESS,
    FAILED,
    CANCELED;

    val isComplete: Boolean
        get() = this in COMPLETE_STATES

    companion object {
        private val COMPLETE_STATES = arrayOf(SUCCESS, FAILED, CANCELED)
    }
}