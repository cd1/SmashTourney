package com.gmail.cristiandeives.smashtourney.data

import android.util.Log
import androidx.annotation.UiThread
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@UiThread
class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    fun createTourney(tourney: Tourney): Task<DocumentReference> {
        val fsTourney = FirestoreTourney.fromTourney(tourney)

        Log.d(TAG, "adding Firestore document $tourney to collection $TOURNEY_COLLECTION")
        return db.collection(TOURNEY_COLLECTION).add(fsTourney)
    }

    fun getTourneysQuery(): Query {
        Log.d(TAG, "querying Firestore documents from collection $TOURNEY_COLLECTION")
        return db.collection(TOURNEY_COLLECTION)
            .orderBy(TOURNEY_DATE_TIME_FIELD)
    }

    companion object {
        private const val TOURNEY_COLLECTION = "tourneys"
        private const val TOURNEY_DATE_TIME_FIELD = "dateTime"
        private val TAG = FirestoreRepository::class.java.simpleName
        private var instance: FirestoreRepository? = null

        fun getInstance() = instance
            ?: FirestoreRepository().also { instance = it }
    }
}