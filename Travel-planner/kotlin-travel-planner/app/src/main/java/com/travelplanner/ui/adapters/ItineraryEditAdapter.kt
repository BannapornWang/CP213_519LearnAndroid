package com.travelplanner.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.R
import com.travelplanner.data.DayPlan
import com.travelplanner.data.Place
import java.util.Collections

sealed class EditItineraryItem {
    data class DayHeader(val dayIndex: Int, val date: String) : EditItineraryItem()
    data class PlaceItem(val place: Place) : EditItineraryItem()
}

class ItineraryEditAdapter(
    private var items: MutableList<EditItineraryItem> = mutableListOf(),
    private val onDragStart: ((RecyclerView.ViewHolder) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PLACE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EditItineraryItem.DayHeader -> TYPE_HEADER
            is EditItineraryItem.PlaceItem -> TYPE_PLACE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_place, parent, false)
            PlaceViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EditItineraryItem.DayHeader -> (holder as HeaderViewHolder).bind(item)
            is EditItineraryItem.PlaceItem -> (holder as PlaceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<EditItineraryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItems(): List<EditItineraryItem> = items

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        init {
            textView.setTextColor(Color.WHITE)
            textView.textSize = 18f
            textView.setPadding(0, 32, 0, 16)
        }

        fun bind(header: EditItineraryItem.DayHeader) {
            textView.text = "Day ${header.dayIndex + 1} - ${header.date}"
        }
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textLocation: TextView = itemView.findViewById(R.id.textAddress)
        private val textTime: TextView = itemView.findViewById(R.id.textDuration)
        private val textCost: TextView = itemView.findViewById(R.id.textCost)
        private val textCategory: TextView = itemView.findViewById(R.id.textCategory)
        private val textIndex: TextView = itemView.findViewById(R.id.textIndex)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        
        // Use a generic View to hide checkbox so it represents edit mode drag handle if needed
        private val checkVisited: View? = itemView.findViewById(R.id.checkVisited)

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(item: EditItineraryItem.PlaceItem) {
            val place = item.place
            textName.text = place.name
            textLocation.text = place.address
            textTime.text = "${place.durationMinutes}m"
            textCost.text = "฿${place.cost.toInt()}"
            textIndex.text = "≡" // Indicate it's draggable
            textIndex.setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    onDragStart?.invoke(this@PlaceViewHolder)
                }
                false
            }
            
            textCategory.text = place.category
            
            // Hide delete and checkbox in edit mode
            btnDelete.visibility = View.GONE
            checkVisited?.visibility = View.GONE
        }
    }
}
