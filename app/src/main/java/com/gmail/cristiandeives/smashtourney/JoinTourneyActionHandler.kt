package com.gmail.cristiandeives.smashtourney

import android.view.View
import androidx.annotation.UiThread

@UiThread
interface JoinTourneyActionHandler {
    fun joinTourney(view: View)
}