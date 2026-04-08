const express = require('express');
const router = express.Router();

router.get('/search', (req, res) => {
  res.json({ message: 'Search places' });
});

router.get('/trending', (req, res) => {
  res.json({ message: 'Get trending destinations' });
});

router.get('/details/:placeId', (req, res) => {
  res.json({ message: `Get details for place ${req.params.placeId}` });
});

module.exports = router;
