const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
  res.json({ message: 'Get all user trips' });
});

router.get('/:id', (req, res) => {
  res.json({ message: `Get trip ${req.params.id}` });
});

router.post('/', (req, res) => {
  res.json({ message: 'Create new trip' });
});

router.put('/:id', (req, res) => {
  res.json({ message: `Update trip ${req.params.id}` });
});

router.delete('/:id', (req, res) => {
  res.json({ message: `Delete trip ${req.params.id}` });
});

module.exports = router;
