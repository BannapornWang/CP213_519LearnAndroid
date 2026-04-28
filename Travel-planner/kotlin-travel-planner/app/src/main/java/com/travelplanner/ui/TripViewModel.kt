package com.travelplanner.ui

import android.app.Application
import androidx.lifecycle.*
import com.travelplanner.data.*
import com.travelplanner.utils.ItineraryUtils
import kotlinx.coroutines.launch
import java.util.UUID

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repo = TripRepository(db.tripDao())

    val allTrips: LiveData<List<Trip>> = repo.allTrips.asLiveData()

    private val _currentTrip = MutableLiveData<Trip?>()
    val currentTrip: LiveData<Trip?> = _currentTrip

    private val _tripCreatedEvent = MutableLiveData<Boolean>()
    val tripCreatedEvent: LiveData<Boolean> = _tripCreatedEvent

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ─── Load ─────────────────────────────────────────────────────────────────

    fun loadTrip(id: String) = viewModelScope.launch {
        _currentTrip.value = db.tripDao().getTripById(id)
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    fun getDays(trip: Trip) = repo.tripDays(trip)
    fun getExpenses(trip: Trip) = repo.tripExpenses(trip)
    fun getBookings(trip: Trip) = repo.tripBookings(trip)

    // ─── Create ───────────────────────────────────────────────────────────────

    fun createTrip(
        title: String, destination: String, startDate: String,
        endDate: String, budget: Double, currency: String, days: Int, apiKey: String
    ) = viewModelScope.launch {
        try {
            val itinerary = ItineraryUtils.generateItinerary(destination, startDate, days, budget, apiKey)
            repo.createTrip(title, destination, startDate, endDate, budget, currency, itinerary)
            _tripCreatedEvent.value = true
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown Error"
            _errorMessage.value = "Failed to create trip: $errorMessage"
            _tripCreatedEvent.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    fun updateStatus(trip: Trip, status: TripStatus) = viewModelScope.launch {
        repo.updateTrip(trip.copy(status = status))
        _currentTrip.value = trip.copy(status = status)
    }

    fun updateRating(trip: Trip, rating: Int) = viewModelScope.launch {
        repo.updateTrip(trip.copy(rating = rating))
        _currentTrip.value = trip.copy(rating = rating)
    }

    fun updateMood(trip: Trip, mood: String) = viewModelScope.launch {
        repo.updateTrip(trip.copy(mood = mood))
        _currentTrip.value = trip.copy(mood = mood)
    }

    fun updateDays(trip: Trip, days: List<DayPlan>) = viewModelScope.launch {
        repo.updateDays(trip, days)
        val updated = db.tripDao().getTripById(trip.id)
        _currentTrip.value = updated
    }

    // ─── Expenses ─────────────────────────────────────────────────────────────

    fun addExpense(trip: Trip, category: ExpenseCategory, amount: Double, desc: String, date: String) =
        viewModelScope.launch {
            val expense = Expense(UUID.randomUUID().toString(), category, amount, desc, date)
            repo.addExpense(trip, expense)
            _currentTrip.value = db.tripDao().getTripById(trip.id)
        }

    fun removeExpense(trip: Trip, expenseId: String) = viewModelScope.launch {
        repo.removeExpense(trip, expenseId)
        _currentTrip.value = db.tripDao().getTripById(trip.id)
    }

    // ─── Delete trip ──────────────────────────────────────────────────────────

    fun deleteTrip(id: String) = viewModelScope.launch { repo.deleteTrip(id) }
}
