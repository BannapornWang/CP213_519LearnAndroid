package com.travelplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.data.Trip
import com.travelplanner.data.TripRepository
import com.travelplanner.data.AppDatabase
import com.travelplanner.databinding.ItemJournalTripBinding
import com.travelplanner.utils.BudgetUtils

class JournalAdapter(
    private val onClick: (Trip) -> Unit
) : ListAdapter<Trip, JournalAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemJournalTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemJournalTripBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val trip = getItem(position)
        val ctx = holder.binding.root.context
        with(holder.binding) {
            // Title, destination, dates
            textJTitle.text       = trip.title
            textJDestination.text = trip.destination
            textJDates.text       = "${BudgetUtils.formatShortDate(trip.startDate)} – ${BudgetUtils.formatShortDate(trip.endDate)}"
            textJDays.text        = BudgetUtils.getDaysCount(trip.startDate, trip.endDate).toString()

            // Mood badge
            if (trip.mood.isNotEmpty()) {
                textMood.text = trip.mood
                textMood.visibility = View.VISIBLE
            } else {
                textMood.visibility = View.GONE
            }

            // Rating
            if (trip.rating > 0) {
                ratingJournal.rating = trip.rating.toFloat()
                ratingJournal.visibility = View.VISIBLE
            } else {
                ratingJournal.visibility = View.GONE
            }

            // Visited places cost + bookings info
            val repo = TripRepository(AppDatabase.getDatabase(ctx).tripDao())
            val days = repo.tripDays(trip)
            val expenses = repo.tripExpenses(trip)
            val bookings = repo.tripBookings(trip)

            // Sum costs of visited (ticked) places across all days
            val visitedCost = days.sumOf { day -> day.places.filter { it.visited }.sumOf { it.cost } }
            textJSpent.text    = BudgetUtils.formatCurrency(visitedCost, trip.currency)
            textJExpenses.text = "${expenses.size} expenses"
            textJBookings.text = "${bookings.size} bookings"
            textJPhotos.text   = "0 photos"

            root.setOnClickListener { onClick(trip) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(a: Trip, b: Trip) = a.id == b.id
            override fun areContentsTheSame(a: Trip, b: Trip) = a == b
        }
    }
}
