package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.Repository
import com.gmail.cristiandeives.smashtourney.data.TaskState
import com.gmail.cristiandeives.smashtourney.data.Tourney
import org.threeten.bp.LocalDateTime

@UiThread
class MainViewModel : ViewModel() {
    private val repo = Repository.instance
    private val _createTourneyState = MutableLiveData<TaskState>()
    private val _isTourneyJustCreated = MutableLiveData<Event<Boolean>>()

    val createTourneyTitle = MutableLiveData<String>()
    val createTourneyDateTime = MutableLiveData<LocalDateTime>()
    val createTourneyState: LiveData<TaskState> = _createTourneyState
    val isTourneyJustCreated: LiveData<Event<Boolean>> = _isTourneyJustCreated

    init {
        resetCreateTourneyData()
    }

    fun createTourney() {
        val currentTitle = createTourneyTitle.value.orEmpty()
        val currentDateTime = createTourneyDateTime.value ?: LocalDateTime.now()

        val tourney = Tourney(currentTitle, currentDateTime)

        Log.i(TAG, "creating tourney: $tourney")
        _createTourneyState.value = TaskState.IN_PROGRESS
        repo.createTourney(tourney).onSuccess {
            Log.d(TAG, "create tourney finished successfully")

            _isTourneyJustCreated.value = Event(true)
            _createTourneyState.value = TaskState.SUCCESS
            resetCreateTourneyData()
        }.onFailure { ex ->
            Log.e(TAG, "create tourney failed: ${ex.message}", ex)

            _createTourneyState.value = TaskState.FAILED
        }.onCanceled {
            Log.d(TAG, "create tourney was canceled")

            _createTourneyState.value = TaskState.CANCELED
        }
    }

    private fun resetCreateTourneyData() {
        createTourneyTitle.value = ""
        createTourneyDateTime.value = LocalDateTime.now()
        _createTourneyState.postValue(null)
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}