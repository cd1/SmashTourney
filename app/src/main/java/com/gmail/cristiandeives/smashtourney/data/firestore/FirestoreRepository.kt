package com.gmail.cristiandeives.smashtourney.data.firestore

import android.util.Log
import com.gmail.cristiandeives.smashtourney.data.Repository
import com.gmail.cristiandeives.smashtourney.data.TaskListener
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository : Repository {
    private val db = FirebaseFirestore.getInstance()

    override fun createTourney(tourney: Tourney): TaskListener {
        val fsTourney = FirestoreTourney.fromTourney(tourney)

        Log.d(TAG, "adding Firestore document $tourney to collection $TOURNEY_COLLECTION")
        val task = db.collection(TOURNEY_COLLECTION).add(fsTourney)
        return FirestoreTaskListener(task)
    }

    companion object {
        private const val TOURNEY_COLLECTION = "tourneys"
        private val TAG = FirestoreRepository::class.java.simpleName
    }
}