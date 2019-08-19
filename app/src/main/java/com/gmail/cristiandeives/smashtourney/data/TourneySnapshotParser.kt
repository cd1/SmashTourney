package com.gmail.cristiandeives.smashtourney.data

import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot

object TourneySnapshotParser : SnapshotParser<Tourney> {
    override fun parseSnapshot(snapshot: DocumentSnapshot): Tourney {
        val snapId = snapshot.id

        return snapshot.toObject(FirestoreTourney::class.java)
            ?.copy(id = snapId)
            ?.toTourneyShallow() ?: Tourney(snapId)
    }
}