package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.google.firebase.firestore.Query
import org.threeten.bp.LocalDateTime

@UiThread
class MainViewModel : ViewModel() {
    private val repo = FirestoreRepository.getInstance()
    private val _createTourneyState = MutableLiveData<TaskState>()
    private val _isTourneyJustCreated = MutableLiveData<Event<Boolean>>()
    private val _listTourneysState = MutableLiveData<TaskState>()
    private var tourneysQueryInstance: Query? = null

    val createTourneyTitle = MutableLiveData<String>()
    val createTourneyDateTime = MutableLiveData<LocalDateTime>()
    val createTourneyState: LiveData<TaskState> = _createTourneyState
    val isTourneyJustCreated: LiveData<Event<Boolean>> = _isTourneyJustCreated
    val listTourneysState: LiveData<TaskState> = _listTourneysState
    var tourneysSize = 0
        private set

    init {
        resetCreateTourneyData()
    }

    fun createTourney() {
        val currentTitle = createTourneyTitle.value.orEmpty()
        val currentDateTime = createTourneyDateTime.value ?: LocalDateTime.now()

        val tourney = Tourney(title = currentTitle, dateTime = currentDateTime)

        Log.i(TAG, "creating tourney: $tourney")
        _createTourneyState.value = TaskState.IN_PROGRESS
        repo.createTourney(tourney).addOnSuccessListener {
            Log.d(TAG, "create tourney finished successfully")

            _isTourneyJustCreated.value = Event(true)
            _createTourneyState.value = TaskState.SUCCESS
            resetCreateTourneyData()
        }.addOnFailureListener { ex ->
            Log.e(TAG, "create tourney failed: ${ex.message}", ex)

            _createTourneyState.value = TaskState.FAILED
        }.addOnCanceledListener {
            Log.d(TAG, "create tourney was canceled")

            _createTourneyState.value = TaskState.CANCELED
        }
    }

    fun getTourneysQuery(): Query {
        var query = tourneysQueryInstance

        if (query == null) {
            _listTourneysState.value = TaskState.IN_PROGRESS

            query = repo.getTourneysQuery().apply {
                addSnapshotListener { snap, ex ->
                    if (ex != null) {
                        _listTourneysState.value = TaskState.FAILED
                        return@addSnapshotListener
                    }

                    tourneysSize = (snap?.documents?.size ?: 0)
                    _listTourneysState.value = TaskState.SUCCESS
                }
            }.also { tourneysQueryInstance = it }
        }

        return query
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