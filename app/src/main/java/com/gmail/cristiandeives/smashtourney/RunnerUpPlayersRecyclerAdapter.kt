package com.gmail.cristiandeives.smashtourney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.gmail.cristiandeives.smashtourney.data.Fighter
import com.gmail.cristiandeives.smashtourney.data.Player
import com.gmail.cristiandeives.smashtourney.databinding.ViewHolderRunnerUpPlayerBinding

@MainThread
class RunnerUpPlayersRecyclerAdapter : RecyclerView.Adapter<RunnerUpPlayersRecyclerAdapter.ViewHolder>() {
    var data = emptyList<RunnerUpPlayer>()
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    fun updateChampion(champion: Player) {
        for ((i, p) in data.withIndex()) {
            val isChampionNow = (p.nickname == champion.nickname)

            // a player can't be runner-up anymore if it's now the champion
            if (isChampionNow) {
                p.isRunnerUp.value = false
            }

            if (isChampionNow != p.isChampion) {
                p.isChampion = isChampionNow

                notifyItemChanged(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderRunnerUpPlayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val runnerUpPlayer = data[position]

        holder.binding.player = runnerUpPlayer
    }

    class RunnerUpPlayer(
        val nickname: String,
        val fighter: Fighter,
        var isChampion: Boolean,
        val isRunnerUp: MutableLiveData<Boolean>
    )

    class ViewHolder(val binding: ViewHolderRunnerUpPlayerBinding) : RecyclerView.ViewHolder(binding.root)
}