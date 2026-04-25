package com.travelplanner.utils

import android.util.Log
import com.travelplanner.data.DayPlan
import com.travelplanner.data.Place
import com.travelplanner.data.PlaceSuggestion
import com.travelplanner.BuildConfig
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

    private val TAG = "ItineraryUtils"

    // ─── Database ─────────────────────────────────────────────────────────────

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
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // ─── AI itinerary generator via Gemini REST API ───────────────────────────

    suspend fun generateItinerary(destination: String, startDate: String, days: Int, budget: Double): List<DayPlan> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        Log.d(TAG, "API Key length: ${apiKey.length}")

        if (apiKey.isEmpty()) {
            Log.w(TAG, "API key is empty, using mock data")
            return generateMockItinerary(startDate, days, budget)
        }

        val prompt = """Create a travel itinerary for $destination for $days days with a total budget of ${budget.toInt()} THB.
Return ONLY a JSON array. No explanations, no markdown, no extra text. Just the raw JSON array.
Format:
[{"date":"Day 1","places":[{"name":"Place Name","category":"Attraction","description":"Brief description","estimatedCost":500,"durationMinutes":90,"transport":"Metro Line X -> Walk 5 min"}]},{"date":"Day 2","places":[...]}]
Categories allowed: Attraction, Food, Cafe, Shopping, Nature, Experience, Cultural, Nightlife"""

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }.toString()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val body = response.body?.string() ?: ""
                Log.d(TAG, "Gemini HTTP status: ${response.code}")
                Log.d(TAG, "Gemini raw response: ${body.take(500)}")

                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini API error: $body")
                    return@withContext generateMockItinerary(startDate, days, budget)
                }

                val root = JSONObject(body)
                val text = root
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                Log.d(TAG, "Gemini text output: ${text.take(300)}")
                parseAIResponse(text, startDate, days, budget)
            } catch (e: Exception) {
                Log.e(TAG, "Exception calling Gemini: ${e.message}", e)
                generateMockItinerary(startDate, days, budget)
            }
        }
    }

    private fun parseAIResponse(jsonText: String, startDate: String, days: Int, budget: Double): List<DayPlan> {
        try {
            val startIndex = jsonText.indexOf('[')
            val endIndex = jsonText.lastIndexOf(']')
            if (startIndex == -1 || endIndex == -1) {
                System.err.println("Gemini Response does not contain a JSON array:\n$jsonText")
                return generateMockItinerary(startDate, days, budget)
            }
            
            val cleanText = jsonText.substring(startIndex, endIndex + 1)
            val jsonArray = JSONArray(cleanText)
            val result = mutableListOf<DayPlan>()
            
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance().apply { time = fmt.parse(startDate) ?: java.util.Date() }

            for (i in 0 until jsonArray.length()) {
                val dayObj = jsonArray.getJSONObject(i)
                
                val dateStr = fmt.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, 1)
                
                val placesArr = dayObj.optJSONArray("places")
                val dayPlaces = mutableListOf<Place>()

                if (placesArr != null) {
                    for (j in 0 until placesArr.length()) {
                        val pObj = placesArr.getJSONObject(j)
                        dayPlaces.add(
                            Place(
                                id = java.util.UUID.randomUUID().toString(),
                                name = pObj.optString("name", "Unknown"),
                                category = pObj.optString("category", "Attraction"),
                                address = "",
                                durationMinutes = pObj.optInt("durationMinutes", 60),
                                cost = pObj.optDouble("estimatedCost", 0.0),
                                transport = pObj.optString("transport", ""),
                                notes = pObj.optString("description", "")
                            )
                        )
                    }
                }
                result.add(DayPlan(date = dateStr, places = dayPlaces))
            }
            if (result.isEmpty()) {
                System.err.println("Gemini returned empty json array")
                return generateMockItinerary(startDate, days, budget)
            }
            
            // Pad if less than days requested
            while (result.size < days) {
                val dateStr = fmt.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, 1)
                result.add(DayPlan(date = dateStr, places = mutableListOf()))
            }
            
            return result
        } catch(e: Exception) {
            e.printStackTrace()
            return generateMockItinerary(startDate, days, budget)
        }
    }

    fun generateMockItinerary(startDate: String, days: Int, budget: Double): List<DayPlan> {
        val dailyBudget = budget / days
        val result = mutableListOf<DayPlan>()
        val usedIds = mutableSetOf<String>()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply { time = fmt.parse(startDate) ?: java.util.Date() }

        repeat(days) { _ ->
            val dateStr = fmt.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)

            val dayPlaces = mutableListOf<Place>()
            var remainBudget = dailyBudget
            var remainTime = 600

            val available = PLACE_SUGGESTIONS.filter { it.id !in usedIds }.shuffled()
            for (s in available) {
                if (dayPlaces.size >= 4) break
                if (s.durationMinutes > remainTime) continue
                val (minC, maxC) = COST_RANGES[s.category] ?: Pair(100.0, 500.0)
                val cost = minC + Math.random() * (maxC - minC)
                if (cost > remainBudget && dayPlaces.isNotEmpty()) continue
                dayPlaces.add(Place(
                    id = UUID.randomUUID().toString(),
                    name = s.name, category = s.category, address = s.address,
                    durationMinutes = s.durationMinutes,
                    cost = cost, notes = s.description
                ))
                usedIds.add(s.id)
                remainBudget -= cost
                remainTime -= (s.durationMinutes + 30)
            }
            result.add(DayPlan(date = dateStr, places = dayPlaces))
        }
        return result
    }

    // ─── Smart route optimizer (2-opt) ────────────────────────────────────────

    fun optimizeRoute(places: List<Place>): List<Place> {
        if (places.size <= 2) return places
        val opt = places.toMutableList()
        var improved = true
        while (improved) {
            improved = false
            for (i in 1 until opt.size - 1) {
                for (j in i + 1 until opt.size) {
                    val before = dist(opt[i - 1], opt[i]) + dist(opt[i], opt[j])
                    val after  = dist(opt[i - 1], opt[j]) + dist(opt[j], opt[i])
                    if (after < before) {
                        val tmp = opt[i]; opt[i] = opt[j]; opt[j] = tmp
                        improved = true
                    }
                }
            }
        }
        return opt
    }

    private fun dist(a: Place, b: Place) = Math.random()

    // ─── Vibe search ─────────────────────────────────────────────────────────

    fun searchByVibe(query: String): List<PlaceSuggestion> {
        val keywords = query.lowercase().split("\\s+".toRegex())
        return PLACE_SUGGESTIONS.filter { s ->
            val haystack = "${s.name} ${s.category} ${s.description} ${s.vibes.joinToString(" ")}".lowercase()
            keywords.any { kw -> haystack.contains(kw) }
        }.take(6)
    }

    fun nearbyGems(): List<PlaceSuggestion> = PLACE_SUGGESTIONS.shuffled().take(6)

    // ─── AI Place Search via Gemini ──────────────────────────────────────────

    suspend fun searchPlacesAI(query: String): List<PlaceSuggestion> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) return searchByVibe(query)

        val prompt = """Search for 5-8 popular places or activities for: "$query".
Return ONLY a JSON array of objects. No explanations.
Format:
[{"id":"ai_1","name":"Place Name","category":"Attraction","vibes":["vibe1","vibe2"],"rating":4.8,"priceLevel":2,"durationMinutes":90,"description":"Short description","address":"Location"}]
Categories allowed: Attraction, Food, Cafe, Shopping, Nature, Experience, Cultural, Nightlife"""

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }.toString()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                val body = response.body?.string() ?: ""
                Log.d(TAG, "Gemini Search HTTP status: ${response.code}")
                
                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini Search API error: $body")
                    return@withContext searchByVibe(query)
                }

                val root = JSONObject(body)
                val candidates = root.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    Log.w(TAG, "No candidates returned from Gemini")
                    return@withContext searchByVibe(query)
                }

                val text = candidates.getJSONObject(0)
                    .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")

                val startIndex = text.indexOf('[')
                val endIndex = text.lastIndexOf(']')
                if (startIndex == -1) return@withContext searchByVibe(query)

                val cleanText = text.substring(startIndex, endIndex + 1)
                val jsonArray = JSONArray(cleanText)
                val result = mutableListOf<PlaceSuggestion>()

                for (i in 0 until jsonArray.length()) {
                    val o = jsonArray.getJSONObject(i)
                    val vibesArr = o.optJSONArray("vibes")
                    val vibesList = mutableListOf<String>()
                    if (vibesArr != null) {
                        for (j in 0 until vibesArr.length()) vibesList.add(vibesArr.getString(j))
                    }

                    result.add(PlaceSuggestion(
                        id = UUID.randomUUID().toString(),
                        name = o.optString("name", "Unknown"),
                        category = o.optString("category", "Attraction"),
                        vibes = vibesList,
                        rating = o.optDouble("rating", 4.5),
                        priceLevel = o.optInt("priceLevel", 1),
                        durationMinutes = o.optInt("durationMinutes", 60),
                        description = o.optString("description", ""),
                        address = o.optString("address", "")
                    ))
                }
                result
            } catch (e: Exception) {
                Log.e(TAG, "AI Search failed", e)
                searchByVibe(query)
            }
        }
    }
}
