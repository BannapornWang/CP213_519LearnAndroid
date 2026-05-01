# Travel Planner — Kotlin Native Android

Full-featured Android travel journal app built with Kotlin, Room, and Material Design 3. Powered by advanced AI for seamless trip planning.

## 🌟 Features
- **Dual-AI Itinerary Generator** — Uses **Groq (Llama 3.3)** as primary and **Google Gemini** as fallback to auto-build day-by-day plans from budget + days.
- **Smart Route Optimizer** — Intelligent logic to reorder places for efficient travel.
- **AI-Powered Vibe Search** — Discover hidden gems by "vibe" (e.g., "jazz bar", "quiet cafe", "rooftop scenic") using natural language.
- **Real-time Budget Tracker** — Visualized expense tracking by category with animated progress bars and currency formatting.
- **Full Trip Journal** — Record bookings, expenses, star ratings, and mood tags for every journey.
- **Nearby Gems** — Curated place suggestions based on location and categories.

## 🛠 Tech Stack
| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.1 |
| **AI Providers** | Groq (Llama 3.3), Google Gemini (2.0 Flash) |
| **Networking** | OkHttp 4.12, Coroutines + Flow |
| **Database** | Room 2.6 (SQLite) |
| **Serialization** | Gson (JSON mapping) |
| **Architecture** | MVVM + Repository Pattern |
| **UI** | Material Design 3, ViewBinding |
| **Build** | Gradle 8.7, Version Catalog |

## ⚙️ Configuration

The app requires API keys for AI functionality. To run the project locally:

1.  **Get API Keys**:
    *   [Groq API Key](https://console.groq.com/) (Primary)
    *   [Google Gemini API Key](https://aistudio.google.com/) (Fallback)
2.  **Setup `local.properties`**:
    Open `local.properties` at the root of the project and add:
    ```properties
    GROQ_API_KEY=your_groq_api_key_here
    GEMINI_API_KEY=your_gemini_api_key_here
    GEMINI_MODEL=gemini-2.0-flash-lite
    ```

## 🚀 Getting Started

1.  **Clone / Open** in Android Studio Koala (2024.1.1) or later.
2.  **Sync Gradle** — dependencies will be fetched automatically.
3.  **Run** on a physical device or emulator (minSdk 26 / Android 8.0+).

### Build from Terminal
```bash
./gradlew assembleDebug
# APK Location: app/build/outputs/apk/debug/app-debug.apk
```

## 📂 Project Structure

```text
app/src/main/
├── java/com/travelplanner/
│   ├── data/
│   │   ├── Models.kt          ← Trip, DayPlan, Expense, Booking schemas
│   │   ├── TripDao.kt         ← Room Persistence
│   │   └── TripRepository.kt  ← Data orchestration
│   ├── ui/
│   │   ├── MainActivity.kt       ← Dashboard & trip filters
│   │   ├── CreateTripActivity.kt ← AI-powered trip creation
│   │   ├── TripDetailActivity.kt ← Tabs: Itinerary, Expenses, Bookings
│   │   ├── ExploreFragment.kt    ← AI Vibe Search
│   │   └── TripViewModel.kt      ← Core state & AI logic
│   └── utils/
│       ├── ItineraryUtils.kt  ← Groq/Gemini API integration & logic
│       └── BudgetUtils.kt     ← Calculations & formatting
└── res/
    ├── layout/               ← MD3 XML layouts
    └── values/               ← Themes, Colors, and Strings
```

## 📸 Screenshots
*(Add screenshots or wireframe reference here)*
Refer to `wireframe_Travel_Planning.png` in the root directory for the design vision.
