package com.gmail.cristiandeives.smashtourney.data

interface TaskListener {
    fun onSuccess(callback: () -> Unit): TaskListener
    fun onFailure(callback: (ex: Exception) -> Unit): TaskListener
    fun onComplete(callback: () -> Unit): TaskListener
    fun onCanceled(callback: () -> Unit): TaskListener
}