package com.gmail.cristiandeives.smashtourney.data

import android.util.Log
import androidx.annotation.UiThread
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

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

    fun loadTourney(id: String): DocumentReference {
        Log.d(TAG, "querying Firestore document with id=$id from collection $TOURNEY_COLLECTION")
        return db.collection(TOURNEY_COLLECTION)
            .document(id)
    }

    fun getPlayersQuery(tourneyId: String): Query {
        Log.d(TAG, "querying Firestore documents from collection $TOURNEY_COLLECTION/$tourneyId/$TOURNEY_PLAYERS_COLLECTION")
        return db.collection(TOURNEY_COLLECTION)
            .document(tourneyId).collection(TOURNEY_PLAYERS_COLLECTION)
            .orderBy(TOURNEY_PLAYERS_NICKNAME_FIELD)
    }

    fun checkPlayerNicknameExists(tourneyId: String, nickname: String): Task<QuerySnapshot> {
        Log.d(TAG, "querying Firestore documents with nickname = $nickname from collection $TOURNEY_COLLECTION/$tourneyId/$TOURNEY_PLAYERS_COLLECTION")
        return db.collection(TOURNEY_COLLECTION).document(tourneyId)
            .collection(TOURNEY_PLAYERS_COLLECTION)
            .whereEqualTo(TOURNEY_PLAYERS_NICKNAME_FIELD, nickname).get()
    }

    fun addPlayerToTourney(tourneyId: String, player: Player): Task<DocumentReference> {
        Log.d(TAG, "adding document $player to collection $tourneyId/$TOURNEY_PLAYERS_COLLECTION")
        return db.collection(TOURNEY_COLLECTION).document(tourneyId)
            .collection(TOURNEY_PLAYERS_COLLECTION).add(player)
    }

    companion object {
        private const val TOURNEY_COLLECTION = "tourneys"
        private const val TOURNEY_DATE_TIME_FIELD = "dateTime"
        private const val TOURNEY_PLAYERS_COLLECTION = "players"
        private const val TOURNEY_PLAYERS_NICKNAME_FIELD = "nickname"

        private val TAG = FirestoreRepository::class.java.simpleName
        private var instance: FirestoreRepository? = null

        fun getInstance() = instance
            ?: FirestoreRepository().also { instance = it }
    }
}