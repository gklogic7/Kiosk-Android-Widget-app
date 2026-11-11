# Kiosk — Lightweight Tablet System Telemetry Widgets & Retro Desktop Workspace

Kiosk is an ultra-lightweight Android tablet utility that pairs standalone real-time CPU and memory Home Screen widgets with a minimalist, desktop-grade widget canvas and taskbar configuration environment. Inspired by the high-contrast monochrome dot-matrix aesthetic of Nothing OS and switchable to nostalgic Windows XP Luna and Classic skins, Kiosk delivers instant hardware telemetry with near-zero background battery and memory overhead.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product and design choices have been confirmed from your answers and are locked in for implementation:

- **Confirmed Delivery Model**: **Standalone Home Screen Widgets + Minimalist Tablet Desktop & Config App** — Kiosk provides real Android Home Screen widgets (`AppWidgetProvider` with `RemoteViews`) that display live CPU, RAM, and system health directly on the tablet launcher, paired with an in-app tablet desktop workspace where widgets can be dragged, resized, and configured alongside a matching bottom taskbar.
- **Confirmed Theme & Skin Engine**: **Three Switchable Skins** —
  1. **Nothing OS Dot-Matrix (Default)**: Inspired by the provided reference image—featuring circular, pill, and squircle cards in pitch black, matte charcoal, crisp white, and signature crimson red (`#D71921`) accents with dot-matrix typography and glyph matrices.
  2. **Windows XP Luna Blue**: Iconic royal-blue beveled title bars, vibrant green Start button, warm parchment dialog surfaces (`#ECE9D8`), retro pixel icons, and classic Task Manager dark-green LED grid graphs.
  3. **Windows XP Classic (Chiseled Gray)**: Tactile 3D raised/sunken bevel borders (`#D4D0C8`), navy gradient headers, and classic system tray telemetry indicators.
- **Confirmed Telemetry & Desktop Utilities**:
  - Real-time **CPU load & core frequency** monitor + **RAM & swap/heap** usage gauges.
  - **Battery health/temperature, internal storage capacity, and live network throughput (RX/TX)** tiles.
  - **Draggable & resizable desktop widget grid** optimized for landscape and portrait tablet screens.
  - **Lightweight running process & memory breakdown inspector** modal/window.
  - **Taskbar & Start Menu** with retro classic icons, quick system toggles (Wi-Fi, Bluetooth, Auto-Rotate, DND/Bedtime, Display Brightness intents, Refresh Rate governor), and live digital clock.

---

## 1. Overview & Core Concept

- **What It Does**: Kiosk monitors real-time device hardware metrics—CPU utilization, per-core frequencies, RAM allocation, storage capacity, battery thermal/charge state, and network transfer speeds—and presents them both as standalone Android Home Screen widgets and inside a customizable, low-footprint tablet desktop workspace.
- **Target Audience / Persona**: Android tablet power users, developers, and retro-tech enthusiasts who use their tablet as a desk companion or secondary workstation and want immediate visibility into system performance without bloated background services.
- **Key Value**: Combines genuine low-overhead hardware telemetry with two beloved design languages—modern dot-matrix minimalism and nostalgic Windows XP desktop ergonomics—while strictly respecting Android memory and battery budgets.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Tablet Desktop & Widget Canvas Launch**:
   - Opening Kiosk launches directly into an adaptive, multi-column **Tablet Desktop Canvas** anchored by a bottom **Taskbar**.
   - Users see modular telemetry tiles arranged on a snap-to-grid canvas: Circular CPU Load Gauge, Dot-Matrix RAM Usage Card, Dual Pill Toggles (Bluetooth / Network), Multi-Day CPU/Memory History Strip, Storage & Battery Dials, and Clock/Timezone Tile.
2. **Interactive Grid Customization (Drag & Resize)**:
   - Tapping the **Layout Edit / Grid Lock** toggle in the taskbar or long-pressing any tile unlocks the canvas.
   - Users can drag tiles across grid cells and cycle tile footprints between **1×1 Circle**, **2×1 Pill**, **2×2 Squircle**, and **4×2 Wide Telemetry Banner**.
3. **Instant Skin Switching (Nothing OS ↔ Windows XP Luna ↔ Windows XP Classic)**:
   - Selecting the **Skin Switcher** from the Taskbar or Start Menu immediately transforms both the in-app desktop canvas and all pinned Home Screen widgets:
     - In **Nothing OS Dot-Matrix**, tiles render with ultra-clean circular/squircle geometry, dot-matrix numerals, glyph icons, and red accent indicators.
     - In **Windows XP Luna** or **Classic**, tiles morph into windowed mini-dialogs with title bars, minimize/maximize chrome, classic pixel icons (My Computer, Chip, RAM Stick, Network Monitors), and green CRT grid plots, while the bottom bar becomes the unmistakable XP Taskbar with the Start button and system tray.
4. **Process & Memory Breakdown Inspector**:
   - Tapping the RAM/CPU tile or selecting **Task Manager / Process Inspector** from the Start Menu opens a lightweight floating window showing detailed JVM/native heap usage, system memory categories (Available, Cached, Kernel/Used, Threshold), per-core CPU frequency bars, and active app process metrics.
5. **Standalone Home Screen Widget Pinning & Sync**:
   - Users can add **Kiosk CPU & RAM Compact Widget** or **Kiosk Telemetry Deck Widget** directly from their tablet's home screen widget picker (or request pinning from the Start Menu).
   - Changing the active skin, refresh cadence (Eco 5s, Balanced 2s, Real-Time 1s), or visible metrics in the config panel immediately updates all active Home Screen widgets.

### Visual Identity & Theme

- **Aesthetic Direction**: Dual-personality precision engine—**Nothing OS Industrial Dot-Matrix Minimalism** (primary visual inspiration from the provided reference image) paired with authentic **Windows XP Desktop Nostalgia**.
- **Color Palette & Mood**:
  - **Nothing OS Minimal Skin**:
    - `--background`: `#000000` (Pure OLED Black canvas)
    - `--surface-dark`: `#161618` (Matte Charcoal widget card)
    - `--surface-light`: `#F2F2F4` (Crisp Off-White high-contrast card)
    - `--accent-red`: `#D71921` (Signature Nothing Crimson for peak load, active recording/alerts, and status dots)
    - `--text-primary`: `#FFFFFF` on dark cards / `#111111` on light cards
    - `--text-muted`: `#8E8E93` (Technical metadata & axis labels)
  - **Windows XP Luna Blue Skin**:
    - `--background`: `#3A6EA5` / Bliss-inspired atmospheric sky-and-meadow gradient backdrop
    - `--window-surface`: `#ECE9D8` (Warm Luna parchment dialog background)
    - `--titlebar-gradient`: `#0058EE` to `#3593FF` (Beveled royal blue window header)
    - `--taskbar-blue`: `#245DDA` with `#3C9E37` (Vibrant green Start button) and `#0F8AEE` (System tray well)
    - `--graph-crt`: `#000000` background with `#008040` grid lines and `#00FF00` live telemetry trace
  - **Windows XP Classic Skin**:
    - `--background`: `#008080` (Classic Teal desktop)
    - `--window-surface`: `#D4D0C8` with 2dp 3D light (`#FFFFFF`) and shadow (`#808080` / `#404040`) bevels
    - `--titlebar-gradient`: `#0A246A` to `#A6CAF0` (Classic Navy-to-Sky title bar)
- **Typography & Hierarchy**:
  - Bundled locally via Google Fonts (`res/font/`):
    - **Dot-Matrix / Retro Telemetry Display**: `VT323` and `Share Tech Mono` for dotted/segmented digital counters (`22:25`, `34°`, `GMT+1`, CPU `%`, and XP Task Manager counters).
    - **Editorial Contrast Accent**: `Playfair Display` for high-contrast typographic callouts (inspired by the `35 mm` card in the reference image).
    - **Clean UI & Labels**: `IBM Plex Sans` / `Inter` for crisp uppercase tracking (`MON TUE WED`, `AUTO-ROTATE`, `BLUETOOTH`, `TASK MANAGER`).
- **Component Styling & Layout**:
  - Designed specifically for tablet viewports (`Medium` and `Expanded` window size classes) using an adaptive multi-column bento/desktop grid with generous 12dp–16dp spacing in Nothing mode and crisp 2dp beveled frames in XP mode.
- **Interactive Feedback & Motion**:
  - Smooth canvas-rendered telemetry sparklines and dot-matrix glyph animations that redraw only dirty regions without allocating objects per frame.
  - Tactile depressed bevel states on Windows XP buttons and subtle spring scale + ripple feedback on Nothing OS circular/pill toggles.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Low-Overhead Telemetry Sampling Architecture**
  - *Chosen Approach*: Lifecycle-aware coroutine ticker that reads Android's `ActivityManager.MemoryInfo`, `/proc/stat` (with automatic fallback to per-core CPU scaling frequency ratios from `/sys/devices/system/cpu/cpu*/cpufreq/scaling_cur_freq` on Android 8+ restricted kernels), `TrafficStats` (network RX/TX delta), `StatFs` (storage), and `BatteryManager` sticky broadcasts.
  - *Why*: Modern Android restricts raw `/proc/stat` on some devices; combining `/proc/stat` with real-time per-core CPU frequency scaling states (`scaling_cur_freq` vs `scaling_max_freq`) and process memory counters guarantees accurate, real hardware telemetry with zero root permissions and <0.3% CPU overhead.
  - *Alternatives Considered*: Polling heavy shell processes (`top` or `dumpsys`) every second, which causes GC churn and battery drain.
- **Decision 2: Standalone Home Screen Widgets with RemoteViews Canvas Rendering**
  - *Chosen Approach*: Implement standard `AppWidgetProvider` receivers that render crisp, skin-aware widget layouts and custom bitmap dot-matrix/CRT graphs on demand and via configurable update intervals (`Eco`, `Balanced`, `Performance`).
  - *Why*: Ensures compatibility across all Android tablet launchers while allowing both the Nothing OS dot-matrix look and Windows XP Task Manager bevel look to appear faithfully on the user's home screen.
- **Decision 3: Lightweight Preferences Persistence (`SharedPreferences` / `DataStore`)**
  - *Chosen Approach*: Persist active skin selection, widget grid positions/sizes, sampling interval, and temperature unit preferences locally on device.
  - *Why*: Instant synchronous/asynchronous access shared seamlessly between the main tablet desktop activity and the `AppWidgetProvider` receiver without requiring cloud sign-in or heavy database overhead.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                     ANDROID SYSTEM & HARDWARE TELEMETRY                      │
│  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────────┐  │
│  │ CPU Core Freqs &   │  │ ActivityManager    │  │ BatteryManager, StatFs │  │
│  │ /proc/stat Sampler │  │ MemoryInfo & Heap  │  │ & TrafficStats RX/TX   │  │
│  └─────────┬──────────┘  └─────────┬──────────┘  └───────────┬────────────┘  │
└────────────┼───────────────────────┼─────────────────────────┼───────────────┘
             │                       │                         │
             ▼                       ▼                         ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                   LowOverheadTelemetryEngine (Singleton)                     │
│  • Zero-allocation ring buffers (60-sample history for CPU, RAM, Net)        │
│  • Adaptive polling cadence (1s Active / 3s Eco / Paused on screen off)      │
│  • Preferences Store (Active Skin, Grid Layout JSON, Polling Mode, Units)    │
└────────────────────────┬─────────────────────────────────┬───────────────────┘
                         │                                 │
                         ▼                                 ▼
┌─────────────────────────────────────────┐ ┌──────────────────────────────────┐
│    Kiosk Tablet Desktop (Compose UI)    │ │ Standalone Home Screen Widgets   │
│ ┌─────────────────────────────────────┐ │ │        (AppWidgetProvider)       │
│ │ Adaptive Draggable & Resizable Grid │ │ │ ┌──────────────────────────────┐ │
│ │ • Circular CPU & Core Dial Tile     │ │ │ │ Compact CPU/RAM Gauge Widget │ │
│ │ • Dot-Matrix / CRT RAM Graph Tile   │ │ │ └──────────────────────────────┘ │
│ │ • Weekly Load Dot-Matrix Strip      │ │ │ ┌──────────────────────────────┐ │
│ │ • Storage, Battery & Network Tiles  │ │ │ │ Wide Desktop Telemetry Deck  │ │
│ │ • Quick System Toggle Pills/Circles │ │ │ │ (Skin-Synced Nothing / XP)   │ │
│ └─────────────────────────────────────┘ │ └──────────────────────────────────┘
│ ┌─────────────────────────────────────┐ │
│ │ Floating Process & Memory Inspector │ │
│ │ • Heap/RAM Breakdown & Core Bars    │ │
│ └─────────────────────────────────────┘ │
│ ┌─────────────────────────────────────┐ │
│ │ Skin-Adaptive Bottom Taskbar        │ │
│ │ • Start Menu Launcher & Skin Picker │ │
│ │ • Quick Toggles, Tray & Live Clock  │ │
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### Data Model & State

- **`TelemetrySnapshot`**: Immutable snapshot holding `cpuLoadPercent`, `perCoreFrequenciesMhz` (list of current/max MHz per core), `ramUsedMb`, `ramTotalMb`, `ramAvailableMb`, `ramThresholdMb`, `jvmHeapUsedMb`, `storageUsedGb`, `storageTotalGb`, `batteryPercent`, `batteryTempCelsius`, `isCharging`, `networkRxKbps`, `networkTxKbps`, and 60-point circular history arrays for CPU and RAM.
- **`KioskSkin`**: Enum (`NOTHING_DOT_MATRIX`, `WIN_XP_LUNA`, `WIN_XP_CLASSIC`) controlling color tokens, border bevels, typography, icon sets, and widget RemoteViews layouts.
- **`DesktopWidgetItem`**: Represents a tile on the tablet canvas with `id`, `type` (`CPU_GAUGE`, `RAM_GRAPH`, `LOAD_TIMELINE`, `BATTERY_THERMAL`, `STORAGE_DISK`, `NETWORK_IO`, `QUICK_TOGGLES`, `CLOCK_GMT`, `EDITORIAL_METRIC`), `gridSpan` (`1x1`, `2x1`, `2x2`, `4x2`), `orderIndex`, and `isVisible`.

### Interactive Component & State Mapping

- **Draggable & Resizable Grid Interactions**:
  - Toggle **Edit Grid Mode** → Enables reorder buttons, drag handles, span size cycler (`1×1 Circle` → `2×1 Pill` → `2×2 Squircle` → `4×2 Banner`), and tile visibility switches. Changes persist immediately and recalculate the adaptive tablet grid.
- **Skin & Theme Switcher**:
  - Selecting **Nothing OS**, **Windows XP Luna**, or **Windows XP Classic** in the Taskbar Start Menu or Theme Drawer updates `KioskSkin` state, animates the in-app theme transition, and dispatches an `ACTION_APPWIDGET_UPDATE` broadcast so all pinned Home Screen widgets redraw in the new skin immediately.
- **Quick System Toggles (Circle & Pill Controls)**:
  - Interactive tiles for **Wi-Fi Settings**, **Bluetooth Settings**, **Auto-Rotate Toggle / Display Settings**, **Bedtime / Do Not Disturb Settings**, and **Eco Sampling Mode** execute safe Android system panel intents or update internal Kiosk governor states with instant visual feedback.
- **Process & Memory Breakdown Inspector**:
  - Triggered from the Taskbar or by tapping the RAM/CPU tile; displays real-time breakdown of Device RAM (Used, Cached/Free, Low-Memory Threshold), Kiosk's own ultra-low memory footprint (proving its lightweight optimization), and live per-core CPU frequency bars with a "Trigger GC / Trim Memory" optimization action.
- **Start Menu & Taskbar Controls**:
  - Clicking the **Start / Kiosk Button** opens a skin-accurate popup menu (Windows XP two-column Start Menu with user banner and classic icons in XP skins; sleek dot-matrix command deck in Nothing skin) providing one-tap access to Widget Pinning, Process Inspector, Skin Selector, Polling Rate Governor, and Grid Reset.
