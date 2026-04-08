const express = require('express');
const router = express.Router();

router.post('/register', (req, res) => {
  res.json({ message: 'User registered successfully' });
});

router.post('/login', (req, res) => {
  res.json({ token: 'mock-jwt-token' });
});

router.post('/refresh-token', (req, res) => {
  res.json({ token: 'new-mock-jwt-token' });
});

module.exports = router;
