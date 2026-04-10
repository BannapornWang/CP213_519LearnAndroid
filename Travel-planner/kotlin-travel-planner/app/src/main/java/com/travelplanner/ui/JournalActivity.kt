package com.travelplanner.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelplanner.data.AppDatabase
import com.travelplanner.data.TripRepository
import com.travelplanner.data.TripStatus
import com.travelplanner.databinding.ActivityJournalBinding
import com.travelplanner.ui.adapters.JournalAdapter
import com.travelplanner.utils.BudgetUtils

class JournalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJournalBinding
    private val viewModel: TripViewModel by viewModels()
    private lateinit var adapter: JournalAdapter
    private var showOnlyCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupTabs()
        observeTrips()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationJournal.selectedItemId = com.travelplanner.R.id.nav_journal
        binding.bottomNavigationJournal.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.travelplanner.R.id.nav_trips -> {
                    finish() // go back to MainActivity
                    true
                }
                com.travelplanner.R.id.nav_journal -> true
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = JournalAdapter { trip ->
            val intent = Intent(this, TripDetailActivity::class.java)
            intent.putExtra(TripDetailActivity.EXTRA_TRIP_ID, trip.id)
            startActivity(intent)
        }
        binding.recyclerJournal.layoutManager = LinearLayoutManager(this)
        binding.recyclerJournal.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabAll.setOnClickListener {
            showOnlyCompleted = false
            updateTabStyles()
            refreshList()
        }
        binding.tabCompleted.setOnClickListener {
            showOnlyCompleted = true
            updateTabStyles()
            refreshList()
        }
    }

    private fun updateTabStyles() {
        binding.tabAll.apply {
            setTextColor(if (!showOnlyCompleted) getColor(com.travelplanner.R.color.primary) else getColor(com.travelplanner.R.color.text_secondary))
            textSize = if (!showOnlyCompleted) 14f else 14f
        }
        binding.tabCompleted.apply {
            setTextColor(if (showOnlyCompleted) getColor(com.travelplanner.R.color.primary) else getColor(com.travelplanner.R.color.text_secondary))
        }
    }

    private fun observeTrips() {
        viewModel.allTrips.observe(this) { allTrips ->
            val journalTrips = allTrips.filter {
                it.status == TripStatus.COMPLETED || it.status == TripStatus.ONGOING
            }
            val completedTrips = allTrips.filter { it.status == TripStatus.COMPLETED }

            // Update tab labels
            binding.tabAll.text = "All Trips (${journalTrips.size})"
            binding.tabCompleted.text = "Completed (${completedTrips.size})"

            // Update stats banner — sum visited place costs across all journal trips
            val repo = TripRepository(AppDatabase.getDatabase(this).tripDao())
            val totalSpent = journalTrips.sumOf { trip ->
                val days = repo.tripDays(trip)
                days.sumOf { day -> day.places.filter { it.visited }.sumOf { it.cost } }
            }
            val countries = journalTrips
                .map { it.destination.substringAfterLast(",").trim() }
                .filter { it.isNotEmpty() }
                .toSet()
                .size

            binding.textStatsTripsDone.text = completedTrips.size.toString()
            binding.textStatsTotalSpent.text = BudgetUtils.formatCurrency(totalSpent, "THB")
            binding.textStatsCountries.text = maxOf(countries, 1).toString()

            refreshList()
        }
    }

    private fun refreshList() {
        val allTrips = viewModel.allTrips.value ?: emptyList()
        val filtered = if (showOnlyCompleted) {
            allTrips.filter { it.status == TripStatus.COMPLETED }
        } else {
            allTrips.filter { it.status == TripStatus.COMPLETED || it.status == TripStatus.ONGOING }
        }
        adapter.submitList(filtered)
        val empty = filtered.isEmpty()
        binding.recyclerJournal.visibility = if (empty) View.GONE else View.VISIBLE
        binding.layoutEmpty.visibility     = if (empty) View.VISIBLE else View.GONE
    }
}
