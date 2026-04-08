/**
 * Vibe Search Service
 */

const VIBE_CATEGORIES = {
  'ผ่อนคลาย': ["คาเฟ่เงียบ", "สปา", "สวน", "ชายหาด"],
  'ผจญภัย': ["hiking", "water sports", "extreme"],
  'โรแมนติก': ["fine dining", "sunset spots", "rooftop"],
  'ครอบครัว': ["สวนสนุก", "zoo", "museum", "kid-friendly"],
  'ถ่ายรูปสวย': ["instagrammable", "scenic", "art gallery"],
  'กินเที่ยว': ["street food", "local cuisine", "food market"]
};

function getVibeCategories() {
  return VIBE_CATEGORIES;
}

async function searchByVibe(query, location, filters) {
  // Parse natural language query
  // Match with place database
  // Rank by relevance
  
  return {
    results: [
      {
        place: { name: "Mock Place A", type: "Cafe" },
        vibeMatch: 95,
        matchedVibes: ["คาเฟ่เงียบ", "ผ่อนคลาย"],
        highlights: ["Good coffee", "Quiet zone"]
      },
      {
        place: { name: "Mock Place B", type: "Park" },
        vibeMatch: 80,
        matchedVibes: ["สวน", "ผ่อนคลาย"],
        highlights: ["Shady trees", "Breeze"]
      }
    ],
    suggestedVibes: ["ถ่ายรูปสวย", "กินเที่ยว"]
  };
}

async function analyzeVibeFromReviews(reviews) {
  // NLP Extraction Mock
  return [
    { tag: "Romantic", confidence: 0.92 },
    { tag: "Quiet", confidence: 0.85 }
  ];
}

module.exports = {
  searchByVibe,
  getVibeCategories,
  analyzeVibeFromReviews
};
