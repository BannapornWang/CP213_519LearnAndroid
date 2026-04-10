package com.tripmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripmate.data.network.RetrofitInstance
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val response: JsonObject) : UiState()
    data class Error(val message: String) : UiState()
}

class AIGeneratorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun generateItinerary(destination: String, travelers: String, pace: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Building the post body
                val body = JsonObject().apply {
                    addProperty("destination", destination)
                    addProperty("travelers", travelers.toIntOrNull() ?: 1)
                    val prefs = JsonObject()
                    prefs.addProperty("pace", pace)
                    add("preferences", prefs)
                }

                val result = RetrofitInstance.aiService.generateItinerary(body)
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown network error")
            }
        }
    }
}
