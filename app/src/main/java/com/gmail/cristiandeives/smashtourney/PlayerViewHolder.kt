package com.gmail.cristiandeives.smashtourney

import android.view.View
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView

@MainThread
class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textPlayer = view.findViewById<TextView>(R.id.text_player)

    fun setPlayerText(text: String) {
        textPlayer.text = text
    }
}