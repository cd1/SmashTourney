package com.gmail.cristiandeives.smashtourney.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

data class FirestoreTourney(
    @get:Exclude val id: String = "",
    val title: String = "",
    val dateTime: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now(),
    val champion: DocumentReference? = null,
    val runnerUps: List<DocumentReference> = emptyList()
) {
    fun toTourneyShallow(): Tourney {
        val javaDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(dateTime.seconds, dateTime.nanoseconds.toLong()), ZoneOffset.UTC
        )
        val javaCreatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(createdAt.seconds, createdAt.nanoseconds.toLong()), ZoneOffset.UTC
        )

        // TODO: "champion" and "runnerUps" aren't unpacked because we'd need to read data from Firestore again
        // currently, we don't need to read those values anywhere in the app, so we can live
        // with it.
        return Tourney(id, title, javaDateTime, javaCreatedAt)
    }

    companion object {
        fun fromTourneyShallow(tourney: Tourney) =
            FirestoreTourney(
                tourney.id,
                tourney.title,
                Timestamp(
                    tourney.dateTime.atZone(ZoneOffset.UTC).toEpochSecond(),
                    tourney.dateTime.nano
                ),
                Timestamp(
                    tourney.createdAt.atZone(ZoneOffset.UTC).toEpochSecond(),
                    tourney.createdAt.nano
                )
                // TODO: "champion" and "runnerUps" aren't packed because we'd need the Firestore connection object
                // currently, we don't need to write those values via "fromTourneyShallow" anywhere
                // in the app, so we can live with it.
            )
    }
}