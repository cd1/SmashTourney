package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository

@UiThread
class ListTourneysViewModel : ViewModel() {
    private val repo = FirestoreRepository.getInstance()
    private val _listState = MutableLiveData<Resource<Nothing>>()
    private val _tourneysCount = MutableLiveData<Int>()

    val tourneysQuery = repo.getAvailableTourneysQuery()
    val listState: LiveData<Resource<Nothing>> = _listState
    val tourneysCount: LiveData<Int> = _tourneysCount

    init {
        loadTourneysQuery()
    }

    private fun loadTourneysQuery() {
        _listState.value = Resource.Loading()

        tourneysQuery.addSnapshotListener { snap, ex ->
            if (ex != null) {
               Log.e(TAG, "list tourney failed: ${ex.message}", ex)
                _listState.value = Resource.Error(Error.Server())
                return@addSnapshotListener
            }

            _tourneysCount.value = (snap?.documents?.size ?: 0)
            _listState.value = Resource.Success()
        }
    }

    sealed class Error : RuntimeException() {
        class Server : Error()
    }

    companion object {
        private val TAG = ListTourneysViewModel::class.java.simpleName
    }
}