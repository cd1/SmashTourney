package com.gmail.cristiandeives.smashtourney

import android.view.View
import androidx.annotation.UiThread

@UiThread
interface NewTourneyActionHandler {
    fun showDatePickerDialog(view: View)
    fun showTimePickerDialog(view: View)
    fun createTourney(view: View)
}