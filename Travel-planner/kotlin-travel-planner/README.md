# Travel Planner — Kotlin Native Android

Full-featured Android travel journal app built with Kotlin, Room, and Material Design 3.

## Features
- **AI Itinerary Generator** — auto-builds day-by-day plans from budget + days
- **Smart Route Optimizer** — reorders places for efficient travel
- **Real-time Budget Tracker** — by category with animated progress bars
- **Vibe Search** — find places by feel ("jazz bar", "quiet cafe", "rooftop scenic")
- **Nearby Gems** — curated place suggestions
- **Record Trip** — journal, bookings, expenses, star rating, mood tags

## Tech Stack
| Layer | Tech |
|---|---|
| Language | Kotlin 2.1 |
| Database | Room 2.6 (SQLite) |
| Serialization | Gson (JSON for nested models) |
| Architecture | MVVM + Repository + ViewModel |
| UI | Material Design 3, ViewBinding |
| Async | Coroutines + Flow |
| Build | Gradle 8.7, Version Catalog |

## Configuration

This app uses Google's Gemini API for the **AI Itinerary Generator**. To run the project locally, you must provide your own API key.

1. Get an API key from [Google AI Studio](https://aistudio.google.com/)
2. Open `local.properties` at the root of the project (create it if missing)
3. Add the following line:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

## How to build

1. **Clone / open** in Android Studio Koala or later
2. **Sync Gradle** — all dependencies download automatically
3. **Run** on emulator or device (minSdk 26 = Android 8.0)

```bash
# Optional: build from terminal
./gradlew assembleDebug
# APK → app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/src/main/
├── java/com/travelplanner/
│   ├── data/
│   │   ├── Models.kt          ← Trip, DayPlan, Expense, Booking, PlaceSuggestion
│   │   ├── Converters.kt      ← Gson Type converters for Room
│   │   ├── TripDao.kt         ← Room DAO
│   │   ├── AppDatabase.kt     ← Room singleton
│   │   └── TripRepository.kt  ← Single source of truth
│   ├── ui/
│   │   ├── MainActivity.kt       ← Trip list + filter chips
│   │   ├── CreateTripActivity.kt ← New trip form + AI itinerary
│   │   ├── TripDetailActivity.kt ← 4-tab detail view
│   │   ├── ExploreFragment.kt    ← Vibe search + nearby gems
│   │   ├── TripViewModel.kt      ← Shared ViewModel
│   │   └── adapters/
│   │       ├── TripAdapter.kt
│   │       ├── PlaceAdapter.kt
│   │       ├── ExpenseAdapter.kt
│   │       └── SuggestionAdapter.kt
│   └── utils/
│       ├── BudgetUtils.kt     ← Currency formatting, date helpers
│       └── ItineraryUtils.kt  ← Generator, route optimizer, vibe search
└── res/
    ├── layout/               ← All XML layouts
    ├── drawable/             ← Shape drawables
    └── values/               ← colors, strings, themes, arrays
```
