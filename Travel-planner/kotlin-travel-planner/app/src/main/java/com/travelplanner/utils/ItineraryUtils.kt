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
import java.io.IOException
import java.util.concurrent.TimeUnit

object ItineraryUtils {

    private const val TAG = "ItineraryUtils"

    // Model fallback chain: try primary first, then backup if primary is unavailable
    // Note: only models available on v1beta are listed here
    private val MODEL_FALLBACK_CHAIN = listOf(
        BuildConfig.GEMINI_MODEL,   // e.g. gemini-2.0-flash-lite (from local.properties)
        "gemini-2.0-flash"          // backup: more stable
    )

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
        .readTimeout(60, TimeUnit.SECONDS)   // increased: AI generation can be slow
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun getGeminiKey(): String = BuildConfig.GEMINI_API_KEY
    private fun getGroqKey(): String = BuildConfig.GROQ_API_KEY

    /** Returns true for errors that are safe to retry (server busy, rate limit, network) */
    private fun isRetryable(e: Exception): Boolean {
        val msg = e.message ?: ""
        return e is IOException
                || msg.contains("503")
                || msg.contains("429")
                || msg.contains("500")
                || msg.contains("high demand", ignoreCase = true)
                || msg.contains("timeout", ignoreCase = true)
    }

    /**
     * Call Groq API (OpenAI-compatible chat completions).
     * Returns the raw text content from the response.
     */
    private fun callGroq(prompt: String, maxTokens: Int = 1200): String {
        val key = getGroqKey()
        if (key.isBlank()) throw Exception("No Groq key configured")

        val url = "https://api.groq.com/openai/v1/chat/completions"
        val body = JSONObject().apply {
            put("model", "llama-3.3-70b-versatile")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("response_format", JSONObject().apply { put("type", "json_object") })
            put("max_tokens", maxTokens)
            put("temperature", 0.5)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $key")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        Log.d(TAG, "[Groq] HTTP ${response.code}")

        if (!response.isSuccessful) {
            var errorMsg = ""
            try { errorMsg = JSONObject(responseBody).getJSONObject("error").getString("message") } catch (e: Exception) {}
            if (errorMsg.isBlank()) errorMsg = response.message
            throw Exception("${response.code}: $errorMsg")
        }

        // Groq wraps the JSON inside a json_object — extract the actual array from "result" or root
        val root = JSONObject(responseBody)
        return root.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }

    /**
     * Call Gemini API.
     * Returns the raw text content from the response.
     */
    private fun callGemini(model: String, prompt: String, maxTokens: Int = 1200): String {
        val key = getGeminiKey()
        if (key.isBlank()) throw Exception("No Gemini key configured")

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key"
        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("maxOutputTokens", maxTokens)
                put("temperature", 0.5)
            })
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        Log.d(TAG, "[Gemini/$model] HTTP ${response.code}")

        if (!response.isSuccessful) {
            var errorMsg = ""
            try { errorMsg = JSONObject(responseBody).getJSONObject("error").getString("message") } catch (e: Exception) {}
            if (errorMsg.isBlank()) errorMsg = response.message
            throw Exception("${response.code}: $errorMsg")
        }

        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates") ?: throw Exception("No candidates from Gemini")
        val contentObj = candidates.getJSONObject(0).optJSONObject("content")
            ?: throw Exception("Gemini response blocked")
        return contentObj.getJSONArray("parts").getJSONObject(0).getString("text")
    }

    /**
     * Try calling with exponential backoff. Returns raw text on success.
     * Throws on exhausted retries.
     */
    private suspend fun callWithRetry(
        tag: String,
        maxRetries: Int = 3,
        call: () -> String
    ): String {
        var retryCount = 0
        var lastException: Exception? = null
        while (retryCount < maxRetries) {
            try {
                return call()
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "[$tag] Attempt ${retryCount + 1} failed: ${e.message}")
                retryCount++
                if (isRetryable(e) && retryCount < maxRetries) {
                    val delayMs = minOf(2000L * (1 shl (retryCount - 1)), 10000L)
                    Log.d(TAG, "[$tag] Waiting ${delayMs}ms before retry...")
                    kotlinx.coroutines.delay(delayMs)
                } else {
                    throw Exception(lastException?.message ?: "$tag failed")
                }
            }
        }
        throw Exception(lastException?.message ?: "$tag exhausted retries")
    }

    suspend fun generateItinerary(destination: String, startDate: String, days: Int, budget: Double, userApiKey: String = ""): List<DayPlan> {
        val prompt = """Return a JSON object with key "days" containing an array for a $days-day trip to $destination, budget ${budget.toInt()} THB.
Each element: {"date":"Day N","places":[{"name":str,"category":str,"description":str,"estimatedCost":int,"durationMinutes":int,"transport":str}]}.
Categories: Attraction|Shopping|Food|Cafe|Nightlife|Nature|Experience|Cultural. No markdown. Return only JSON."""

        return withContext(Dispatchers.IO) {
            var lastException: Exception? = null

            // 1. Try Groq first (faster + higher free quota)
            if (getGroqKey().isNotBlank()) {
                try {
                    Log.d(TAG, "generateItinerary: trying Groq...")
                    val text = callWithRetry("Groq") { callGroq(prompt, maxTokens = 1500) }
                    // Groq with json_object wraps in an object — extract the "days" array
                    val jsonRoot = JSONObject(text)
                    val arrayText = when {
                        jsonRoot.has("days") -> jsonRoot.getJSONArray("days").toString()
                        else -> {
                            // fallback: find first array in the object
                            var found = ""
                            for (key in jsonRoot.keys()) {
                                val v = jsonRoot.opt(key)
                                if (v is JSONArray) { found = v.toString(); break }
                            }
                            found.ifEmpty { throw Exception("Groq returned unexpected structure") }
                        }
                    }
                    return@withContext parseAIResponse(arrayText, destination, startDate, days, budget)
                } catch (e: Exception) {
                    lastException = e
                    Log.w(TAG, "Groq failed, falling back to Gemini: ${e.message}")
                }
            }

            // 2. Fallback: Gemini model chain
            val geminiPrompt = """Return a JSON array for a $days-day trip to $destination, budget ${budget.toInt()} THB.
Each element: {"date":"Day N","places":[{"name":str,"category":str,"description":str,"estimatedCost":int,"durationMinutes":int,"transport":str}]}.
Categories: Attraction|Shopping|Food|Cafe|Nightlife|Nature|Experience|Cultural. No markdown."""

            for (model in MODEL_FALLBACK_CHAIN) {
                try {
                    Log.d(TAG, "generateItinerary: trying Gemini/$model...")
                    val text = callWithRetry("Gemini/$model") { callGemini(model, geminiPrompt, 1200) }
                    return@withContext parseAIResponse(text, destination, startDate, days, budget)
                } catch (e: Exception) {
                    lastException = e
                    Log.w(TAG, "Gemini/$model failed: ${e.message}")
                }
            }

            throw Exception(lastException?.message ?: "All AI providers failed. Please try again later.")
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
        val prompt = """Return a JSON object with key "places" containing an array of 5 travel places for vibe: "$query".
Each: {"name":str,"category":str,"tags":[str],"rating":float,"priceLevel":int,"durationMinutes":int,"description":str,"address":str}. Return only JSON."""

        var lastException: Exception? = null

        // 1. Try Groq first
        if (getGroqKey().isNotBlank()) {
            try {
                val text = callWithRetry("Groq/search", maxRetries = 2) { callGroq(prompt, maxTokens = 700) }
                val jsonRoot = JSONObject(text)
                val arr = when {
                    jsonRoot.has("places") -> jsonRoot.getJSONArray("places")
                    else -> {
                        var found: JSONArray? = null
                        for (key in jsonRoot.keys()) { val v = jsonRoot.opt(key); if (v is JSONArray) { found = v; break } }
                        found ?: throw Exception("Groq returned unexpected structure")
                    }
                }
                val results = parseGroqPlaces(arr)
                if (results.isNotEmpty()) return@withContext results
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "searchPlacesAI Groq failed, falling back to Gemini: ${e.message}")
            }
        }

        // 2. Fallback: Gemini
        val geminiPrompt = """Return a JSON array of 5 travel places for vibe: "$query".
Each: {"name":str,"category":str,"tags":[str],"rating":float,"priceLevel":int,"durationMinutes":int,"description":str,"address":str}. No markdown."""
        val geminiBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", geminiPrompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("maxOutputTokens", 600)
                put("temperature", 0.5)
            })
        }.toString()

        try {
            val text = callWithRetry("Gemini/search", maxRetries = 3) {
                callGemini(BuildConfig.GEMINI_MODEL, geminiPrompt, 600)
            }
            val cleanText = text.trim().let {
                val s = it.indexOf('['); val e = it.lastIndexOf(']')
                if (s != -1 && e != -1) it.substring(s, e + 1) else it
            }
            val results = parseGroqPlaces(JSONArray(cleanText))
            if (results.isNotEmpty()) return@withContext results
        } catch (e: Exception) {
            lastException = e
            Log.e(TAG, "searchPlacesAI Gemini also failed: ${e.message}")
        }

        throw Exception(lastException?.message ?: "Search failed on all providers")
    }

    private fun parseGroqPlaces(jsonArray: JSONArray): List<PlaceSuggestion> {
        val results = mutableListOf<PlaceSuggestion>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val vibesArr = obj.optJSONArray("tags") ?: JSONArray()
            val vibes = mutableListOf<String>()
            for (j in 0 until vibesArr.length()) vibes.add(vibesArr.getString(j))
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
        return results
    }
}
