package com.gmail.cristiandeives.smashtourney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Tourney
import com.gmail.cristiandeives.smashtourney.databinding.ViewHolderTourneyBinding
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@MainThread
class ListTourneysAdapter(opts: FirestoreRecyclerOptions<Tourney>) : FirestoreRecyclerAdapter<Tourney, ListTourneysAdapter.ViewHolder>(opts) {
    var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    var timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderTourneyBinding.inflate(inflater, parent, false).apply {
            setDateFormatter(dateFormatter)
            setTimeFormatter(timeFormatter)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, tourney: Tourney) {
        holder.binding.tourney = tourney

        val directions = ListTourneysFragmentDirections.actionListTourneysViewTourney(tourney.id)
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(directions))
    }

    class ViewHolder(val binding: ViewHolderTourneyBinding) : RecyclerView.ViewHolder(binding.root)
}