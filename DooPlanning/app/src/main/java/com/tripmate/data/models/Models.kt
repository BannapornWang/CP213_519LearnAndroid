package com.tripmate.data.models

data class Destination(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val avgPrice: Double,
    val category: String,
    val vibe: String,
    val location: String
)

data class NearbyPlace(
    val id: String,
    val name: String,
    val distance: String,
    val suggestedDuration: String,
    val category: String
)

data class TripRequest(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val vibes: List<String>,
    val budget: Double,
    val preferences: String
)

data class GeneratedItinerary(
    val tripId: String,
    val days: List<DayPlan>,
    val totalBudget: Double,
    val route: String
)

data class DayPlan(
    val date: String,
    val activities: List<Activity>,
    val dayBudget: Double
)

data class Activity(
    val id: String,
    val place: String,
    val startTime: String,
    val endTime: String,
    val duration: String,
    val travelTimeToNext: String,
    val cost: Double,
    val category: String
)

data class TripRecord(
    val id: String,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val budget: Double,
    val actualSpent: Double,
    val coverImage: String,
    val memories: List<String>,
    val itinerary: GeneratedItinerary?
)

data class BookingInfo(
    val type: String,
    val provider: String,
    val confirmationCode: String,
    val details: String,
    val dateTime: String
)

data class Expense(
    val id: String,
    val date: String,
    val description: String,
    val amount: Double,
    val category: ExpenseCategory,
    val receipt: String?
)

data class Memory(
    val id: String,
    val date: String,
    val note: String,
    val photos: List<String>,
    val rating: Int
)

enum class ExpenseCategory {
    TRANSPORT, FOOD, ACCOMMODATION, ACTIVITY, SHOPPING, OTHER
}

