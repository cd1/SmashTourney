package com.gmail.cristiandeives.smashtourney

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gmail.cristiandeives.smashtourney.databinding.FragmentJoinTourneyBinding
import com.google.android.material.snackbar.Snackbar

// TODO: improve UX when the tourney doesn't exist (e.g. return to the "list tourneys" screen)
@MainThread
class JoinTourneyFragment : Fragment(), JoinTourneyActionHandler {
    private val args by navArgs<JoinTourneyFragmentArgs>()
    private val viewModel by viewModels<JoinTourneyViewModel>(factoryProducer = {
        ViewModelFactory(args.tourneyId)
    })
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.join_tourney_progress))
            setCancelable(false)
        }
    }
    private var currentSnackbar: Snackbar? = null

    private lateinit var binding: FragmentJoinTourneyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentJoinTourneyBinding.inflate(inflater, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            vm = viewModel
            action = this@JoinTourneyFragment

            spinnerFighter.adapter = FighterSpinnerAdapter(requireContext())
        }

        viewModel.apply {
            isFighterRandom.observe(viewLifecycleOwner, Observer { isRandom ->
                Log.v(TAG, "> isFighterRandom#onChanged(t=$isRandom)")

                // if the property android:enabled existed for Spinner, we could do this
                // in the layout XML via Data Binding... :-(
                binding.spinnerFighter.isEnabled = !isRandom

                Log.v(TAG, "< isFighterRandom#onChanged(t=$isRandom)")
            })

            joinState.observe(viewLifecycleOwner, Observer { res: Resource<Nothing>? ->
                Log.v(TAG, "> joinState#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onLoading()
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> onError(res)
                }

                if (res?.isFinished == true) {
                    stopJoinProgress()
                }

                Log.v(TAG, "< joinState#onChanged(t=$res)")
            })
        }

        Log.v(TAG, "< onViewCreated(...)")
    }

    override fun onStop() {
        Log.v(TAG, "> onStop()")
        super.onStop()

        hideKeyboard()
        dismissSnackbar()

        Log.v(TAG, "< onStop()")
    }

    @UiThread
    override fun joinTourney(view: View) {
        try {
            viewModel.joinPlayerToTourney()
        } catch (ex: JoinTourneyViewModel.Error) {
            when (ex) {
                is JoinTourneyViewModel.Error.MissingNickname -> displayMissingNicknameFailure()
                is JoinTourneyViewModel.Error.MissingFighter -> displayMissingFighterFailure()
            }
        }
    }

    @UiThread
    private fun startJoinProgress() {
        progressDialog.show()
    }

    @UiThread
    private fun stopJoinProgress() {
        progressDialog.dismiss()
    }

    @UiThread
    private fun hideKeyboard() {
        requireContext().getSystemService<InputMethodManager>()
            ?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    @UiThread
    private fun dismissSnackbar() {
        currentSnackbar?.dismiss()
    }

    @UiThread
    private fun onLoading() {
        startJoinProgress()
    }

    @UiThread
    private fun onSuccess() {
        stopJoinProgress()
        findNavController().navigateUp()
    }

    @UiThread
    private fun onError(errorRes: Resource.Error<Nothing>) {
        errorRes.exception?.consume()?.let { ex ->
            when (ex) {
                is JoinTourneyViewModel.Error.MissingNickname -> displayMissingNicknameFailure()
                is JoinTourneyViewModel.Error.MissingFighter -> displayMissingFighterFailure()
                is JoinTourneyViewModel.Error.DuplicateNickname -> displayDuplicateNicknameFailure(ex.nickname)
                is JoinTourneyViewModel.Error.Server -> displayJoinFailureMessage()
            }
        }
    }

    @UiThread
    private fun displayMissingNicknameFailure() {
        currentSnackbar = Snackbar.make(requireView(), R.string.join_tourney_missing_nickname_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.editNickname.requestFocus()
    }

    @UiThread
    private fun displayMissingFighterFailure() {
        currentSnackbar = Snackbar.make(requireView(), R.string.join_tourney_missing_fighter_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.spinnerFighter.requestFocus()
    }

    @UiThread
    private fun displayDuplicateNicknameFailure(nickname: String) {
        currentSnackbar = Snackbar.make(requireView(), getString(R.string.join_tourney_duplicate_nickname_error, nickname), Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.spinnerFighter.requestFocus()
    }

    @UiThread
    private fun displayJoinFailureMessage() {
        currentSnackbar = Snackbar.make(requireView(), R.string.join_tourney_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    companion object {
        private val TAG = JoinTourneyFragment::class.java.simpleName
    }
}