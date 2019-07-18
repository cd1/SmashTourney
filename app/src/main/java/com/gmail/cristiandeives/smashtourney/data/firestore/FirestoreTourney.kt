package com.gmail.cristiandeives.smashtourney.data.firestore

import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.google.firebase.Timestamp
import org.threeten.bp.ZoneId

class FirestoreTourney(
    val title: String,
    val dateTime: Timestamp,
    val createdAt: Timestamp
) {
    companion object {
        fun fromTourney(tourney: Tourney) =
            FirestoreTourney(
                tourney.title,
                Timestamp(
                    tourney.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                    tourney.dateTime.nano
                ),
                Timestamp(
                    tourney.createdAt.atZone(ZoneId.systemDefault()).toEpochSecond(),
                    tourney.createdAt.nano
                )
            )
    }
}