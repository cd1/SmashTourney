package com.gmail.cristiandeives.smashtourney

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.cristiandeives.smashtourney.data.Player
import com.gmail.cristiandeives.smashtourney.databinding.FragmentEnterResultsBinding
import com.google.android.material.snackbar.Snackbar

@MainThread
class EnterResultsFragment : Fragment() {
    private val args by navArgs<EnterResultsFragmentArgs>()
    private val viewModel by viewModels<EnterResultsViewModel>(factoryProducer = {
        ViewModelFactory(args.tourneyId)
    })
    private val loadPlayersProgressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.load_players_progress))
            setCancelable(false)
        }
    }
    private val enterResultsProgressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.enter_results_progress))
            setCancelable(false)
        }
    }
    private var currentSnackbar: Snackbar? = null

    private lateinit var binding: FragmentEnterResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentEnterResultsBinding.inflate(inflater, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        val ctx = requireContext()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            vm = viewModel

            spinnerChampion.adapter = PlayersSpinnerAdapter(ctx)
            recyclerViewRunnerUps.apply {
                layoutManager = LinearLayoutManager(ctx)
                adapter = RunnerUpPlayersRecyclerAdapter()
            }
        }

        viewModel.apply {
            champion.observe(viewLifecycleOwner, Observer { champion: Player? ->
                Log.v(TAG, "> champion#onChanged(t=$champion)")

                champion?.let { c ->
                    (binding.recyclerViewRunnerUps.adapter as RunnerUpPlayersRecyclerAdapter)
                        .updateChampion(c)
                }

                Log.v(TAG, "< champion#onChanged(t=$champion)")
            })

            loadPlayersRes.observe(viewLifecycleOwner, Observer { res: Resource<List<Player>>? ->
                Log.v(TAG, "> loadPlayerRes#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onLoadPlayersLoading()
                    is Resource.Success -> onLoadPlayersSuccess(res)
                    is Resource.Error -> onLoadPlayersError(res)
                }

                if (res?.isFinished == true) {
                    stopLoadPlayersProgress()
                }

                Log.v(TAG, "< loadPlayerRes#onChanged(t=$res)")
            })

            enterStateRes.observe(viewLifecycleOwner, Observer { res: Resource<Nothing>? ->
                Log.v(TAG, "> enterStateRes#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onEnterStateLoading()
                    is Resource.Success -> onEnterStateSuccess()
                    is Resource.Error -> onEnterStateError(res)
                }

                if (res?.isFinished == true) {
                    stopEnterStateProgress()
                }

                Log.v(TAG, "< enterStateRes#onChanged(t=$res)")
            })
        }

        Log.v(TAG, "< onViewCreated(...)")
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
    private fun onLoadPlayersLoading() {
        loadPlayersProgressDialog.show()
    }

    @UiThread
    private fun onLoadPlayersSuccess(res: Resource.Success<List<Player>>) {
        val players = res.data ?: return
        val actualChampion = viewModel.champion.value

        val runnerUpPlayers = players.map { p ->
            val isRunnerUpLive = MutableLiveData<Boolean>().apply {
                value = (p != actualChampion && p in viewModel.runnerUps)

                observe(viewLifecycleOwner, Observer { isPlayerRunnerUp: Boolean? ->
                    Log.v(TAG, "> [${p.nickname}].isRunnerUp#onChanged(t=$isPlayerRunnerUp)")

                    if (isPlayerRunnerUp == true) {
                        viewModel.runnerUps.add(p)
                    } else {
                        viewModel.runnerUps.remove(p)
                    }

                    Log.v(TAG, "< [${p.nickname}].isRunnerUp#onChanged(t=$isPlayerRunnerUp)")
                })
            }

            RunnerUpPlayersRecyclerAdapter.RunnerUpPlayer(p.nickname, p.fighter, p == actualChampion, isRunnerUpLive)
        }

        (binding.recyclerViewRunnerUps.adapter as RunnerUpPlayersRecyclerAdapter).data = runnerUpPlayers
    }

    @UiThread
    private fun onLoadPlayersError(res: Resource.Error<*>) {
        res.exception?.consume()?.let { ex ->
            when (ex) {
                is EnterResultsViewModel.Error.Server -> displayFailureMessage(R.string.load_players_error)
            }
        }
    }

    @UiThread
    private fun stopLoadPlayersProgress() {
        loadPlayersProgressDialog.dismiss()
    }

    @UiThread
    private fun onEnterStateLoading() {
        enterResultsProgressDialog.show()
    }

    @UiThread
    private fun onEnterStateSuccess() {
        findNavController().popBackStack(R.id.listTourneysFragment, false)
    }

    @UiThread
    private fun onEnterStateError(res: Resource.Error<*>) {
        res.exception?.consume()?.let { ex ->
            when (ex) {
                is EnterResultsViewModel.Error.MissingChampion -> displayFailureMessage(R.string.enter_results_missing_champion_error)
                is EnterResultsViewModel.Error.MissingRunnerUp -> displayFailureMessage(R.string.enter_results_missing_runner_up_error)
                is EnterResultsViewModel.Error.RunnerUpIsChampion -> displayFailureMessage(R.string.enter_results_runner_up_is_champion_error)
                is EnterResultsViewModel.Error.Server -> displayFailureMessage(R.string.enter_results_error)
            }
        }
    }

    @UiThread
    private fun stopEnterStateProgress() {
        enterResultsProgressDialog.dismiss()
    }

    @UiThread
    private fun displayFailureMessage(@StringRes messageRes: Int) {
        currentSnackbar = Snackbar.make(requireView(), messageRes, Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    companion object {
        private val TAG = EnterResultsFragment::class.java.simpleName
    }
}