package com.gmail.cristiandeives.smashtourney.data

import org.threeten.bp.LocalDateTime

data class Tourney(
    val id: String = "",
    val title: String = "",
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
)