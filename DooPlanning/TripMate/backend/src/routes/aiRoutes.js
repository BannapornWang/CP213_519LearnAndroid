const express = require('express');
const router = express.Router();
const aiService = require('../services/aiItineraryService');

router.post('/generate-itinerary', async (req, res) => {
  try {
    const itinerary = await aiService.generateItinerary(req.body);
    res.json(itinerary);
  } catch (error) {
    res.status(500).json({ error: 'Failed to generate itinerary' });
  }
});

router.post('/optimize-route', (req, res) => {
  res.json({ message: 'Route optimized' });
});

router.post('/vibe-search', (req, res) => {
  res.json({ message: 'Vibe search results' });
});

router.post('/nearby-gems', (req, res) => {
  res.json({ message: 'Nearby gems' });
});

module.exports = router;
