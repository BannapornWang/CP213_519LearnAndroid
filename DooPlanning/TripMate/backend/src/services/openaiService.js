/**
 * OpenAI API Integration Mock
 */
async function generateItinerarySuggestions(prompt) {
  return {
    suggestion: "Based on prompt, here is a mock itinerary suggestion.",
    parsedConfidence: 0.95
  };
}

async function parseVibeQuery(query) {
  return {
    extractedVibes: ['Chill', 'Nature'],
    originalQuery: query
  };
}

async function summarizeTrip(tripData) {
  return "A wonderful 3-day trip to mock destination.";
}

module.exports = { generateItinerarySuggestions, parseVibeQuery, summarizeTrip };
