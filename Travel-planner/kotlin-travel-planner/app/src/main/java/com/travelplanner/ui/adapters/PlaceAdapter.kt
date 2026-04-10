package com.travelplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.data.Place
import com.travelplanner.databinding.ItemPlaceBinding

class PlaceAdapter(
    private val onToggleVisited: (Place) -> Unit,
    private val onDelete: (Place) -> Unit
) : ListAdapter<Place, PlaceAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place = getItem(position)
        with(holder.binding) {
            textIndex.text    = (position + 1).toString()
            textName.text     = place.name
            textCategory.text = place.category
            textAddress.text  = place.address
            val h = place.durationMinutes / 60
            val m = place.durationMinutes % 60
            textDuration.text = if (h > 0) "${h}h${if (m > 0) " ${m}m" else ""}" else "${m}m"
            textCost.text     = if (place.cost > 0) "฿${"%.0f".format(place.cost)}" else "Free"
            checkVisited.setImageResource(
                if (place.visited) android.R.drawable.checkbox_on_background
                else android.R.drawable.radiobutton_off_background
            )
            checkVisited.setOnClickListener { onToggleVisited(place) }
            btnDelete.setOnClickListener { onDelete(place) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Place>() {
            override fun areItemsTheSame(a: Place, b: Place) = a.id == b.id
            override fun areContentsTheSame(a: Place, b: Place) = a == b
        }
    }
}
