package com.gmail.cristiandeives.smashtourney.data

import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot

object PlayerSnapshotParser : SnapshotParser<Player> {
    override fun parseSnapshot(snapshot: DocumentSnapshot): Player {
        val snapId = snapshot.id

        return snapshot.toObject(Player::class.java)?.copy(id = snapId)
            ?: Player()
    }
}