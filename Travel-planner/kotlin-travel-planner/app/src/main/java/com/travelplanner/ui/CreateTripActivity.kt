package com.travelplanner.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.travelplanner.R
import com.travelplanner.databinding.ActivityCreateTripBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            } else if (created == false) {
                binding.btnCreate.isEnabled = true
                binding.btnCreate.text = "Create Trip"
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        binding.btnCreate.setOnClickListener { createTrip() }
        binding.editStartDate.setOnClickListener { showDatePicker() }

        // Fix Spinner text visibility
        val currencies = resources.getStringArray(com.travelplanner.R.array.currencies)
        binding.spinnerCurrency.adapter = ArrayAdapter(this, com.travelplanner.R.layout.spinner_item, currencies).also {
            it.setDropDownViewResource(com.travelplanner.R.layout.spinner_item)
        }

    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.editStartDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun createTrip() {
        val title       = binding.editTitle.text.toString().trim()
        val destination = binding.editDestination.text.toString().trim()
        val startDate   = binding.editStartDate.text.toString().trim()
        val daysStr     = binding.editDays.text.toString().trim()
        val budgetStr   = binding.editBudget.text.toString().trim()
        val currency    = binding.spinnerCurrency.selectedItem?.toString() ?: "THB"
        val apiKey      = ""

        if (title.isEmpty() || destination.isEmpty() || startDate.isEmpty() ||
            daysStr.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val days   = daysStr.toIntOrNull() ?: 3
        val budget = budgetStr.toDoubleOrNull() ?: 0.0

        // compute endDate
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try { cal.time = fmt.parse(startDate)!! } catch (e: Exception) {
            Toast.makeText(this, "Invalid date (use YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
            return
        }
        cal.add(java.util.Calendar.DAY_OF_YEAR, days - 1)
        val endDate = fmt.format(cal.time)

        binding.btnCreate.isEnabled = false
        binding.btnCreate.text = "Generating AI Itinerary..."


        viewModel.createTrip(title, destination, startDate, endDate, budget, currency, days, apiKey)
    }
}
