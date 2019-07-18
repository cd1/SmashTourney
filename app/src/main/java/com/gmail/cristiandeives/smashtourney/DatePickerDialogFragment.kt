package com.gmail.cristiandeives.smashtourney

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs

class DatePickerDialogFragment : DialogFragment() {
    private val args by navArgs<DatePickerDialogFragmentArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.v(TAG, "> onCreateDialog(...)")

        val dialog = DatePickerDialog(
            requireContext(),
            fragmentManager?.primaryNavigationFragment as DatePickerDialog.OnDateSetListener,
            args.year,
            args.month - 1,
            args.dayOfMonth
        )

        Log.v(TAG, "< onCreateDialog(...)")
        return dialog
    }

    companion object {
        private val TAG = DatePickerDialogFragment::class.java.simpleName
    }
}