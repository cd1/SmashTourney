package com.gmail.cristiandeives.smashtourney.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

data class FirestoreTourney(
    @get:Exclude val id: String = "",
    val title: String = "",
    val dateTime: Timestamp = Timestamp.now(),
    val createdAt: Timestamp = Timestamp.now()
) {
    fun toTourney(): Tourney {
        val javaDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(dateTime.seconds, dateTime.nanoseconds.toLong()), ZoneOffset.UTC
        )
        val javaCreatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(createdAt.seconds, createdAt.nanoseconds.toLong()), ZoneOffset.UTC
        )

        return Tourney(id, title, javaDateTime, javaCreatedAt)
    }

    companion object {
        fun fromTourney(tourney: Tourney) =
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
            )
    }
}