package com.gmail.cristiandeives.smashtourney

import android.app.Application
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen

class MainApplication : Application() {
    override fun onCreate() {
        Log.v(TAG, "> onCreate()")
        super.onCreate()

        AndroidThreeTen.init(this)

        Log.v(TAG, "< onCreate()")
    }

    companion object {
        private val TAG = MainApplication::class.java.simpleName
    }
}