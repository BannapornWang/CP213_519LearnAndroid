const mongoose = require('mongoose');

const MemorySchema = new mongoose.Schema({
  tripId: { type: mongoose.Schema.Types.ObjectId, ref: 'Trip', required: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  date: Date,
  note: String,
  photos: [String],
  rating: { type: Number, min: 1, max: 5 },
  location: {
    name: String,
    coordinates: { lat: Number, lng: Number }
  }
}, { timestamps: { createdAt: true, updatedAt: false } });

module.exports = mongoose.model('Memory', MemorySchema);
