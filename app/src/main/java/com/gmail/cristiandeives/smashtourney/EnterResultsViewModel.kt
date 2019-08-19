package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository
import com.gmail.cristiandeives.smashtourney.data.Player
import com.gmail.cristiandeives.smashtourney.data.PlayerSnapshotParser

@MainThread
class EnterResultsViewModel(private val tourneyId: String) : ViewModel() {
    private val repo = FirestoreRepository.getInstance()
    private val playersFirestoreQuery = repo.getPlayersQuery(tourneyId)

    val availablePlayers = MutableLiveData<List<Player>>()
    val champion = MutableLiveData<Player>()
    val runnerUps = mutableSetOf<Player>()

    val enterStateRes = MutableLiveData<Resource<Nothing>>()
    val loadPlayersRes = MutableLiveData<Resource<List<Player>>>()

    init {
        loadTourneyPlayers(tourneyId)
    }

    fun enterResults() {
        val actualChampion = champion.value
        if (actualChampion == null) {
            enterStateRes.value = Resource.Error(Error.MissingChampion())
            return
        }

        if (runnerUps.isEmpty()) {
            enterStateRes.value = Resource.Error(Error.MissingRunnerUp())
            return
        }

        if (runnerUps.find { it == actualChampion } != null) {
            enterStateRes.value = Resource.Error(Error.RunnerUpIsChampion())
            return
        }

        val runnerUpIds = runnerUps.map { it.id }.toSet()

        enterStateRes.value = Resource.Loading()

        repo.enterResults(tourneyId, actualChampion.id, runnerUpIds).addOnSuccessListener {
            Log.d(TAG, "enter results finished successfully")
            enterStateRes.value = Resource.Success()
        }.addOnFailureListener { ex ->
            Log.d(TAG, "enter results failed: ${ex.message}", ex)
            enterStateRes.value = Resource.Error(Error.Server())
        }.addOnCanceledListener {
            Log.d(TAG, "enter results was canceled")
            enterStateRes.value = Resource.Canceled()
        }
    }

    private fun loadTourneyPlayers(tourneyId: String) {
        loadPlayersRes.value = Resource.Loading()

        playersFirestoreQuery.addSnapshotListener { snap, ex ->
            Log.v(TAG, "> playersFirestoreQuery#onEvent(snapshot=$snap, exception=$ex)")

            if (ex != null) {
                Log.e(TAG, "load players query with with tourney ID = $tourneyId failed: ${ex.message}", ex)
                loadPlayersRes.value = Resource.Error(Error.Server())
                return@addSnapshotListener
            }

            val players = snap?.documents?.map(PlayerSnapshotParser::parseSnapshot)
                ?: emptyList()
            Log.d(TAG, players.map { it.toString() }.toString())

            availablePlayers.value = players
            if (champion.value !in players) {
                champion.value = players.first()
            }
            loadPlayersRes.value = Resource.Success(players)
        }
    }

    sealed class Error : RuntimeException() {
        class MissingChampion : Error()

        class MissingRunnerUp : Error()

        class RunnerUpIsChampion : Error()

        class Server : Error()
    }

    companion object {
        private val TAG = EnterResultsViewModel::class.java.simpleName
    }
}