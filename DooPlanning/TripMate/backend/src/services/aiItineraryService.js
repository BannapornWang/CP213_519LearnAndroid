/**
 * AI Itinerary Service
 */

async function generateItinerary(tripRequest) {
  const { destination, startDate, endDate, travelers, vibes, budget, preferences } = tripRequest || {};
  
  if (!destination || !startDate || !endDate) {
    throw new Error('Missing required fields');
  }

  // 1. คำนวณจำนวนวัน
  const start = new Date(startDate);
  const end = new Date(endDate);
  const daysCount = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
  
  // 2. แบ่งงบประมาณต่อวัน
  const dailyBudget = budget?.total ? budget.total / daysCount : 0;

  // 3. เลือกสถานที่ตาม vibe และ budget
  // 4. จัดเรียงตาม Smart Route Optimizer
  // 5. คำนวณเวลาที่ใช้ในแต่ละที่ และเพิ่มเวลาเดินทางระหว่างจุด
  // 6. ปรับตาม pace preference
  
  const mockDays = [];
  for (let i = 0; i < daysCount; i++) {
    const currentDate = new Date(start);
    currentDate.setDate(currentDate.getDate() + i);
    
    mockDays.push({
      date: currentDate.toISOString().split('T')[0],
      activities: [
        {
          place: { name: `Highlight in ${destination}`, category: (vibes && vibes[0]) ? vibes[0] : 'Attraction' },
          startTime: '09:00',
          endTime: '11:00',
          duration: 120, // minutes
          travelTimeToNext: 30, // minutes
          estimatedCost: budget?.breakdown?.activities ? budget.breakdown.activities / daysCount : 0,
          category: 'ACTIVITY',
          notes: 'Enjoy the morning vibes'
        }
      ],
      meals: [],
      dayBudget: dailyBudget
    });
  }

  return {
    tripId: `trip_${Date.now()}`,
    days: mockDays,
    totalEstimatedCost: budget?.total || 0,
    route: { mapData: 'mock_route_points' }
  };
}

async function adjustItinerary(itinerary, changes) {
  // - เพิ่ม/ลบ สถานที่
  // - Re-optimize route
  // - Re-calculate budget
  
  return {
    ...itinerary,
    updated: true,
    totalEstimatedCost: itinerary.totalEstimatedCost + (changes.costAdjustment || 0)
  };
}

module.exports = {
  generateItinerary,
  adjustItinerary
};
