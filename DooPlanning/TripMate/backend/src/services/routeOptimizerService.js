/**
 * Route Optimizer Service
 */

function calculateTravelTime(from, to, mode = 'driving') {
  // Mock function to estimate travel time
  return Math.floor(Math.random() * 30) + 15; // 15-45 minutes
}

async function optimizeRoute(places, startPoint, constraints) {
  // TSP Approximation Mock
  // 1. Consider opening hours
  // 2. Include lunch break
  // 3. Calculate travel time
  
  if (!places || places.length === 0) return null;
  
  const optimizedOrder = [...places].sort((a, b) => b.priority - a.priority);
  
  let currentTime = new Date();
  if (constraints && constraints.startTime) {
    const [hours, mins] = constraints.startTime.split(':');
    currentTime.setHours(hours, mins, 0, 0);
  } else {
    currentTime.setHours(9, 0, 0, 0);
  }

  const timeline = [];
  let totalDistance = 0;
  let totalTravelTime = 0;

  for (let i = 0; i < optimizedOrder.length; i++) {
    const place = optimizedOrder[i];
    const travelTime = calculateTravelTime(startPoint, place.location, 'driving'); // min
    
    totalTravelTime += travelTime;
    totalDistance += travelTime * 0.5; // Mock distance (km)
    
    const arrivalTime = new Date(currentTime.getTime() + travelTime * 60000);
    const departureTime = new Date(arrivalTime.getTime() + (place.suggestedDuration || 60) * 60000);
    
    timeline.push({
      place,
      arrivalTime: arrivalTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      departureTime: departureTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      travelToNext: {
        distance: travelTime * 0.5,
        duration: travelTime,
        mode: 'driving'
      }
    });
    
    currentTime = departureTime;
  }

  return {
    optimizedOrder,
    totalDistance,
    totalTravelTime,
    timeline,
    savings: {
      distanceSaved: 5.2,
      timeSaved: 25
    }
  };
}

async function suggestGapFillers(currentRoute, availableTime, location) {
  // Mock Gap Fillers
  return [
    { name: "Local Cafe", fitScore: 95 },
    { name: "Small Park", fitScore: 88 }
  ];
}

module.exports = {
  optimizeRoute,
  calculateTravelTime,
  suggestGapFillers
};
