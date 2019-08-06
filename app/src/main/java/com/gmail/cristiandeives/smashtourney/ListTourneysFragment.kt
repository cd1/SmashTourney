package com.gmail.cristiandeives.smashtourney

import android.app.ProgressDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.gmail.cristiandeives.smashtourney.data.TourneySnapshotParser
import com.google.android.material.snackbar.Snackbar
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.SimpleDateFormat

@MainThread
class ListTourneysFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.list_tourneys_progress))
            setCancelable(false)
        }
    }

    private lateinit var textNoTourneys: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tourneysAdapter: ListTourneysAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        val view = inflater.inflate(R.layout.fragment_list_tourneys, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = viewModel.getTourneysQuery()
        val opts = FirestoreRecyclerOptions.Builder<Tourney>()
            .setQuery(query, TourneySnapshotParser)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        tourneysAdapter = ListTourneysAdapter(opts)

        textNoTourneys = view.findViewById(R.id.text_no_tourneys)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tourneysAdapter
        }

        viewModel.listTourneysState.observe(viewLifecycleOwner, Observer { state: TaskState? ->
            Log.v(TAG, "> listTourneysState#onChanged(t=$state)")

            when (state) {
                TaskState.IN_PROGRESS -> startLoadProgress()
                TaskState.SUCCESS -> onTourneysLoaded()
                TaskState.FAILED -> displayFailureMessage()
                else -> Log.d(TAG, "skipping state $state")
            }

            if (state?.isComplete == true) {
                stopLoadProgress()
            }

            Log.v(TAG, "< listTourneysState#onChanged(t=$state)")
        })
    }

    override fun onStart() {
        Log.v(TAG, "> onStart()")
        super.onStart()

        val javaDateFormat = DateFormat.getTimeFormat(requireContext()) as? SimpleDateFormat
        val tf = javaDateFormat?.let {
            DateTimeFormatter.ofPattern(it.toPattern())
        } ?: DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

        tourneysAdapter.timeFormatter = tf

        Log.v(TAG, "< onStart()")
    }

    @UiThread
    private fun startLoadProgress() {
        progressDialog.show()
    }

    @UiThread
    private fun stopLoadProgress() {
        progressDialog.dismiss()
    }

    @UiThread
    private fun onTourneysLoaded() {
        if (viewModel.tourneysSize > 0) {
            displayData()
        } else {
            displayNoData()
        }
    }

    @UiThread
    private fun displayData() {
        textNoTourneys.isGone = true
        recyclerView.isVisible = true
    }

    @UiThread
    private fun displayNoData() {
        textNoTourneys.isVisible = true
        recyclerView.isGone = true
    }

    @UiThread
    private fun displayFailureMessage() {
        Snackbar.make(requireView(), R.string.list_tourneys_error, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = ListTourneysFragment::class.java.simpleName
    }
}