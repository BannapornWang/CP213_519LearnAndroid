/**
 * Google Places API Integration Mock
 */
async function searchPlaces(query, location, type) {
  return [{ id: 'p1', name: 'Mock Place related to ' + query, location }];
}

async function getPlaceDetails(placeId) {
  return { id: placeId, name: 'Detailed Mock Place', rating: 4.8 };
}

async function getNearbyPlaces(location, radius, type) {
  return [{ id: 'p2', name: 'Nearby Place within ' + radius }];
}

async function getPlacePhotos(placeId) {
  return ['photo_url_1', 'photo_url_2'];
}

module.exports = { searchPlaces, getPlaceDetails, getNearbyPlaces, getPlacePhotos };
