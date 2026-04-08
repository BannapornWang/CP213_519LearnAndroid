const mongoose = require('mongoose');

const ExpenseSchema = new mongoose.Schema({
  tripId: { type: mongoose.Schema.Types.ObjectId, ref: 'Trip', required: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  date: Date,
  description: String,
  amount: Number,
  currency: { type: String, default: 'THB' },
  category: { 
    type: String, 
    enum: ['transport', 'food', 'accommodation', 'activity', 'shopping', 'other'] 
  },
  receipt: String
}, { timestamps: { createdAt: true, updatedAt: false } });

module.exports = mongoose.model('Expense', ExpenseSchema);
