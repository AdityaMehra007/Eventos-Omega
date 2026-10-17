# 🎪 Eventos-Omega: AI-Powered Event Operations & Logistics Engine

> **Intelligent Event Operations, Staffing Orchestration & Gemini AI Telemetry for Android**  
> Built with **Kotlin, Jetpack Compose Material 3, Android Room SQLite & Google Gemini AI**  
> Maintained by **Aditya Mehra** | Bengaluru, Karnataka, India  
> **GitHub:** [@AdityaMehra007](https://github.com/AdityaMehra007)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Google Gemini AI](https://img.shields.io/badge/Google%20Gemini-Integrated-8E75C4.svg?logo=google&logoColor=white)](https://ai.google.dev/)
[![Room Database](https://img.shields.io/badge/Room-SQLite%20Offline--First-3DDC84.svg?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)

---

## 📌 Overview

**Eventos-Omega** is an enterprise-grade mobile platform engineered to manage complex, large-scale live events, corporate brand activations, and concert logistics. Born from direct field operations experience across Bengaluru (including Aero India and live stadium concert productions), it blends offline-first operational resilience with Google Gemini AI intelligence for automated staffing and schedule synthesis.

---

## 🚀 Key Architecture & Modules

### 1. 🤖 Gemini AI Operational Copilot (`GeminiService`)
* Dynamic AI assistant providing on-the-fly crowd density assessments, emergency operational protocols, and staffing allocation recommendations.

### 2. 💾 Offline-First Event Persistence (`EventDao` & `EventEntity`)
* Robust Room Database caching event data locally:
  * Expected attendee counts & throughput limits.
  * Security and staff allocation requirements.
  * Budget forecasting in INR (`budgetEstimatedINR`).
  * Time-slot scheduling and zone partition tracking.

### 3. 🎨 Native Jetpack Compose UI (`EventosMainApp`)
* Implements edge-to-edge Material 3 dark surface themes (`DarkSurfaceBase`), reactive event state boards, and live schedule cards.

### 4. 🏢 Real-World Field Proven
* Calibrated for multi-zone venues, vendor dispatching, and high-concurrency event telemetry.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose with Material 3 & Custom Dark Surface Theme
* **Local Persistence:** Android Architecture Components (Room Database, DAOs, Entities)
* **AI Service:** Google Gemini Generative AI SDK
* **Backend:** Firebase App Services (Sync, Telemetry)
* **Build System:** Gradle Kotlin DSL (`build.gradle.kts`)

---

## 🏁 Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/AdityaMehra007/Eventos-Omega.git
cd Eventos-Omega

# 2. Open in Android Studio
# 3. Assemble and launch
./gradlew assembleDebug
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
