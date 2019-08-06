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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Player
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.gmail.cristiandeives.smashtourney.databinding.FragmentViewTourneyBinding
import com.google.android.material.snackbar.Snackbar
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.SimpleDateFormat

@MainThread
class ViewTourneyFragment : Fragment(), JoinTourneyActionHandler {
    private val args by navArgs<ViewTourneyFragmentArgs>()
    private val viewModel by viewModels<ViewTourneyViewModel>(factoryProducer = {
        ViewModelFactory(args.tourneyId)
    })
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.view_tourney_progress))
            setCancelable(false)
        }
    }
    private var currentSnackbar: Snackbar? = null

    private lateinit var binding: FragmentViewTourneyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentViewTourneyBinding.inflate(inflater, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            vm = viewModel
            action = this@ViewTourneyFragment
        }

        val query = viewModel.playersFirestoreQuery
        val opts = FirestoreRecyclerOptions.Builder<Player>()
            .setQuery(query, Player::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PlayersAdapter(opts)
        }

        viewModel.apply {
            loadTourneyRes.observe(viewLifecycleOwner, Observer { res: Resource<Tourney>? ->
                Log.v(TAG, "> loadTourneyRes#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onTourneyLoading()
                    is Resource.Error -> onTourneyError(res)
                }

                if (res?.isFinished == true) {
                    stopLoadProgress()
                }

                Log.v(TAG, "< loadTourneyRes#onChanged(t=$res)")
            })

            loadPlayersRes.observe(viewLifecycleOwner, Observer { res: Resource<Boolean> ->
                Log.v(TAG, "> loadPlayersRes#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onPlayersLoading()
                    is Resource.Success -> onPlayersSuccess(res)
                    is Resource.Error -> onPlayersError(res)
                }

                binding.textPlayersStatus.isVisible =
                    (res is Resource.Loading
                            || res is Resource.Error
                            || (res is Resource.Success && (res.data != true)))
                binding.recyclerViewPlayers.isVisible =
                    (res is Resource.Success && (res.data == true))

                Log.v(TAG, "< loadPlayersRes#onChanged(t=$res)")
            })
        }

        Log.v(TAG, "< onViewCreated(...)")
    }

    override fun onStart() {
        Log.v(TAG, "> onStart()")
        super.onStart()

        val javaDateFormat = DateFormat.getTimeFormat(requireContext()) as? SimpleDateFormat

        binding.apply {
            dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            timeFormatter = javaDateFormat?.let {
                DateTimeFormatter.ofPattern(it.toPattern())
            } ?: DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        }

        Log.v(TAG, "< onStart()")
    }

    override fun onStop() {
        Log.v(TAG, "> onStop()")
        super.onStop()

        dismissSnackbar()

        Log.v(TAG, "< onStop()")
    }

    @UiThread
    private fun dismissSnackbar() {
        currentSnackbar?.dismiss()
    }

    @UiThread
    override fun joinTourney(view: View) {
        val directions = ViewTourneyFragmentDirections.actionViewTourneyJoinTourney(args.tourneyId)
        findNavController().navigate(directions)
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
    private fun onTourneyLoading() {
        startLoadProgress()
    }

    @UiThread
    private fun onTourneyError(res: Resource.Error<*>) {
        res.exception?.consume()?.let { ex ->
            when (ex) {
                is ViewTourneyViewModel.Error.NoSuchTourney -> displayNoSuchTourneyMessage()
                is ViewTourneyViewModel.Error.Server -> displayLoadingTourneyFailureMessage()
            }
        }
    }

    // TODO: improve UX when tourney doesn't exist (e.g. return to the "list tourneys" screen)
    @UiThread
    private fun displayNoSuchTourneyMessage() {
        currentSnackbar = Snackbar.make(requireView(), R.string.view_tourney_no_such_tourney_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    @UiThread
    private fun displayLoadingTourneyFailureMessage() {
        currentSnackbar = Snackbar.make(requireView(), R.string.view_tourney_load_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    @UiThread
    private fun onPlayersLoading() {
        displayLoadingPlayersProgressMessage()
    }

    @UiThread
    private fun onPlayersSuccess(res: Resource.Success<Boolean>) {
        if (res.data != true) {
            binding.textPlayersStatus.setText(R.string.view_tourney_player_no_players)
        }
    }

    @UiThread
    private fun onPlayersError(res: Resource.Error<*>) {
        res.exception?.consume()?.let { ex ->
            when (ex) {
                is ViewTourneyViewModel.Error.Server -> displayLoadingPlayersFailureMessage()
            }
        }
    }

    @UiThread
    private fun displayLoadingPlayersProgressMessage() {
        binding.textPlayersStatus.setText(R.string.view_tourney_player_progress)
    }

    @UiThread
    private fun displayLoadingPlayersFailureMessage() {
        binding.textPlayersStatus.setText(R.string.view_tourney_player_error)
    }

    companion object {
        private val TAG = ViewTourneyFragment::class.java.simpleName
    }
}