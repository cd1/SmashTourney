package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.Fighter
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository
import com.gmail.cristiandeives.smashtourney.data.Player

@UiThread
class AddPlayerViewModel(private val tourneyId: String) : ViewModel() {
    private val repo = FirestoreRepository.getInstance()
    private val _addPlayerState = MutableLiveData<Resource<Nothing>>()
    private val _availableFighters = MutableLiveData<List<Fighter>>().apply {
        value = Fighter.DEFAULT_FIGHTERS
    }

    val addPlayerState: LiveData<Resource<Nothing>> = _addPlayerState
    val availableFighters: LiveData<List<Fighter>> = _availableFighters
    val nickname = MutableLiveData<String>()
    val fighter = MutableLiveData<Fighter>().apply {
        value = _availableFighters.value?.first()
    }
    val isFighterRandom = MutableLiveData<Boolean>()

    fun addPlayerToTourney() {
        _addPlayerState.value = Resource.Loading()

        val actualNickname = nickname.value.takeUnless { it.isNullOrBlank() }
        if (actualNickname == null) {
            _addPlayerState.value = Resource.Error(Error.MissingNickname())
            return
        }

        val actualFighter = if (isFighterRandom.value == true) Fighter.RANDOM else fighter.value
        if (actualFighter == null) {
            _addPlayerState.value = Resource.Error(Error.MissingFighter())
            return
        }

        // FIXME: there's a race condition which still allows a player to be added with an existing nickname
        // E.g. if two users try to add a player with the same nickname at the "same" time,
        // both instances will check the nicknames, both will report that they don't exist yet,
        // and both will add players with the same nickname. we need to add some constraint
        // to the server to prevent this situation effectively.
        repo.checkPlayerNicknameExists(tourneyId, actualNickname).addOnSuccessListener { snap ->
            if (snap.isEmpty) {
                val player = Player(nickname = actualNickname, fighter = actualFighter)

                repo.addPlayerToTourney(tourneyId, player).addOnSuccessListener {
                    Log.d(TAG, "add player to tourney finished successfully")
                    _addPlayerState.value = Resource.Success()
                }.addOnFailureListener { ex ->
                    Log.e(TAG, "add player to tourney failed: ${ex.message}", ex)
                    _addPlayerState.value = Resource.Error(Error.Server())
                }.addOnCanceledListener {
                    Log.d(TAG, "add player to tourney was canceled")
                    _addPlayerState.value = Resource.Canceled()
                }
            } else {
                Log.d(TAG, "player with nickname = $actualNickname already exists")
                _addPlayerState.value = Resource.Error(Error.DuplicateNickname(actualNickname))
            }
        }.addOnFailureListener { ex ->
            Log.e(TAG, "check player nickname failed: ${ex.message}", ex)
            _addPlayerState.value = Resource.Error(Error.Server())
        }.addOnCanceledListener {
            Log.d(TAG, "check player nickname was canceled")
            _addPlayerState.value = Resource.Canceled()
        }
    }

    sealed class Error : RuntimeException() {
        class MissingNickname : Error()

        class MissingFighter : Error()

        class DuplicateNickname(val nickname: String) : Error()

        class Server : Error()
    }

    companion object {
        private val TAG = AddPlayerViewModel::class.java.simpleName
    }
}