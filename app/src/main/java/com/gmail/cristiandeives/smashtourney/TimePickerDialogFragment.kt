package com.gmail.cristiandeives.smashtourney

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs

class TimePickerDialogFragment : DialogFragment() {
    private val args by navArgs<TimePickerDialogFragmentArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.v(TAG, "> onCreateDialog(...)")

        val ctx = requireContext()
        val dialog = TimePickerDialog(
            ctx,
            fragmentManager?.primaryNavigationFragment as TimePickerDialog.OnTimeSetListener,
            args.hour,
            args.minute,
            DateFormat.is24HourFormat(ctx)
        )

        Log.v(TAG, "< onCreateDialog(...)")
        return dialog
    }

    companion object {
        private val TAG = TimePickerDialogFragment::class.java.simpleName
    }
}