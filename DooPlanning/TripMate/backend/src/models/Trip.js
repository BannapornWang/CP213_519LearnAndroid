const mongoose = require('mongoose');

const BookingSchema = new mongoose.Schema({
  type: { type: String, enum: ['flight', 'hotel', 'activity', 'transport'] },
  provider: String,
  confirmationCode: String,
  details: mongoose.Schema.Types.Mixed,
  dateTime: Date,
  cost: Number
});

const ActivitySchema = new mongoose.Schema({
  placeId: String,
  placeName: String,
  startTime: String,
  endTime: String,
  duration: Number,
  completed: { type: Boolean, default: false },
  notes: String,
  photos: [String]
});

const DayPlanSchema = new mongoose.Schema({
  date: Date,
  activities: [ActivitySchema]
});

const TripSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  name: String,
  destination: {
    name: String,
    country: String,
    coordinates: { lat: Number, lng: Number }
  },
  startDate: Date,
  endDate: Date,
  status: { type: String, enum: ['draft', 'planned', 'ongoing', 'completed'], default: 'planned' },
  travelers: Number,
  vibes: [String],
  budget: {
    total: Number,
    currency: { type: String, default: 'THB' },
    breakdown: {
      transport: Number,
      accommodation: Number,
      food: Number,
      activities: Number,
      misc: Number
    }
  },
  bookings: [BookingSchema],
  itinerary: [DayPlanSchema],
  coverImage: String
}, { timestamps: true });

module.exports = mongoose.model('Trip', TripSchema);
