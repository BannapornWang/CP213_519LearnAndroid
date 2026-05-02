# Travel Planner — Kotlin Native Android

แอปพลิเคชันสำหรับวางแผนการท่องเที่ยวและจดบันทึกการเดินทาง (Travel Journal) ที่พัฒนาด้วยภาษา Kotlin โดยใช้พลังของ AI ในการช่วยออกแบบแผนการเดินทางที่ชาญฉลาดและเหมาะสมกับคุณ

---

##  เกี่ยวกับโปรเจ็ค (Project Overview)
**Travel Planner** เป็นแอป Android ที่ช่วยให้การวางแผนเที่ยวเป็นเรื่องง่ายและสนุกยิ่งขึ้น ผู้ใช้สามารถระบุจุดหมายปลายทาง งบประมาณ และจำนวนวันที่ต้องการไป แล้วให้ AI ช่วยสร้างแผนการเดินทางรายวัน (Day-by-day Itinerary) ให้โดยอัตโนมัติ พร้อมทั้งมีระบบจัดการงบประมาณและบันทึกความประทับใจหลังจบการเดินทาง

##  เทคโนโลยีที่ใช้ (Tech Stack)
โปรเจ็คนี้พัฒนาโดยใช้เทคโนโลยีที่ทันสมัยและเป็นมาตรฐานของ Android Development:

| ส่วนงาน | เทคโนโลยีที่ใช้ |
|---|---|
| **Language** | [Kotlin 2.1](https://kotlinlang.org/) |
| **UI Framework** | [Material Design 3](https://m3.material.io/) (ViewBinding & XML Layouts) |
| **Architecture** | MVVM + Repository Pattern |
| **AI Integration** | **Groq (Llama 3.3)** เป็นตัวหลัก และ **Google Gemini (2.0 Flash)** เป็นตัวสำรอง |
| **Local Database** | [Room Persistence Library](https://developer.android.com/training/data-storage/room) (SQLite) |
| **Networking** | OkHttp 4.12, Coroutines & Flow |
| **Serialization** | Gson (สำหรับจัดการ JSON จาก AI) |
| **Build System** | Gradle 8.7 (Kotlin DSL) |

##  ฟีเจอร์หลัก (Key Features)
- ** Dual-AI Itinerary Generator** — ระบบสร้างแผนการเดินทางอัตโนมัติโดยใช้ Groq (Llama 3.3) เป็นหลัก และสลับไปใช้ Gemini หากระบบหลักไม่พร้อมใช้งาน
- ** AI-Powered Vibe Search** — ค้นหาสถานที่ท่องเที่ยวตาม "บรรยากาศ" (Vibe) เช่น "คาเฟ่เงียบๆ", "บาร์แจ๊สบนดาดฟ้า" หรือ "ที่ถ่ายรูปสวยๆ"
- **  Smart Route Optimizer** — ระบบจัดลำดับสถานที่ในแต่ละวันให้อัตโนมัติ เพื่อการเดินทางที่รวดเร็วและประหยัดเวลาที่สุด
- **   Real-time Budget Tracker** — บันทึกและติดตามค่าใช้จ่ายแยกตามหมวดหมู่ (อาหาร, เดินทาง, ที่พัก, กิจกรรม) พร้อมกราฟแสดงสถานะงบประมาณที่เหลือ
- **  Trip Journal & Records** — บันทึกรายละเอียดการจอง (Bookings), สถานะการเช็คอิน, ให้คะแนนความพึงพอใจ และบันทึกอารมณ์ (Mood) ในแต่ละทริป
- **  Nearby Gems** — แนะนำสถานที่ท่องเที่ยวใกล้เคียงที่น่าสนใจตามหมวดหมู่ต่างๆ

---

## การตั้งค่า (Configuration)

แอปนี้จำเป็นต้องใช้ API Key สำหรับการใช้งานส่วน AI กรุณาตั้งค่าในไฟล์ `local.properties`:

1.  **รับ API Keys**:
    *   [Groq API Key](https://console.groq.com/) (ตัวหลัก)
    *   [Google Gemini API Key](https://aistudio.google.com/) (ตัวสำรอง)
2.  **เพิ่มใน `local.properties`**:
    ```properties
    GROQ_API_KEY=your_groq_api_key_here
    GEMINI_API_KEY=your_gemini_api_key_here
    GEMINI_MODEL=gemini-2.0-flash-lite
    ```

## 🚀การเริ่มต้นใช้งาน (Getting Started)

1.  **Clone / เปิดโปรเจ็ค** ใน Android Studio Koala (2024.1.1) หรือเวอร์ชันที่ใหม่กว่า
2.  **Sync Gradle** — ระบบจะดาวน์โหลด dependencies ที่จำเป็นโดยอัตโนมัติ
3.  **Run** บนอุปกรณ์จริงหรือ Emulator (รองรับตั้งแต่ Android 8.0+ / API 26 ขึ้นไป)

### การ Build ผ่าน Terminal
```bash
./gradlew assembleDebug
# ไฟล์ APK จะอยู่ที่: app/build/outputs/apk/debug/app-debug.apk
```

## 📂 โครงสร้างโปรเจ็ค (Project Structure)

```text
app/src/main/
├── java/com/travelplanner/
│   ├── data/
│   │   ├── Models.kt          ← โครงสร้างข้อมูล Trip, DayPlan, Expense
│   │   ├── TripDao.kt         ← การจัดการ Database ด้วย Room
│   │   └── TripRepository.kt  ← ตัวกลางจัดการข้อมูล (Data orchestration)
│   ├── ui/
│   │   ├── MainActivity.kt       ← หน้าหลักและตัวกรองทริป
│   │   ├── CreateTripActivity.kt ← หน้าสร้างทริปด้วย AI
│   │   ├── TripDetailActivity.kt ← รายละเอียดทริป (Itinerary, Expenses, Record)
│   │   ├── ExploreFragment.kt    ← ค้นหาสถานที่ด้วย AI Vibe Search
│   │   └── TripViewModel.kt      ← จัดการ Logic และ State ของแอป
│   └── utils/
│       ├── ItineraryUtils.kt  ← การเชื่อมต่อกับ AI (Groq/Gemini)
│       └── BudgetUtils.kt     ← การคำนวณและจัดการรูปแบบงบประมาณ
└── res/
    ├── layout/               ← ไฟล์ Layout (XML) แบบ Material Design 3
    └── values/               ← ไฟล์กำหนด Themes, Colors และ Strings
```

##  ตัวอย่างหน้าตาแอป (Screenshots)
สามารถดูตัวอย่างการออกแบบได้ที่ไฟล์ `wireframe_Travel_Planning.png` ในโฟลเดอร์หลักของโปรเจ็ค

