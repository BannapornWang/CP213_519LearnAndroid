/**
 * AI Itinerary Service
 */
const { GoogleGenAI, Type, Schema } = require('@google/genai');

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

async function generateItinerary(tripRequest) {
  const { destination, startDate, endDate, travelers, vibes, budget, preferences } = tripRequest || {};
  
  if (!destination) {
    throw new Error('Missing required fields (destination)');
  }

  // Provide defaults if dates are missing from Android frontend
  const start = startDate ? new Date(startDate) : new Date();
  const end = endDate ? new Date(endDate) : new Date(start.getTime() + 3 * 24 * 60 * 60 * 1000); // default 3 days later
  const daysCount = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;

  const prompt = `
Create a detailed ${daysCount}-day itinerary for ${destination}.
Travelers: ${travelers || 1} people.
Vibe/Style: ${vibes ? vibes.join(', ') : 'Popular Highlights'}.
Budget: ${budget?.level || 'Moderate'}.

IMPORTANT: You must include specific travel methods and transit recommendations between locations, including arriving from the airport into the city (e.g. Maglev Train, Metro Line X, Taxi, Walking).

Return the itinerary strictly in JSON format matching this schema:
{
  "tripId": "string",
  "totalEstimatedCost": "number (amount in local currency)",
  "airportInfo": {
    "airportCode": "string",
    "transitOptions": [
      {
        "mode": "string (e.g., Maglev Train)",
        "duration": "string (e.g., 8 mins)",
        "notes": "string (e.g., Fastest, takes you to Longyang Road)"
      }
    ]
  },
  "days": [
    {
      "date": "YYYY-MM-DD",
      "dayTitle": "string",
      "activities": [
        {
          "place": {
            "name": "string",
            "category": "string"
          },
          "startTime": "HH:MM",
          "endTime": "HH:MM",
          "transitFromPrevious": {
            "mode": "string (e.g., Metro Line 2, Walk)",
            "durationMinutes": "number",
            "instructions": "string (e.g., Take Line 2 to Jing'an Temple)"
          },
          "notes": "string"
        }
      ]
    }
  ]
}
`;

  try {
    const response = await ai.models.generateContent({
      model: 'gemini-2.5-flash',
      contents: prompt,
      config: {
        responseMimeType: "application/json",
      }
    });

    const itineraryJson = JSON.parse(response.text);
    return itineraryJson;
  } catch (error) {
    console.error('Error generating itinerary from Gemini:', error);
    // Fallback or rethrow
    throw new Error('Failed to generate itinerary. Please check API key or prompt.');
  }
}

async function adjustItinerary(itinerary, changes) {
  // To be implemented using Gemini if needed
  return {
    ...itinerary,
    updated: true
  };
}

module.exports = {
  generateItinerary,
  adjustItinerary
};

