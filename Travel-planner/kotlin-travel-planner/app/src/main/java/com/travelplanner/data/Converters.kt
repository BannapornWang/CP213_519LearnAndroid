package com.travelplanner.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter fun fromTripStatus(v: TripStatus): String = v.name
    @TypeConverter fun toTripStatus(v: String): TripStatus = TripStatus.valueOf(v)

    @TypeConverter fun fromDayPlanList(v: List<DayPlan>): String = gson.toJson(v)
    @TypeConverter
    fun toDayPlanList(v: String): List<DayPlan> {
        val type = object : TypeToken<List<DayPlan>>() {}.type
        return gson.fromJson(v, type) ?: emptyList()
    }

    @TypeConverter fun fromExpenseList(v: List<Expense>): String = gson.toJson(v)
    @TypeConverter
    fun toExpenseList(v: String): List<Expense> {
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(v, type) ?: emptyList()
    }

    @TypeConverter fun fromBookingList(v: List<Booking>): String = gson.toJson(v)
    @TypeConverter
    fun toBookingList(v: String): List<Booking> {
        val type = object : TypeToken<List<Booking>>() {}.type
        return gson.fromJson(v, type) ?: emptyList()
    }
}
