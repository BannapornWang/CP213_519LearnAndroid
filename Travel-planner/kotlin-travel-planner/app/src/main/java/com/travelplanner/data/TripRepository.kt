package com.travelplanner.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TripRepository(private val dao: TripDao) {

    private val gson = Gson()

    val allTrips: Flow<List<Trip>> = dao.getAllTrips()

    // ─── helpers to serialize nested models ──────────────────────────────────

    fun tripDays(trip: Trip): List<DayPlan> {
        val type = object : TypeToken<List<DayPlan>>() {}.type
        return gson.fromJson(trip.daysJson, type) ?: emptyList()
    }

    fun tripExpenses(trip: Trip): List<Expense> {
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(trip.expensesJson, type) ?: emptyList()
    }

    fun tripBookings(trip: Trip): List<Booking> {
        val type = object : TypeToken<List<Booking>>() {}.type
        return gson.fromJson(trip.bookingsJson, type) ?: emptyList()
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    suspend fun createTrip(
        title: String,
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        currency: String,
        days: List<DayPlan> = emptyList()
    ): Trip {
        val trip = Trip(
            id = UUID.randomUUID().toString(),
            title = title,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            budget = budget,
            currency = currency,
            status = TripStatus.PLANNING,
            daysJson = gson.toJson(days)
        )
        dao.insertTrip(trip)
        return trip
    }

    suspend fun updateTrip(trip: Trip) {
        dao.updateTrip(trip.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTrip(id: String) = dao.deleteTripById(id)

    suspend fun addExpense(trip: Trip, expense: Expense) {
        val list = tripExpenses(trip).toMutableList().also { it.add(expense) }
        updateTrip(trip.copy(expensesJson = gson.toJson(list)))
    }

    suspend fun removeExpense(trip: Trip, expenseId: String) {
        val list = tripExpenses(trip).filter { it.id != expenseId }
        updateTrip(trip.copy(expensesJson = gson.toJson(list)))
    }

    suspend fun addBooking(trip: Trip, booking: Booking) {
        val list = tripBookings(trip).toMutableList().also { it.add(booking) }
        updateTrip(trip.copy(bookingsJson = gson.toJson(list)))
    }

    suspend fun removeBooking(trip: Trip, bookingId: String) {
        val list = tripBookings(trip).filter { it.id != bookingId }
        updateTrip(trip.copy(bookingsJson = gson.toJson(list)))
    }

    suspend fun updateDays(trip: Trip, days: List<DayPlan>) {
        updateTrip(trip.copy(daysJson = gson.toJson(days)))
    }
}
