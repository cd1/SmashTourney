package com.gmail.cristiandeives.smashtourney.data

import com.gmail.cristiandeives.smashtourney.data.firestore.FirestoreRepository

interface Repository {
    fun createTourney(tourney: Tourney): TaskListener

    companion object {
        val instance by lazy { FirestoreRepository() }
    }
}