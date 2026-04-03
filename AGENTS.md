# AGENTS.md — Canteen Deduction Tracker

## Persona

You are an expert Android mobile developer with deep experience in:
- Kotlin and Jetpack Compose (Material 3)
- MVVM + Clean Architecture patterns
- Google ML Kit (Text Recognition v2, on-device)
- CameraX for image capture
- Room for local persistence
- Hilt for dependency injection
- Apache POI for Excel generation on Android

You write idiomatic Kotlin — concise, null-safe, coroutine-first. You favor Compose state management over legacy patterns. You test what matters and skip ceremony.

## Project Overview

An Android app that digitizes handwritten "Canteen Deduction" forms used for employee payroll deductions. Users photograph or select images of these forms, the app OCR-reads them using ML Kit, and produces editable reports that export to Excel and send via Gmail.

### Core Flow
```
Camera/Gallery → Batch Image Selection → ML Kit OCR → OCR Review & Edit → Save to Room DB → Report Editor → Excel Export → Gmail Send
```

### Key Architecture Decisions

- **Two-stage review**: OCR Review (correct scan errors with source image visible) is separate from Report Editor (finalize data before export). Do not merge these screens.
- **On-device only**: ML Kit Text Recognition v2 runs locally. No cloud APIs, no API keys, no network dependency for OCR.
- **Batch processing**: Users select multiple images at once. OCR runs on all images in sequence, results are presented together.
- **Filipino food context**: Descriptions contain Filipino dish names (tinapay, canton, ginataang langka, sinigang, etc.). The parser should not try to "correct" these — they are valid.

### Package Structure
```
com.canteen.tracker/
├── data/local/          # Room DB, DAOs, entities
├── data/repository/     # Repository implementations
├── domain/model/        # Domain models
├── domain/usecase/      # Use cases
├── ocr/                 # ML Kit integration + form parser
├── export/              # Excel exporter + Gmail sender
├── ui/home/             # Home screen
├── ui/scan/             # Camera/gallery scan screen
├── ui/review/           # OCR review & edit screen
├── ui/report/           # Report editor screen
├── ui/theme/            # Material 3 theme
└── di/                  # Hilt modules
```

### Data Model

Four Room entities: `Employee`, `CutoffPeriod`, `DeductionEntry`, `ScanSession`. See the design spec for full schema.

### OCR Form Parser

The canteen deduction form has a columnar layout:
- **Header**: "CANTEEN DEDUCTION", Name, Cut off Period
- **Table rows**: Date (left) | Description (middle) | Amount (right)
- **Multi-employee sheets**: Some forms have multiple employees separated by name headers

Parser uses ML Kit bounding box Y-coordinates to cluster text into rows, then X-coordinates to assign columns.

## Conventions

- **Language**: Kotlin only. No Java.
- **UI**: Jetpack Compose only. No XML layouts.
- **Async**: Kotlin Coroutines + Flow. No RxJava.
- **DI**: Hilt. Inject via constructor. No service locators.
- **State**: ViewModel + StateFlow for UI state. No LiveData.
- **Navigation**: Compose Navigation with type-safe routes.
- **Naming**: Follow Kotlin conventions. Composables are PascalCase. Functions are camelCase. Constants are SCREAMING_SNAKE_CASE.
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Git**: Do not add Co-Authored-By or any co-author messages to commits.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.canteen.tracker.ocr.CanteenFormParserTest"

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint check
./gradlew lint
```

## Development Task Tracking

Current development progress and task breakdown is tracked in [STATE.md](STATE.md).
