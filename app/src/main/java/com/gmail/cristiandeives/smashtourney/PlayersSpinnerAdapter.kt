package com.gmail.cristiandeives.smashtourney

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gmail.cristiandeives.smashtourney.data.Player

class PlayersSpinnerAdapter(ctx: Context) : ArrayAdapter<Player>(ctx, android.R.layout.simple_spinner_item) {
    var data = emptyList<Player>()
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

        textView.text = getItem(position).nickname

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView

        textView.text = getItem(position).nickname

        return view
    }
}