package com.travelplanner.utils

import com.travelplanner.BuildConfig
import android.util.Log
import com.travelplanner.data.DayPlan
import com.travelplanner.data.Place
import com.travelplanner.data.PlaceSuggestion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ItineraryUtils {

    private const val TAG = "ItineraryUtils"

    val PLACE_SUGGESTIONS = listOf(
        PlaceSuggestion("1","Grand Palace","Attraction", listOf("cultural","historic","iconic"),4.8,2,150,"The official residence of the Kings of Siam","Na Phra Lan Rd, Bangkok"),
        PlaceSuggestion("2","Chatuchak Weekend Market","Shopping", listOf("local","vibrant","shopping"),4.5,1,180,"One of the world's largest weekend markets","587/10 Chatuchak, Bangkok"),
        PlaceSuggestion("3","Wat Pho Temple","Attraction", listOf("cultural","peaceful","historic"),4.7,1,90,"Temple of the Reclining Buddha","2 Sanam Chai Rd, Bangkok"),
        PlaceSuggestion("4","Rooftop Jazz Bar","Nightlife", listOf("jazz","rooftop","chill","romantic"),4.6,3,120,"Hidden rooftop bar with live jazz and city views","27th Floor, Silom, Bangkok"),
        PlaceSuggestion("5","Quiet Workspace Cafe","Cafe", listOf("cafe","quiet","work","coffee"),4.4,2,120,"Specialty coffee shop with fast wifi","Ari, Bangkok"),
        PlaceSuggestion("6","Lumphini Park","Nature", listOf("nature","peaceful","jogging","relaxing"),4.5,0,90,"Largest park in central Bangkok","Lumphini, Pathum Wan, Bangkok"),
        PlaceSuggestion("7","Street Food Alley","Food", listOf("local","food","affordable","authentic"),4.7,1,60,"Famous street food lane with local dishes","Yaowarat, Bangkok"),
        PlaceSuggestion("8","Sky Walk Observation Deck","Attraction", listOf("scenic","iconic","photo","views"),4.6,2,90,"Stunning 360° panoramic city views","Mahanakhon Tower, Silom"),
        PlaceSuggestion("9","Local Cooking Class","Experience", listOf("cooking","cultural","hands-on","fun"),4.9,2,180,"Learn to cook Thai cuisine with local chef","Banglamphu, Bangkok"),
        PlaceSuggestion("10","Floating Market","Cultural", listOf("local","cultural","photography","iconic"),4.3,1,150,"Traditional market on wooden boats","Damnoen Saduak, Ratchaburi")
    )

    private val COST_RANGES = mapOf(
        "Attraction"  to Pair(200.0, 800.0),
        "Shopping"    to Pair(500.0, 3000.0),
        "Food"        to Pair(150.0, 500.0),
        "Cafe"        to Pair(100.0, 350.0),
        "Nightlife"   to Pair(400.0, 1500.0),
        "Nature"      to Pair(0.0, 200.0),
        "Experience"  to Pair(500.0, 2000.0),
        "Cultural"    to Pair(100.0, 600.0)
    )

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    suspend fun generateItinerary(destination: String, startDate: String, days: Int, budget: Double, userApiKey: String = ""): List<DayPlan> {
        val apiKey = if (userApiKey.isNotBlank()) userApiKey else getApiKey()
        Log.d(TAG, "Using API Key starting with: ${apiKey.take(8)}...")
        
        // Using dynamic model from BuildConfig (default: gemini-2.5-flash)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/${BuildConfig.GEMINI_MODEL}:generateContent?key=$apiKey"

        val prompt = """Create a detailed travel itinerary for $destination for $days days with a total budget of ${budget.toInt()} THB.
IMPORTANT: Return ONLY a raw JSON array of objects. Do not include markdown formatting or any other text.
Each object represents a day and should have:
- "date": "Day X"
- "places": an array of objects with:
    - "name": place name
    - "category": one of (Attraction, Shopping, Food, Cafe, Nightlife, Nature, Experience, Cultural)
    - "description": brief summary
    - "estimatedCost": number in THB
    - "durationMinutes": number
    - "transport": how to get there

Example structure:
[
  {
    "date": "Day 1",
    "places": [
      {
        "name": "Grand Palace",
        "category": "Attraction",
        "description": "Historical royal palace complex",
        "estimatedCost": 500,
        "durationMinutes": 120,
        "transport": "Taxi"
      }
    ]
  }
]"""

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            // Enforce JSON using GenerationConfig
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }.toString()

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val body = response.body?.string() ?: ""
                Log.d(TAG, "Raw Response: $body")

                if (!response.isSuccessful) {
                    var errorDetail = ""
                    try { errorDetail = JSONObject(body).getJSONObject("error").getString("message") } catch(e: Exception) {}
                    if (errorDetail.isBlank()) errorDetail = response.message
                    Log.e(TAG, "Gemini Error: ${response.code} - $body")
                    throw Exception("API Error ${response.code}: $errorDetail")
                }

                val root = JSONObject(body)
                val candidates = root.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    throw Exception("No candidates returned from AI")
                }
                
                val contentObj = candidates.getJSONObject(0).optJSONObject("content")
                if (contentObj == null) {
                    throw Exception("AI response blocked. Please check your prompt.")
                }
                val text = contentObj.getJSONArray("parts").getJSONObject(0).getString("text")

                parseAIResponse(text, destination, startDate, days, budget)
            } catch (e: Exception) {
                Log.e(TAG, "Exception in generateItinerary: ${e.message}")
                throw Exception(e.message ?: "Unknown Error during generation")
            }
        }
    }

    private fun parseAIResponse(jsonText: String, destination: String, startDate: String, days: Int, budget: Double): List<DayPlan> {
        return try {
            // Remove markdown code blocks if present
            var cleanText = jsonText.trim()
            if (cleanText.startsWith("```json")) {
                cleanText = cleanText.removePrefix("```json")
            }
            if (cleanText.endsWith("```")) {
                cleanText = cleanText.removeSuffix("```")
            }
            cleanText = cleanText.trim()

            val startIndex = cleanText.indexOf('[')
            val endIndex = cleanText.lastIndexOf(']')
            if (startIndex == -1) {
                Log.e(TAG, "Invalid JSON structure: $cleanText")
                throw Exception("AI returned invalid structure")
            }
            
            cleanText = cleanText.substring(startIndex, endIndex + 1)
            val jsonArray = JSONArray(cleanText)
            val result = mutableListOf<DayPlan>()
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance().apply { 
                time = try { fmt.parse(startDate)!! } catch(e:Exception) { java.util.Date() } 
            }

            for (i in 0 until jsonArray.length()) {
                val dayObj = jsonArray.getJSONObject(i)
                val dateStr = fmt.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, 1)
                
                val placesArr = dayObj.optJSONArray("places") ?: JSONArray()
                val dayPlaces = mutableListOf<Place>()
                for (j in 0 until placesArr.length()) {
                    val p = placesArr.getJSONObject(j)
                    dayPlaces.add(Place(
                        id = UUID.randomUUID().toString(),
                        name = p.optString("name", "Unknown"),
                        category = p.optString("category", "Attraction"),
                        address = "",
                        durationMinutes = p.optInt("durationMinutes", 60),
                        cost = p.optDouble("estimatedCost", 0.0),
                        transport = p.optString("transport", ""),
                        notes = p.optString("description", "")
                    ))
                }
                result.add(DayPlan(dateStr, dayPlaces))
            }
            result.ifEmpty { throw Exception("No days generated by AI") }
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Parse Error: ${e.message}")
            throw Exception("Failed to parse AI response: ${e.message}")
        }
    }

    fun generateMockItinerary(destination: String, startDate: String, days: Int, budget: Double, isFailed: Boolean = false): List<DayPlan> {
        val result = mutableListOf<DayPlan>()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply { time = try { fmt.parse(startDate)!! } catch(e:Exception) { java.util.Date() } }

        repeat(days) {
            val dateStr = fmt.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tag = if (isFailed) " (Local)" else ""
            val places = mutableListOf(
                Place(UUID.randomUUID().toString(), "Visit $destination $tag", "Attraction", destination, 120, budget/(days*3), "Walk", "Exploring the city"),
                Place(UUID.randomUUID().toString(), "Local Food $tag", "Food", destination, 60, 200.0, "Taxi", "Trying local dishes")
            )
            result.add(DayPlan(dateStr, places))
        }
        return result
    }

    fun optimizeRoute(places: List<Place>): List<Place> = places
    private fun dist(a: Place, b: Place) = Math.random()

    fun searchByVibe(query: String): List<PlaceSuggestion> = PLACE_SUGGESTIONS.take(6)
    fun nearbyGems(): List<PlaceSuggestion> = PLACE_SUGGESTIONS.shuffled().take(6)
    suspend fun searchPlacesAI(query: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        // Using dynamic model from BuildConfig (default: gemini-2.5-flash)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/${BuildConfig.GEMINI_MODEL}:generateContent?key=$apiKey"
        
        val prompt = """Recommend 5 to 7 interesting travel places based on the user's vibe query: "$query".
IMPORTANT: Return ONLY a raw JSON array of objects.
Each object must have:
- "name": string
- "category": string (e.g. Attraction, Cafe, Shopping, Food, Nature, Experience, Nightlife)
- "tags": array of 1 to 3 descriptive short string tags
- "rating": number 1.0 to 5.0
- "priceLevel": integer 1 to 4
- "durationMinutes": integer (e.g. 60, 120)
- "description": brief summary
- "address": string location
"""
        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
            
        val response = httpClient.newCall(request).execute()
        val body = response.body?.string() ?: ""
        
        if (!response.isSuccessful) {
            var errorDetail = ""
            try { errorDetail = JSONObject(body).getJSONObject("error").getString("message") } catch(e: Exception) {}
            if (errorDetail.isBlank()) errorDetail = response.message
            throw Exception("API Error ${response.code}: $errorDetail")
        }
        
        val root = JSONObject(body)
        val candidates = root.optJSONArray("candidates") ?: throw Exception("No candidates returned from AI")
        val contentObj = candidates.getJSONObject(0).optJSONObject("content") ?: throw Exception("AI response blocked. Try another query.")
        val text = contentObj.getJSONArray("parts").getJSONObject(0).getString("text")
        
        val jsonArray = JSONArray(text.trim())
        val results = mutableListOf<PlaceSuggestion>()
        
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val vibesArr = obj.optJSONArray("tags") ?: JSONArray()
            val vibes = mutableListOf<String>()
            for (j in 0 until vibesArr.length()) {
                vibes.add(vibesArr.getString(j))
            }
            results.add(PlaceSuggestion(
                id = UUID.randomUUID().toString(),
                name = obj.optString("name", "Unknown"),
                category = obj.optString("category", "Uncategorized"),
                vibes = vibes,
                rating = obj.optDouble("rating", 4.0),
                priceLevel = obj.optInt("priceLevel", 2),
                durationMinutes = obj.optInt("durationMinutes", 60),
                description = obj.optString("description", ""),
                address = obj.optString("address", "")
            ))
        }
        
        if (results.isEmpty()) throw Exception("No places were generated for your query.")
        return@withContext results
    }
}
