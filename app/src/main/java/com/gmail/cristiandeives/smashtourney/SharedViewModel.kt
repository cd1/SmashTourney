package com.gmail.cristiandeives.smashtourney

import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

@UiThread
class SharedViewModel : ViewModel() {
    val isTourneyJustCreated = MutableLiveData<Event<Boolean>>()
}