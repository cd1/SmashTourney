package com.gmail.cristiandeives.smashtourney

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.gmail.cristiandeives.smashtourney.databinding.FragmentNewTourneyBinding
import com.google.android.material.snackbar.Snackbar
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.text.SimpleDateFormat

@MainThread
class NewTourneyFragment : Fragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener,
    NewTourneyActionHandler {

    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val viewModel by viewModels<NewTourneyViewModel>()
    private val navController by lazy { findNavController() }
    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.create_tourney_progress))
            setCancelable(false)
        }
    }

    private lateinit var binding: FragmentNewTourneyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "> onCreateView(...)")

        binding = FragmentNewTourneyBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner

            vm = viewModel
            action = this@NewTourneyFragment
        }

        Log.v(TAG, "< onCreateView(...)")
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(TAG, "> onViewCreated(...)")
        super.onViewCreated(view, savedInstanceState)

        viewModel.createState.observe(viewLifecycleOwner) { res: Resource<Nothing>? ->
            Log.v(TAG, "> createState#onChanged(t=$res)")

            when (res) {
                is Resource.Loading -> startCreateProgress()
                is Resource.Success -> onTourneyCreated()
                is Resource.Error -> displayFailureMessage()
            }

            if (res?.isFinished == true) {
                stopCreateProgress()
            }

            Log.v(TAG, "< createState#onChanged(t=$res)")
        }

        Log.v(TAG, "< onViewCreated(...)")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        Log.v(TAG, "> onDateSet(..., year=$year, month=$month, dayOfMonth=$dayOfMonth)")

        val currentDateTime = viewModel.dateTime.value ?: LocalDateTime.now()
        viewModel.dateTime.value = currentDateTime
            .withYear(year)
            .withMonth(month + 1)
            .withDayOfMonth(dayOfMonth)

        Log.v(TAG, "< onDateSet(..., year=$year, month=$month, dayOfMonth=$dayOfMonth)")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Log.v(TAG, "> onTimeSet(..., hourOfDay=$hourOfDay, minute=$minute)")

        val currentDateTime = viewModel.dateTime.value ?: LocalDateTime.now()

        viewModel.dateTime.value = currentDateTime
            .withHour(hourOfDay)
            .withMinute(minute)

        Log.v(TAG, "< onTimeSet(..., hourOfDay=$hourOfDay, minute=$minute)")
    }

    override fun showDatePickerDialog(view: View) {
        val currentDate = viewModel.dateTime.value?.toLocalDate() ?: LocalDate.now()

        val datePickerAction = NewTourneyFragmentDirections.actionNewTourneyDatePicker(
            currentDate.year,
            currentDate.monthValue,
            currentDate.dayOfMonth
        )
        navController.navigate(datePickerAction)
    }

    @UiThread
    override fun showTimePickerDialog(view: View) {
        val currentTime = viewModel.dateTime.value?.toLocalTime() ?: LocalTime.now()

        val timePickerAction = NewTourneyFragmentDirections.actionNewTourneyTimePicker(
            currentTime.hour,
            currentTime.minute
        )
        navController.navigate(timePickerAction)
    }

    @UiThread
    private fun startCreateProgress() {
        progressDialog.show()
    }

    @UiThread
    private fun stopCreateProgress() {
        progressDialog.dismiss()
    }

    @UiThread
    private fun onTourneyCreated() {
        sharedViewModel.isTourneyJustCreated.value = Event(true)

        navController.navigateUp()
    }

    @UiThread
    private fun displayFailureMessage() {
        Snackbar.make(requireView(), R.string.create_tourney_error, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = NewTourneyFragment::class.java.simpleName
    }
}
