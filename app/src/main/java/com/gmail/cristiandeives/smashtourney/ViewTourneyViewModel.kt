package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.gmail.cristiandeives.smashtourney.data.TourneySnapshotParser

@UiThread
class ViewTourneyViewModel(private val tourneyId: String) : ViewModel() {
    private val repo = FirestoreRepository.getInstance()

    val playersFirestoreQuery = repo.getPlayersQuery(tourneyId)
    val loadTourneyRes = MutableLiveData<Resource<Tourney>>()
    val loadPlayersRes = MutableLiveData<Resource<Nothing>>()
    val tourney = MediatorLiveData<Tourney>().apply {
        value = Tourney()
    }
    val playersCount = MutableLiveData<Int>()

    init {
        tourney.addSource(loadTourneyRes) { res ->
            Log.v(TAG, "> tourney#onChanged(t=$res)")

            if (res is Resource.Success) {
                tourney.value = res.data
            }

            Log.v(TAG, "< tourney#onChanged(t=$res)")
        }

        loadTourney()
        loadPlayersQuery()
    }

    private fun loadTourney() {
        loadTourneyRes.value = Resource.Loading()

        repo.loadTourney(tourneyId).addSnapshotListener { snap, ex ->
            Log.v(TAG, "> loadTourney#onEvent(snapshot=$snap, exception=$ex)")

            if (ex == null) {
                if (snap?.exists() == true) {
                    val tourney = TourneySnapshotParser.parseSnapshot(snap)
                    loadTourneyRes.value = Resource.Success(tourney)
                } else {
                    Log.d(TAG, "tourney with ID = $tourneyId doesn't exist")
                    loadTourneyRes.value = Resource.Error(Error.NoSuchTourney())
                }

            } else {
                Log.e(TAG, "load tourney with ID = $tourneyId failed: ${ex.message}", ex)
                loadTourneyRes.value = Resource.Error(Error.Server())
            }

            Log.v(TAG, "< loadTourney#onEvent(snapshot=$snap, exception=$ex)")
        }
    }

    private fun loadPlayersQuery() {
        loadPlayersRes.value = Resource.Loading()

        playersFirestoreQuery.addSnapshotListener { snap, ex ->
            Log.v(TAG, "> playersFirestoreQuery#onEvent(snapshot=$snap, exception=$ex)")

            if (ex != null) {
                Log.e(TAG, "load players query with with tourney ID = $tourneyId failed: ${ex.message}", ex)
                loadPlayersRes.value = Resource.Error(Error.Server())
                return@addSnapshotListener
            }

            playersCount.value = (snap?.size() ?: 0)
            loadPlayersRes.value = Resource.Success()
        }
    }

    sealed class Error : RuntimeException() {
        class NoSuchTourney : Error()

        class Server : Error()
    }

    companion object {
        private val TAG = ViewTourneyViewModel::class.java.simpleName
    }
}