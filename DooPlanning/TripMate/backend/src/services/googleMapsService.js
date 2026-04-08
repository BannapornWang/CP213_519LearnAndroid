/**
 * Google Maps API Integration Mock
 */
async function getDirections(origin, destination, mode) {
  return { route: 'encoded_polyline_mock', distance: 5.2, duration: 15 };
}

async function calculateDistance(points) {
  return [ { from: points[0], to: points[1], distance: 10 } ];
}

async function getStaticMapImage(markers, route) {
  return 'https://maps.googleapis.com/maps/api/staticmap?mock=true';
}

module.exports = { getDirections, calculateDistance, getStaticMapImage };
