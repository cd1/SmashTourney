package com.gmail.cristiandeives.smashtourney

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.gmail.cristiandeives.smashtourney.data.Fighter

class FighterSpinnerBindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("availableFighters")
        fun Spinner.setAvailableFighters(availableFighters: List<Fighter>) {
            (adapter as FighterSpinnerAdapter).data = availableFighters
        }

        @JvmStatic
        @BindingAdapter("selectedFighter")
        fun Spinner.setSelectedFighter(selectedFighter: Fighter) {
            for (index in 0 until adapter.count) {
                if (getItemAtPosition(index) == selectedFighter) {
                    setSelection(index)
                    break
                }
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "selectedFighter")
        fun Spinner.getSelectedFighter() = selectedItem as Fighter

        @JvmStatic
        @BindingAdapter("selectedFighterAttrChanged")
        fun Spinner.setSelectedFighterAttrChanged(listener: InverseBindingListener) {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    listener.onChange()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    listener.onChange()
                }
            }
        }
    }
}