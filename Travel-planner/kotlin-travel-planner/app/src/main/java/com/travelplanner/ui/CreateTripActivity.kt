package com.travelplanner.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.travelplanner.databinding.ActivityCreateTripBinding

class CreateTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTripBinding
    private val viewModel: TripViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        viewModel.tripCreatedEvent.observe(this) { created ->
            if (created == true) {
                Toast.makeText(this, "Trip created with AI itinerary!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnCreate.setOnClickListener { createTrip() }
    }

    private fun createTrip() {
        val title       = binding.editTitle.text.toString().trim()
        val destination = binding.editDestination.text.toString().trim()
        val startDate   = binding.editStartDate.text.toString().trim()
        val daysStr     = binding.editDays.text.toString().trim()
        val budgetStr   = binding.editBudget.text.toString().trim()
        val currency    = binding.spinnerCurrency.selectedItem?.toString() ?: "THB"

        if (title.isEmpty() || destination.isEmpty() || startDate.isEmpty() ||
            daysStr.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val days   = daysStr.toIntOrNull() ?: 3
        val budget = budgetStr.toDoubleOrNull() ?: 0.0

        // compute endDate
        val cal = java.util.Calendar.getInstance()
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        try { cal.time = fmt.parse(startDate)!! } catch (e: Exception) {
            Toast.makeText(this, "Invalid date (use YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
            return
        }
        cal.add(java.util.Calendar.DAY_OF_YEAR, days - 1)
        val endDate = fmt.format(cal.time)

        binding.btnCreate.isEnabled = false
        binding.btnCreate.text = "Generating AI Itinerary..."

        viewModel.createTrip(title, destination, startDate, endDate, budget, currency, days)
    }
}
