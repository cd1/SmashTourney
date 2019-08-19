package com.gmail.cristiandeives.smashtourney

import android.app.ProgressDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.gmail.cristiandeives.smashtourney.data.TourneySnapshotParser
import com.gmail.cristiandeives.smashtourney.databinding.FragmentListTourneysBinding
import com.google.android.material.snackbar.Snackbar
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.SimpleDateFormat

@MainThread
class ListTourneysFragment : Fragment() {
    private val viewModel by activityViewModels<ListTourneysViewModel>()
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.list_tourneys_progress))
            setCancelable(false)
        }
    }

    private lateinit var binding: FragmentListTourneysBinding
    private lateinit var tourneysAdapter: ListTourneysAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentListTourneysBinding.inflate(inflater, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            tourneysCount = viewModel.tourneysCount
        }

        val query = viewModel.tourneysQuery
        val opts = FirestoreRecyclerOptions.Builder<Tourney>()
            .setQuery(query, TourneySnapshotParser)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        tourneysAdapter = ListTourneysAdapter(opts)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tourneysAdapter
        }

        viewModel.listState.observe(viewLifecycleOwner) { res: Resource<Nothing>? ->
            Log.v(TAG, "> listState#onChanged(t=$res)")

            when (res) {
                is Resource.Loading -> startLoadProgress()
                is Resource.Error -> displayFailureMessage()
            }

            if (res?.isFinished == true) {
                stopLoadProgress()
            }

            Log.v(TAG, "< listState#onChanged(t=$res)")
        }
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
    private fun displayFailureMessage() {
        Snackbar.make(requireView(), R.string.list_tourneys_error, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = ListTourneysFragment::class.java.simpleName
    }
}