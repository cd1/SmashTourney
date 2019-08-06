package com.gmail.cristiandeives.smashtourney

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.MainThread
import com.gmail.cristiandeives.smashtourney.data.Fighter

@MainThread
class FighterSpinnerAdapter(ctx: Context) : ArrayAdapter<Fighter>(ctx, android.R.layout.simple_spinner_item) {
    var data = emptyList<Fighter>()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getCount() = data.size

    override fun getItem(position: Int) = data[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as TextView

        textView.text = getItem(position).toAdapterString()

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView

        textView.text = getItem(position).toAdapterString()

        return view
    }

    companion object {
        private fun Fighter.toAdapterString() =
            "${String.format("%02d", number)}${if (isEcho) "ε" else ""} - $name"
    }
}