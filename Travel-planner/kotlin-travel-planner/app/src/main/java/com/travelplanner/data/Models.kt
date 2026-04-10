package com.travelplanner.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

// ─── Enums ───────────────────────────────────────────────────────────────────

enum class TripStatus { PLANNING, UPCOMING, ONGOING, COMPLETED }
enum class ExpenseCategory { TRANSPORT, FOOD, ATTRACTION, ACCOMMODATION, OTHER }
enum class BookingType { FLIGHT, HOTEL, CAR, ACTIVITY }

// ─── Embedded / nested models (not Room entities, stored as JSON) ─────────────

@Parcelize
data class Place(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val durationMinutes: Int,
    val cost: Double,
    val notes: String = "",
    var visited: Boolean = false
) : Parcelable

@Parcelize
data class DayPlan(
    val date: String,       // ISO "YYYY-MM-DD"
    val places: MutableList<Place> = mutableListOf()
) : Parcelable

@Parcelize
data class Expense(
    val id: String,
    val category: ExpenseCategory,
    val amount: Double,
    val description: String,
    val date: String
) : Parcelable

@Parcelize
data class Booking(
    val id: String,
    val type: BookingType,
    val title: String,
    val confirmationNo: String = "",
    val date: String,
    val details: String = ""
) : Parcelable

// ─── Room Entity ─────────────────────────────────────────────────────────────

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val currency: String = "THB",
    val status: TripStatus = TripStatus.PLANNING,
    val description: String = "",
    // stored as JSON strings
    val daysJson: String = "[]",
    val expensesJson: String = "[]",
    val bookingsJson: String = "[]",
    val mood: String = "",
    val rating: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─── BudgetSummary (computed, not stored) ────────────────────────────────────

data class BudgetSummary(
    val total: Double = 0.0,
    val transport: Double = 0.0,
    val food: Double = 0.0,
    val attraction: Double = 0.0,
    val accommodation: Double = 0.0,
    val other: Double = 0.0
)

// ─── Place suggestion (for Explore / Vibe Search) ────────────────────────────

data class PlaceSuggestion(
    val id: String,
    val name: String,
    val category: String,
    val vibes: List<String>,
    val rating: Double,
    val priceLevel: Int,
    val durationMinutes: Int,
    val description: String,
    val address: String
)
