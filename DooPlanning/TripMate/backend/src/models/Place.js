const mongoose = require('mongoose');

const PlaceSchema = new mongoose.Schema({
  googlePlaceId: String,
  name: String,
  description: String,
  category: String,
  vibes: [String],
  location: {
    lat: Number,
    lng: Number,
    address: String
  },
  openingHours: mongoose.Schema.Types.Mixed,
  priceLevel: { type: Number, min: 1, max: 4 },
  rating: Number,
  reviewCount: Number,
  photos: [String],
  amenities: [String]
}, { timestamps: { createdAt: false, updatedAt: true } });

module.exports = mongoose.model('Place', PlaceSchema);
