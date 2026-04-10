package com.travelplanner.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelplanner.databinding.FragmentExploreBinding
import com.travelplanner.ui.adapters.SuggestionAdapter
import com.travelplanner.utils.ItineraryUtils

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SuggestionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SuggestionAdapter()
        binding.recyclerSuggestions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSuggestions.adapter = adapter
        adapter.submitList(ItineraryUtils.nearbyGems())

        binding.btnSearch.setOnClickListener {
            val q = binding.editSearch.text.toString().trim()
            val results = if (q.isEmpty()) ItineraryUtils.nearbyGems() else ItineraryUtils.searchByVibe(q)
            adapter.submitList(results)
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
                    adapter.submitList(ItineraryUtils.searchByVibe(query))
                }
            }
            binding.chipGroupVibes.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
