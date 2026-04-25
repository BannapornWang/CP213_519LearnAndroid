package com.travelplanner.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.travelplanner.R
import com.travelplanner.data.TripStatus
import com.travelplanner.databinding.ActivityMainBinding
import com.travelplanner.ui.adapters.TripAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TripViewModel by viewModels()
    private lateinit var adapter: TripAdapter
    private var currentFilter: TripStatus? = null

    private val filterOptions = listOf(
        null to "All",
        TripStatus.UPCOMING to "Upcoming",
        TripStatus.ONGOING to "Ongoing",
        TripStatus.PLANNING to "Planning",
        TripStatus.COMPLETED to "Completed"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilterChips()
        setupFab()
        observeTrips()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_trips
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_trips -> true // already here
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    false
                }
                R.id.nav_journal -> {
                    startActivity(Intent(this, JournalActivity::class.java))
                    false // don't keep it selected; Journal has its own bottom nav state
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = TripAdapter { trip ->
            val intent = Intent(this, TripDetailActivity::class.java)
            intent.putExtra(TripDetailActivity.EXTRA_TRIP_ID, trip.id)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val trip = adapter.currentList[position]
                viewModel.deleteTrip(trip.id)
                Toast.makeText(this@MainActivity, "Trip '${trip.title}' deleted", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val background = android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#EF4444")) // Red
                
                if (dX < 0) { // Swiping to the left
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    val icon = androidx.core.content.ContextCompat.getDrawable(this@MainActivity, android.R.drawable.ic_menu_delete)
                    icon?.let {
                        it.setTint(android.graphics.Color.WHITE)
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupFilterChips() {
        filterOptions.forEach { (status, label) ->
            val chip = Chip(this).apply {
                text = label
                isCheckable = true
                isChecked = (status == currentFilter)
                shapeAppearanceModel = shapeAppearanceModel.withCornerSize(100f)
                textSize = 13f

                if (status == currentFilter) {
                    setChipBackgroundColorResource(R.color.primary)
                    setTextColor(Color.WHITE)
                    chipStrokeWidth = 0f
                } else {
                    setChipBackgroundColorResource(android.R.color.transparent)
                    setTextColor(getColor(R.color.text_secondary))
                    chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)
                    chipStrokeColor = android.content.res.ColorStateList.valueOf(
                        getColor(R.color.border)
                    )
                }

                setOnClickListener {
                    currentFilter = status
                    refreshFilterChipStyles()
                    refreshList()
                }
            }
            binding.chipGroupFilter.addView(chip)
        }
        // select "All" initially
        (binding.chipGroupFilter.getChildAt(0) as? Chip)?.isChecked = true
    }

    private fun refreshFilterChipStyles() {
        filterOptions.forEachIndexed { index, (status, _) ->
            val chip = binding.chipGroupFilter.getChildAt(index) as? Chip ?: return@forEachIndexed
            val selected = status == currentFilter
            chip.isChecked = selected
            if (selected) {
                chip.setChipBackgroundColorResource(R.color.primary)
                chip.setTextColor(Color.WHITE)
                chip.chipStrokeWidth = 0f
            } else {
                chip.setChipBackgroundColorResource(android.R.color.transparent)
                chip.setTextColor(getColor(R.color.text_secondary))
                chip.chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)
                chip.chipStrokeColor = android.content.res.ColorStateList.valueOf(
                    getColor(R.color.border)
                )
            }
        }
    }

    private fun setupFab() {
        binding.fabAddTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }
        binding.btnPlanTrip.setOnClickListener {
            startActivity(Intent(this, CreateTripActivity::class.java))
        }
    }

    private fun observeTrips() {
        viewModel.allTrips.observe(this) { trips ->
            // Update headline
            val upcoming = trips.count { it.status == TripStatus.UPCOMING || it.status == TripStatus.ONGOING }
            binding.textHeadline.text = if (upcoming > 0)
                "$upcoming trip${if (upcoming > 1) "s" else ""} ahead"
            else
                "Where to next?"
            refreshList()
        }
    }

    private fun refreshList() {
        val trips = viewModel.allTrips.value ?: emptyList()
        val filtered = if (currentFilter == null) trips else trips.filter { it.status == currentFilter }
        adapter.submitList(filtered)
        binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }
}
