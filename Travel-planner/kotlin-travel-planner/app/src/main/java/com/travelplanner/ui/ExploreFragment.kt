package com.travelplanner.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelplanner.data.Place
import com.travelplanner.data.PlaceSuggestion
import com.travelplanner.data.Trip
import com.travelplanner.databinding.FragmentExploreBinding
import com.travelplanner.ui.adapters.SuggestionAdapter
import com.travelplanner.utils.ItineraryUtils
import kotlinx.coroutines.launch
import java.util.UUID

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SuggestionAdapter
    private val viewModel: TripViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SuggestionAdapter { suggestion ->
            showAddToTripDialog(suggestion)
        }
        binding.recyclerSuggestions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSuggestions.adapter = adapter
        adapter.submitList(ItineraryUtils.nearbyGems())

        binding.btnSearch.setOnClickListener {
            val q = binding.editSearch.text.toString().trim()
            if (q.isEmpty()) {
                adapter.submitList(ItineraryUtils.nearbyGems())
            } else {
                searchWithAI(q)
            }
        }

        // Vibe chip shortcuts
        val vibes = listOf("jazz bar" to "Jazz Bar", "quiet cafe" to "Quiet Cafe",
            "local food" to "Local Food", "nature peaceful" to "Nature Walk",
            "rooftop scenic" to "Rooftop", "cooking experience" to "Cooking Class")

        vibes.forEach { (query, label) ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = label
                isCheckable = true
                setOnClickListener {
                    binding.editSearch.setText(query)
                    searchWithAI(query)
                }
            }
            binding.chipGroupVibes.addView(chip)
        }
    }

    private fun searchWithAI(query: String) {
        binding.btnSearch.isEnabled = false
        binding.btnSearch.text = "..."
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val results = ItineraryUtils.searchPlacesAI(query)
                adapter.submitList(results)
                
                if (results.isEmpty()) {
                    Toast.makeText(requireContext(), "No results found for '$query'. Try another vibe!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSearch.isEnabled = true
                binding.btnSearch.text = "Search"
            }
        }
    }

    private fun showAddToTripDialog(suggestion: PlaceSuggestion) {
        viewModel.allTrips.observe(viewLifecycleOwner, object : Observer<List<Trip>> {
            override fun onChanged(value: List<Trip>) {
                viewModel.allTrips.removeObserver(this)
                if (value.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No trips found. Create one first!", Toast.LENGTH_SHORT).show()
                    return
                }

                val tripNames = value.map { it.title }
                AlertDialog.Builder(requireContext())
                    .setTitle("Add to which trip?")
                    .setItems(tripNames.toTypedArray()) { _, which ->
                        showDaySelectionDialog(value[which], suggestion)
                    }
                    .show()
            }
        })
    }

    private fun showDaySelectionDialog(trip: Trip, suggestion: PlaceSuggestion) {
        val days = viewModel.getDays(trip)
        val dayLabels = days.map { "Day ${days.indexOf(it) + 1} (${it.date})" }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Day")
            .setItems(dayLabels.toTypedArray()) { _, which ->
                val selectedDay = days[which]
                val newPlace = Place(
                    id = UUID.randomUUID().toString(),
                    name = suggestion.name,
                    category = suggestion.category,
                    address = suggestion.address,
                    durationMinutes = suggestion.durationMinutes,
                    cost = 0.0, // Default or can be mapped
                    notes = suggestion.description,
                    transport = ""
                )
                selectedDay.places.add(newPlace)
                viewModel.updateDays(trip, days)
                Toast.makeText(requireContext(), "Added to ${trip.title}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
