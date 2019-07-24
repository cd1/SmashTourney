package com.gmail.cristiandeives.smashtourney.data

import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot

class TourneySnapshotParser : SnapshotParser<Tourney> {
    override fun parseSnapshot(snapshot: DocumentSnapshot): Tourney {
        val fsTourney = snapshot.toObject(FirestoreTourney::class.java)

        return fsTourney?.toTourney() ?: Tourney()
    }
}