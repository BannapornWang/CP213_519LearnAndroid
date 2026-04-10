package com.tripmate.data.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface AiService {
    @POST("api/ai/generate-itinerary")
    suspend fun generateItinerary(@Body request: JsonObject): JsonObject
}
