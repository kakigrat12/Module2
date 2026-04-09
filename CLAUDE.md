# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

This is a set of Android lab projects (lb1–lb8) for a Java Android development course (Module 2). Each lab is an independent Android Studio project covering different Android topics.

| Lab | Topic |
|-----|-------|
| lb1 | Tween animation (scale, rotate) |
| lb2 | Maps (Google Maps / Yandex / OpenStreetMap) |
| lb3 | App widget with configuration screen |
| lb4 | Menus (options menu + context menu) |
| lb5 | Dialog windows (AlertDialog) |
| lb6 | Notifications with sound |
| lb7 | Audio playback (MediaPlayer) |
| lb8 | SharedPreferences + SQLite database |

## Project Structure

All labs share the same structure — each `lbN/` folder is a standalone Android project:

```
lbN/
├── app/
│   ├── build.gradle.kts          # app-level build config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/lbN/
│       │   └── MainActivity.java  # entry point
│       └── res/
│           ├── anim/              # tween animation XMLs (lb1)
│           ├── layout/
│           │   └── activity_main.xml
│           ├── menu/              # options/context menu XMLs (lb4+)
│           ├── raw/               # audio files (lb6, lb7)
│           └── values/
│               └── themes.xml
├── gradle/libs.versions.toml      # dependency versions (shared pattern)
└── settings.gradle.kts
```

## Build & Run

Each lab is built independently. From inside a lab directory (e.g. `lb1/`):

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

To open in Android Studio: **File → Open** → select the `lbN/` folder.

## Tech Stack

- **Language:** Java (not Kotlin)
- **UI:** XML Views + ConstraintLayout (not Jetpack Compose)
- **Min SDK:** 26, **Target/Compile SDK:** 36
- **Build system:** Gradle 8.13 with Kotlin DSL (`.kts`)
- **AGP:** 8.13.2
- **Theme base:** `Theme.Material3.DayNight.NoActionBar`

## Conventions

- Package names follow `com.example.lbN` pattern
- Theme names follow `Theme.LbN` / `Base.Theme.LbN` pattern
- All labs include a `TextView` at the bottom of the screen with the student's full name: **Зимин Андрей Михайлович**
- Labs 6, 7, 8 require an options menu with an "О программе" item that shows an AlertDialog with the student's name

## Creating a New Lab

When adding lb9+ or recreating a lab:
1. Copy an existing `lbN/` folder (excluding `.gradle/` and `build/` dirs)
2. Replace all occurrences of `lbN` (lowercase) and `LbN` (capitalized) in: `settings.gradle.kts`, `app/build.gradle.kts`, `res/values/themes.xml`, `res/values-night/themes.xml`, `AndroidManifest.xml`, and Java source files
3. Rename the Java source directory from `com/example/lbOLD` to `com/example/lbNEW`

## Common Pitfalls

- **NoActionBar theme:** The default theme uses `NoActionBar`. For labs that need an options menu (lb4, lb6, lb7, lb8), remove `.NoActionBar` from the parent theme name in `themes.xml`
- **`local.properties`** contains the machine-specific SDK path — do not copy between machines
