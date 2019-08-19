package com.gmail.cristiandeives.smashtourney.data

import com.google.firebase.firestore.Exclude

data class Player(
    @get:Exclude val id: String = "",
    val nickname: String = "",
    val fighter: Fighter = Fighter()
)