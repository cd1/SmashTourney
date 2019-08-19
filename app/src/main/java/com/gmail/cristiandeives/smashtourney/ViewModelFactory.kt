package com.gmail.cristiandeives.smashtourney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val tourneyId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            ViewTourneyViewModel::class.java -> ViewTourneyViewModel(tourneyId)
            AddPlayerViewModel::class.java -> AddPlayerViewModel(tourneyId)
            EnterResultsViewModel::class.java -> EnterResultsViewModel(tourneyId)
            else -> throw IllegalArgumentException("ViewModelFactory: cannot create ViewModel for class $modelClass")
        }

        return modelClass.cast(viewModel)
            ?: throw IllegalStateException("ViewModelFactory: failed to cast ViewModel object to class $modelClass")
    }
}