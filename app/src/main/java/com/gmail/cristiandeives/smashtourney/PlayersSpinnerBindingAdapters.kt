package com.gmail.cristiandeives.smashtourney

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.gmail.cristiandeives.smashtourney.data.Player

class PlayersSpinnerBindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("availablePlayers")
        fun Spinner.setAvailablePlayers(availablePlayers: List<Player>?) {
            availablePlayers?.let { players ->
                (adapter as PlayersSpinnerAdapter).data = players
            }
        }

        @JvmStatic
        @BindingAdapter("champion")
        fun Spinner.setChampion(champion: Player?) {
            for (index in 0 until adapter.count) {
                if (getItemAtPosition(index) == champion) {
                    setSelection(index)
                    break
                }
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "champion")
        fun Spinner.getChampion() = selectedItem as Player?

        @JvmStatic
        @BindingAdapter("championAttrChanged")
        fun Spinner.setChampionAttrChanged(listener: InverseBindingListener) {
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