# PokeApp - Master Trainer

[![Build Debug APK](https://github.com/masymasy94/NewPokeApp/actions/workflows/build.yml/badge.svg)](https://github.com/masymasy94/NewPokeApp/actions/workflows/build.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-green)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-green)

Android companion app for the **Pokemon Master Trainer** board game. Helps players manage battles, look up Pokemon stats, and calculate type effectiveness during gameplay.

## Features

- **Pokedex** - Browse all 151 Gen 1 Pokemon with stats, types, and Italian names
- **Trainer Battles (PvP)** - Select Pokemon for player vs player battles with power calculation
- **Gym Leader Battles** - Challenge 13 gym leaders with type effectiveness
- **Random Encounters** - Simulate wild Pokemon encounters from the board game
- **Type Effectiveness** - Full type chart with damage multipliers
- **Evolution System** - Toggle evolution stages with power bonuses

## Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern |
| **DI** | Hilt |
| **Database** | Room |
| **Async** | Kotlin Coroutines + Flow |
| **Navigation** | Navigation Compose |
| **Build** | Gradle (KTS) + KSP |

## Architecture

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── mapper/         # Entity <-> Domain mappers
│   └── repository/     # Repository implementations
├── di/                 # Hilt dependency injection modules
├── domain/
│   ├── model/          # Domain models, enums, battle calculator
│   └── repository/     # Repository interfaces
└── presentation/
    ├── components/     # Reusable Compose components
    ├── navigation/     # Navigation graph and routes
    ├── screen/         # Screens + ViewModels
    └── theme/          # Colors, typography, shapes
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1) or newer
- JDK 17
- Android SDK 34

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/masymasy94/NewPokeApp.git
   ```
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or physical device

### Build

```bash
./gradlew assembleDebug       # Debug APK
./gradlew assembleRelease     # Release APK
```

The debug APK is also built automatically via GitHub Actions on every push to `main` and can be downloaded from the [Actions tab](https://github.com/masymasy94/NewPokeApp/actions).
