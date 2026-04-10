package com.travelplanner.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.data.AppDatabase
import com.travelplanner.data.Trip
import com.travelplanner.data.TripRepository
import com.travelplanner.data.TripStatus
import com.travelplanner.databinding.ItemTripBinding
import com.travelplanner.utils.BudgetUtils

private data class StatusStyle(val label: String, val color: Int)

class TripAdapter(
    private val onClick: (Trip) -> Unit
) : ListAdapter<Trip, TripAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val trip = getItem(position)
        val ctx = holder.binding.root.context
        with(holder.binding) {

            // ── Status badge ──────────────────────────────────────────────
            val style = statusStyle(trip.status)
            val bgAlpha = ColorUtils.setAlphaComponent(style.color, 51) // 20% opacity

            layoutStatusBadge.background.setTint(bgAlpha)
            viewStatusDot.background.setTint(style.color)
            textStatus.text = style.label
            textStatus.setTextColor(style.color)

            // ── Days chip ────────────────────────────────────────────────
            textDays.text = "${BudgetUtils.getDaysCount(trip.startDate, trip.endDate)}d"

            // ── Title / destination / dates ───────────────────────────────
            textTitle.text       = trip.title
            textDestination.text = trip.destination
            textDates.text       = "${BudgetUtils.formatShortDate(trip.startDate)} – ${BudgetUtils.formatShortDate(trip.endDate)}"

            // ── Budget ───────────────────────────────────────────────────
            val repo     = TripRepository(AppDatabase.getDatabase(ctx).tripDao())
            val expenses = repo.tripExpenses(trip)
            val summary  = BudgetUtils.calculateSummary(expenses)
            val pct      = if (trip.budget > 0) (summary.total / trip.budget) else 0.0

            val budgetColor = when {
                pct >= 1.0 -> Color.parseColor("#EF4444") // over
                pct >= 0.8 -> Color.parseColor("#F97316") // warning
                else       -> Color.parseColor("#22C55E") // ok
            }

            textSpent.text  = BudgetUtils.formatCurrency(summary.total, trip.currency)
            textSpent.setTextColor(budgetColor)
            textBudget.text = BudgetUtils.formatCurrency(trip.budget, trip.currency)

            // Custom progress bar: set width as fraction of parent
            viewProgressFill.background.setTint(budgetColor)
            viewProgressFill.post {
                val parent = viewProgressFill.parent as? android.view.View
                val totalWidth = parent?.width ?: 0
                val fillWidth = (minOf(pct, 1.0) * totalWidth).toInt()
                val lp = viewProgressFill.layoutParams
                lp.width = fillWidth
                viewProgressFill.layoutParams = lp
            }

            root.setOnClickListener { onClick(trip) }
        }
    }

    private fun statusStyle(status: TripStatus): StatusStyle = when (status) {
        TripStatus.PLANNING   -> StatusStyle("Planning",   Color.parseColor("#F97316"))
        TripStatus.UPCOMING   -> StatusStyle("Upcoming",   Color.parseColor("#38BDF8"))
        TripStatus.ONGOING    -> StatusStyle("Ongoing",    Color.parseColor("#22C55E"))
        TripStatus.COMPLETED  -> StatusStyle("Completed",  Color.parseColor("#6B7280"))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(a: Trip, b: Trip) = a.id == b.id
            override fun areContentsTheSame(a: Trip, b: Trip) = a == b
        }
    }
}
