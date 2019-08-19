package com.gmail.cristiandeives.smashtourney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Player
import com.gmail.cristiandeives.smashtourney.databinding.ViewHolderPlayerBinding

@MainThread
class PlayersRecyclerAdapter(opts: FirestoreRecyclerOptions<Player>) : FirestoreRecyclerAdapter<Player, PlayersRecyclerAdapter.ViewHolder>(opts) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderPlayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, player: Player) {
        holder.binding.player = player
    }

    class ViewHolder(val binding: ViewHolderPlayerBinding) : RecyclerView.ViewHolder(binding.root)
}