/**
 * OpenWeather API Integration Mock
 */
async function getCurrentWeather(location) {
  return { temp: 32, condition: 'Sunny', location };
}

async function getForecast(location, days) {
  return Array(days).fill(null).map((_, i) => ({
    day: i + 1, temp: 30 + i, condition: 'Clear'
  }));
}

module.exports = { getCurrentWeather, getForecast };
