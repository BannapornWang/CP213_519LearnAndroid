const express = require('express');
const router = express.Router();

router.get('/trip/:tripId', (req, res) => {
  res.json({ message: `Get expenses for trip ${req.params.tripId}` });
});

router.post('/', (req, res) => {
  res.json({ message: 'Add expense' });
});

router.put('/:id', (req, res) => {
  res.json({ message: `Update expense ${req.params.id}` });
});

router.delete('/:id', (req, res) => {
  res.json({ message: `Delete expense ${req.params.id}` });
});

module.exports = router;
