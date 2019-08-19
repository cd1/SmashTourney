package com.gmail.cristiandeives.smashtourney

import android.view.View
import androidx.annotation.UiThread

@UiThread
interface ViewTourneyActionHandler {
    fun addPlayer(view: View)
    fun enterResults(view: View)
}