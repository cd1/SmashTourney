package com.gmail.cristiandeives.smashtourney

import android.view.View
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView

@MainThread
class TourneyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textTitle = view.findViewById<TextView>(R.id.text_title)
    private val textDate = view.findViewById<TextView>(R.id.text_date)
    private val textTime = view.findViewById<TextView>(R.id.text_time)

    fun setTitle(title: String) {
        textTitle.text = title
    }

    fun setDate(date: String) {
        textDate.text = date
    }

    fun setTime(time: String) {
        textTime.text = time
    }
}