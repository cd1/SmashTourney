package com.gmail.cristiandeives.smashtourney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Player

@MainThread
class PlayersAdapter(opts: FirestoreRecyclerOptions<Player>) : FirestoreRecyclerAdapter<Player, PlayerViewHolder>(opts) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_player, parent, false)

        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int, player: Player) {
        val ctx = holder.itemView.context

        holder.setPlayerText(ctx.getString(R.string.view_tourney_player_label, player.nickname, player.fighter.name))
    }
}