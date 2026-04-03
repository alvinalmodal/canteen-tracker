# STATE.md — Development Task Breakdown

## Current Phase: Phase 4 — UI Screens (next)

---

## Phase 1: Project Setup
- [x] Initialize git repository
- [x] Create .gitignore
- [x] Create CLAUDE.md, AGENTS.md, STATE.md
- [x] Scaffold Android project (Gradle, manifest, package structure)
- [x] Configure dependencies (ML Kit, Room, Hilt, CameraX, Apache POI, Compose)
- [x] Set up Hilt application class
- [x] Create Material 3 theme
- [x] Set up Compose Navigation shell

## Phase 2: Data Layer
- [x] Define Room entities (Employee, CutoffPeriod, DeductionEntry, ScanSession)
- [x] Create DAOs with queries (insert, query by cutoff period, aggregate totals)
- [x] Create Room database class
- [x] Implement repository interfaces and implementations
- [ ] Write unit tests for DAOs

## Phase 3: OCR Engine
- [x] Implement ML Kit Text Recognition wrapper
- [x] Build CanteenFormParser (header detection, row parsing, name detection)
- [x] Handle multi-employee sheets (section break detection)
- [x] Handle amount extraction and total detection
- [x] Write unit tests with sample OCR output
- [ ] Integration test against sample_images/

## Phase 4: UI Screens
- [ ] Home Screen (scan session list, FAB for new scan)
- [ ] Scan Screen (CameraX capture + gallery multi-select)
- [ ] OCR Review & Edit Screen (side-by-side image + parsed data, inline editing)
- [ ] Report Editor Screen (full edit table, live totals, add/delete rows)
- [ ] History Detail Screen (view past periods, re-export)
- [ ] Wire up Compose Navigation between all screens

## Phase 5: Export & Share
- [ ] Excel export with Apache POI (Summary + Details sheets)
- [ ] FileProvider setup for sharing files
- [ ] Gmail intent with pre-filled subject/body and Excel attachment
- [ ] Export progress indicator

## Phase 6: Polish
- [ ] Loading states and progress indicators during OCR batch processing
- [ ] Error handling (invalid images, OCR failures, empty results)
- [ ] Empty states for home screen
- [ ] Edge cases (rotated images, partial forms, blurry text)
- [ ] Final testing against all sample images end-to-end
