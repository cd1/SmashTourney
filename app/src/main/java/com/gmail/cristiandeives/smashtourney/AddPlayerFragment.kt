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
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gmail.cristiandeives.smashtourney.databinding.FragmentAddPlayerBinding
import com.google.android.material.snackbar.Snackbar

// TODO: improve UX when the tourney doesn't exist (e.g. return to the "list tourneys" screen)
@MainThread
class AddPlayerFragment : Fragment() {
    private val args by navArgs<AddPlayerFragmentArgs>()
    private val viewModel by viewModels<AddPlayerViewModel>(factoryProducer = {
        ViewModelFactory(args.tourneyId)
    })
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.add_player_progress))
            setCancelable(false)
        }
    }
    private var currentSnackbar: Snackbar? = null

    private lateinit var binding: FragmentAddPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentAddPlayerBinding.inflate(inflater, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            vm = viewModel

            spinnerFighter.adapter = FighterSpinnerAdapter(requireContext())
        }

        viewModel.apply {
            isFighterRandom.observe(viewLifecycleOwner) { isRandom: Boolean? ->
                Log.v(TAG, "> isFighterRandom#onChanged(t=$isRandom)")

                // if the property android:enabled existed for Spinner, we could do this
                // in the layout XML via Data Binding... :-(
                binding.spinnerFighter.isEnabled = (isRandom != true)

                Log.v(TAG, "< isFighterRandom#onChanged(t=$isRandom)")
            }

            addPlayerState.observe(viewLifecycleOwner) { res: Resource<Nothing>? ->
                Log.v(TAG, "> addPlayerState#onChanged(t=$res)")

                when (res) {
                    is Resource.Loading -> onLoading()
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> onError(res)
                }

                if (res?.isFinished == true) {
                    stopAddProgress()
                }

                Log.v(TAG, "< addPlayerState#onChanged(t=$res)")
            }
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
    private fun startAddProgress() {
        progressDialog.show()
    }

    @UiThread
    private fun stopAddProgress() {
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
        startAddProgress()
    }

    @UiThread
    private fun onSuccess() {
        stopAddProgress()
        findNavController().navigateUp()
    }

    @UiThread
    private fun onError(errorRes: Resource.Error<Nothing>) {
        errorRes.exception?.consume()?.let { ex ->
            when (ex) {
                is AddPlayerViewModel.Error.MissingNickname -> displayMissingNicknameFailure()
                is AddPlayerViewModel.Error.MissingFighter -> displayMissingFighterFailure()
                is AddPlayerViewModel.Error.DuplicateNickname -> displayDuplicateNicknameFailure(ex.nickname)
                is AddPlayerViewModel.Error.Server -> displayAddFailureMessage()
            }
        }
    }

    @UiThread
    private fun displayMissingNicknameFailure() {
        currentSnackbar = Snackbar.make(requireView(), R.string.add_player_missing_nickname_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.editNickname.requestFocus()
    }

    @UiThread
    private fun displayMissingFighterFailure() {
        currentSnackbar = Snackbar.make(requireView(), R.string.add_player_missing_fighter_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.spinnerFighter.requestFocus()
    }

    @UiThread
    private fun displayDuplicateNicknameFailure(nickname: String) {
        currentSnackbar = Snackbar.make(requireView(), getString(R.string.add_player_duplicate_nickname_error, nickname), Snackbar.LENGTH_LONG).apply {
            show()
        }
        binding.spinnerFighter.requestFocus()
    }

    @UiThread
    private fun displayAddFailureMessage() {
        currentSnackbar = Snackbar.make(requireView(), R.string.add_player_error, Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    companion object {
        private val TAG = AddPlayerFragment::class.java.simpleName
    }
}