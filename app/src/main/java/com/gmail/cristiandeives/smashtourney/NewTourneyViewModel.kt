package com.gmail.cristiandeives.smashtourney

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.cristiandeives.smashtourney.data.FirestoreRepository
import com.gmail.cristiandeives.smashtourney.data.Tourney
import org.threeten.bp.LocalDateTime

class NewTourneyViewModel : ViewModel() {
    private val repo = FirestoreRepository.getInstance()
    private val _createState = MutableLiveData<Resource<Nothing>>()

    val title = MutableLiveData<String>()
    val dateTime = MutableLiveData<LocalDateTime>()
    val createState: LiveData<Resource<Nothing>> = _createState

    fun createTourney() {
        val currentTitle = title.value.orEmpty()
        val currentDateTime = dateTime.value ?: LocalDateTime.now()

        val tourney = Tourney(title = currentTitle, dateTime = currentDateTime)

        Log.i(TAG, "creating tourney: $tourney")
        _createState.value = Resource.Loading()
        repo.createTourney(tourney).addOnSuccessListener {
            Log.d(TAG, "create tourney finished successfully")

            _createState.value = Resource.Success()
        }.addOnFailureListener { ex ->
            Log.e(TAG, "create tourney failed: ${ex.message}", ex)

            _createState.value = Resource.Error(Error.Server())
        }.addOnCanceledListener {
            Log.d(TAG, "create tourney was canceled")

            _createState.value = Resource.Canceled()
        }
    }

    sealed class Error : RuntimeException() {
        class Server : Error()
    }

    companion object {
        private val TAG = NewTourneyViewModel::class.java.simpleName
    }
}