package com.gmail.cristiandeives.smashtourney.data.firestore

import com.gmail.cristiandeives.smashtourney.data.TaskListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

class FirestoreTaskListener(task: Task<DocumentReference>) : TaskListener {
    private var successCallback: () -> Unit = {}
    private var failureCallback: (Exception) -> Unit = {}
    private var completeCallback: () -> Unit = {}
    private var canceledCallback: () -> Unit = {}

    init {
        task.addOnSuccessListener {
            successCallback()
        }.addOnFailureListener { ex ->
            failureCallback(ex)
        }.addOnCompleteListener {
            completeCallback()
        }.addOnCanceledListener {
            canceledCallback()
        }
    }

    override fun onSuccess(callback: () -> Unit) = also {
        successCallback = callback
    }

    override fun onFailure(callback: (ex: Exception) -> Unit) = also {
        failureCallback = callback
    }

    override fun onComplete(callback: () -> Unit) = also {
        completeCallback = callback
    }

    override fun onCanceled(callback: () -> Unit) = also {
        canceledCallback = callback
    }
}