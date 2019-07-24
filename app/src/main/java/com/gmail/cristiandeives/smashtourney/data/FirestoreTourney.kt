package com.gmail.cristiandeives.smashtourney.data

import com.google.firebase.Timestamp
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class FirestoreTourney(
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

        return Tourney(title, javaDateTime, javaCreatedAt)
    }

    companion object {
        fun fromTourney(tourney: Tourney) =
            FirestoreTourney(
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