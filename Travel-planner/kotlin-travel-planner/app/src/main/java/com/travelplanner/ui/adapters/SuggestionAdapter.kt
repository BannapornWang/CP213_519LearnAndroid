package com.travelplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.data.PlaceSuggestion
import com.travelplanner.databinding.ItemSuggestionBinding

class SuggestionAdapter(
    private val onAddClick: (PlaceSuggestion) -> Unit
) : ListAdapter<PlaceSuggestion, SuggestionAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemSuggestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = getItem(position)
        with(holder.binding) {
            textName.text        = s.name
            textCategory.text    = s.category
            textDescription.text = s.description
            textAddress.text     = s.address
            textRating.text      = "★ ${s.rating}"
            textPrice.text       = "$".repeat(s.priceLevel + 1)
            val h = s.durationMinutes / 60
            val m = s.durationMinutes % 60
            textDuration.text    = if (h > 0) "${h}h${if (m > 0) " ${m}m" else ""}" else "${m}m"
            textVibes.text       = s.vibes.joinToString(" · ")
            btnAdd.setOnClickListener { onAddClick(s) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PlaceSuggestion>() {
            override fun areItemsTheSame(a: PlaceSuggestion, b: PlaceSuggestion) = a.id == b.id
            override fun areContentsTheSame(a: PlaceSuggestion, b: PlaceSuggestion) = a == b
        }
    }
}
