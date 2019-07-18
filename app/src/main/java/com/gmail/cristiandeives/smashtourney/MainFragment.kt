package com.gmail.cristiandeives.smashtourney

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar

@MainThread
class MainFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        Log.v(TAG, "< onCreateView(...)")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_new_tourney)
            .setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_main_new_tourney))

        viewModel.isTourneyJustCreated.observe(viewLifecycleOwner, EventObserver { isCreated ->
            Log.v(TAG, "> isTourneyJustCreated#onChanged(event=$isCreated)")

            if (isCreated) {
                displayTourneyCreatedMessage()
            }

            Log.v(TAG, "< isTourneyJustCreated#onChanged(event=$isCreated)")
        })

        Log.v(TAG, "< onViewCreated(...)")
    }

    @UiThread
    private fun displayTourneyCreatedMessage() {
        Snackbar.make(requireView(), R.string.create_tourney_success, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}