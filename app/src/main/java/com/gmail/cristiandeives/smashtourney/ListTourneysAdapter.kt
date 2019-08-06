package com.gmail.cristiandeives.smashtourney

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.navigation.Navigation
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gmail.cristiandeives.smashtourney.data.Tourney
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@MainThread
class ListTourneysAdapter(opts: FirestoreRecyclerOptions<Tourney>) : FirestoreRecyclerAdapter<Tourney, TourneyViewHolder>(opts) {
    var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    var timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourneyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_tourney, parent, false)

        return TourneyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourneyViewHolder, position: Int, tourney: Tourney) {
        val ctx = holder.itemView.context

        holder.apply {
            setTitle(tourney.title.takeIf { it.isNotEmpty() } ?: ctx.getString(R.string.tourney_title_empty))
            setDate(dateFormatter.format(tourney.dateTime))
            setTime(timeFormatter.format(tourney.dateTime))
        }

        val directions = ListTourneysFragmentDirections.actionListTourneysViewTourney(tourney.id)
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(directions))
    }
}