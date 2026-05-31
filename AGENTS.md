# Agent Instructions

This document provides guidelines and conventions to help AI coding agents be productive in the `mtrschedule` codebase, specifically focusing on UI layout and design patterns.

## UI Layout and Design Conventions

### 1. Architecture
- **Pattern:** MVVM (Model-View-ViewModel).
- **UI Binding:** The project exclusively uses traditional XML layouts combined with **ViewBinding**. Jetpack Compose is **not** used.
- **State Management:** ViewModels (e.g., `TrainScheduleViewModel`) are used to hold UI state and interact with the data layer.

### 2. Layout Strategies
- **ConstraintLayout:** The primary layout manager for complex screen arrangements (e.g., `activity_main.xml`), flattening the view hierarchy for better performance.
- **Lists:** Data representation relies heavily on `RecyclerView` with multiple custom item layouts (e.g., `item_station.xml`, `item_train.xml`, `item_route.xml`).
- **Cards:** `CardView` is used extensively for list items to provide elevation and corner radius (`12dp`), distinguishing them from the background.
- **Refresh/Navigation:** Use `SwipeRefreshLayout` for pull-to-refresh functionality and `DrawerLayout` for side navigation.

### 3. Design System & Theming
- **Material Components:** The app is built on Material Design Components (`Theme.MaterialComponents.Light.NoActionBar`).
- **Color Palette:**
  - **Primary:** Pastel Pink (`#F4A6C1`) and `#E091B0`.
  - **Backgrounds:** Very light grey/blue (`#F7F7FA`) with stark white (`#FFFFFFFF`) for Cards.
  - **Text Colors:** Hierarchical (primary `#1F1F24`, secondary `#5C5D68`, tertiary `#9A9BA6`).
- **Loading States:** The project uses skeleton screens for loading states (e.g., `item_skeleton_station.xml`).

When creating or modifying UI components, always adhere to these established patterns. Use ViewBinding and XML layouts, and ensure consistency with the existing Material Design theme and color palette.
