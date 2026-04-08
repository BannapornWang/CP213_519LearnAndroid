const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  name: { type: String, required: true },
  avatar: String,
  preferences: {
    defaultCurrency: { type: String, default: 'THB' },
    language: { type: String, default: 'th' },
    vibes: [String]
  }
}, { timestamps: true });

module.exports = mongoose.model('User', UserSchema);
