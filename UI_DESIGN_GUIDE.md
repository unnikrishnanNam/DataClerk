# Data Clerk - UI/UX Design Guide

## 🎨 Design Principles

1. **Dark Theme First**: All screens use dark backgrounds for reduced eye strain
2. **Depth Through Shadows**: Elevated surfaces cast subtle shadows
3. **Monochromatic Base**: Black/gray scale with accent colors for highlights
4. **Smooth Transitions**: Animations enhance user experience
5. **Clear Hierarchy**: Important elements are larger and more prominent

## 📱 Screen Designs

### 1. Splash Screen

```
┌─────────────────────────┐
│                         │
│                         │
│      [Animated Logo]    │  <- Pulsing blue database icon
│         ⚡ 120dp         │
│                         │
│     Data Clerk          │  <- Display Small, Bold
│                         │
│  Your Database          │  <- Body Large
│    Assistant            │
│                         │
│     ◯ ◯ ◯ ◯ ◯          │  <- Pulsing indicator
│                         │
│  Connecting to          │  <- Body Medium, gray
│     backend...          │
│                         │
└─────────────────────────┘

Colors:
- Background: #0A0A0A
- Logo: #6B9EFF (pulsing)
- Title: #E8E8E8
- Subtitle: #B0B0B0
- Status: #808080
```

### 2. Home Screen

```
┌─────────────────────────┐
│ ☰  Data Clerk          │  <- Top bar
│    Your Database        │
│    Assistant            │
├─────────────────────────┤
│                         │
│ Current Database        │  <- Small label
│ ┌─────────────────────┐│
│ │ 🗄️  testdb      ▼  ││  <- Dropdown selector
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ testdb          [UP]││  <- Database card
│ │                     ││
│ │ Tables: 12          ││
│ │ Health: Excellent   ││
│ │ Last: 2 mins ago    ││
│ └─────────────────────┘│
│                         │
│ Quick Actions           │
│ ┌──────┬──────┬──────┐│
│ │📊 View│➕ New │🕐 His│││  <- Pill buttons
│ │Schema│ Chat │tory  ││
│ └──────┴──────┴──────┘│
│                         │
│ Recent Conversations    │
│ ┌─────────────────────┐│
│ │ 💬 Product sales    ││  <- Chat item
│ │    analysis         ││
│ │    Show me top 5... ││
│ │    8 messages    →  ││
│ └─────────────────────┘│
│ ┌─────────────────────┐│
│ │ 💬 Customer insights││
│ │    How many active..││
│ │    5 messages    →  ││
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │  💬 Chat with DB    ││  <- Primary CTA
│ └─────────────────────┘│
│                         │
└─────────────────────────┘

Drawer (when opened):
┌──────────────┐
│ 🗄️ Data Clerk│
│    v1.0.0    │
├──────────────┤
│ ┌──────────┐│
│ │ 👤 Busine││  <- User profile
│ │    Owner  ││
│ │ owner@... ││
│ └──────────┘│
│              │
│ ⚙️  Settings │
│ ℹ️  About    │
│ ❓ Help      │
│              │
│ © 2025 Data  │
│   Clerk      │
└──────────────┘
```

### 3. Chat Screen

```
┌─────────────────────────┐
│ ← Chat                  │  <- Header
│   🗄️ testdb             │
│                    🗑️ ⋮  │
├─────────────────────────┤
│                         │
│ ┌─────────────────┐    │
│ │🤖 Hello! I'm    │    │  <- Assistant
│ │   your database │    │     message
│ │   assistant...  │    │
│ └─────────────────┘    │
│                         │
│    ┌────────────────┐  │
│    │ Show me top 5 ││   │  <- User
│    │ products      ││   │     message
│    └────────────────┘👤│
│                         │
│ ┌─────────────────┐    │
│ │🤖 Here are the  │    │
│ │   top products: │    │
│ │                 │    │
│ │ ┌─────────────┐│    │
│ │ │Query: SELECT│││     │  <- Metadata
│ │ │5 rows, 23ms ││    │
│ │ └─────────────┘│    │
│ └─────────────────┘    │
│                         │
├─────────────────────────┤
│ ┌─────────────────┐ [↗]│  <- Input bar
│ │ Ask about your  │    │
│ │ data...         │    │
│ └─────────────────┘    │
└─────────────────────────┘

Message Types:
- Assistant (left): Gray bubble, robot icon
- User (right): Blue bubble, person icon
- Metadata: Darker inset with query info
```

### 4. Schema Viewer Screen

```
┌─────────────────────────┐
│ ← Database Schema    🔄 │  <- Header
│   🗄️ testdb             │
├─────────────────────────┤
│ ┌─────────────────────┐│
│ │ 🔍 Search tables... ││  <- Search bar
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ 📊 users        ▼   ││  <- Collapsed
│ │    5 columns        ││     table
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ 📊 products     ▲   ││  <- Expanded
│ │    5 columns        ││     table
│ ├─────────────────────┤│
│ │ → id               ││
│ │   integer, PK      ││
│ │ → name             ││
│ │   text, NOT NULL   ││
│ │ → price            ││
│ │   decimal          ││
│ │ → stock            ││
│ │   integer          ││
│ │ → category_id      ││
│ │   integer, FK      ││
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ 📊 orders       ▼   ││
│ │    5 columns        ││
│ └─────────────────────┘│
│                         │
└─────────────────────────┘

Interactions:
- Tap table to expand/collapse
- Search filters in real-time
- Scroll through all tables
```

### 5. Settings Screen

```
┌─────────────────────────┐
│ ← Settings              │
├─────────────────────────┤
│                         │
│ API Configuration       │
│ ┌─────────────────────┐│
│ │ Backend URL         ││
│ │ ┌─────────────────┐││
│ │ │http://localhost ││││  <- Text input
│ │ │:8090/api        ││││
│ │ └─────────────────┘││
│ │ API endpoint for... ││
│ └─────────────────────┘│
│                         │
│ App Behavior            │
│ ┌─────────────────────┐│
│ │ 📳 Enable Haptics   ││
│ │ Vibration feedback  ││
│ │                 [✓] ││  <- Toggle
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ ✨ Animations       ││
│ │ Smooth transitions  ││
│ │                 [✓] ││
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ 🔄 Auto-refresh     ││
│ │ Auto refresh data   ││
│ │                 [✓] ││
│ └─────────────────────┘│
│                         │
│ About                   │
│ ┌─────────────────────┐│
│ │ Version:       1.0.0││
│ │ Build:     20251112 ││
│ └─────────────────────┘│
│                         │
└─────────────────────────┘
```

### 6. Error Screen

```
┌─────────────────────────┐
│                         │
│                         │
│      ┌─────────┐        │
│      │         │        │  <- Pulsing red
│      │   ☁️❌  │        │     cloud icon
│      │         │        │
│      └─────────┘        │
│                         │
│  Connection Failed      │  <- Headline
│                         │
│  Unable to connect to   │  <- Body text
│  the backend server.    │
│  Please check your      │
│  connection and try     │
│  again.                 │
│                         │
│ ┌─────────────────────┐│
│ │  🔄 Retry Connection││  <- CTA button
│ └─────────────────────┘│
│                         │
│ ┌─────────────────────┐│
│ │ Troubleshooting:    ││  <- Tips card
│ │                     ││
│ │ • Check if backend  ││
│ │   server is running ││
│ │ • Verify network    ││
│ │ • Check API URL     ││
│ └─────────────────────┘│
│                         │
└─────────────────────────┘
```

## 🎨 Component Styles

### Elevation Levels

```
Level 0: Background      (#0A0A0A) - No shadow
Level 1: Surface         (#141414) - 2dp shadow
Level 2: Elevated        (#242424) - 4dp shadow
Level 3: Dialog/Modal    (#2A2A2A) - 8dp shadow
```

### Corner Radius

```
Small:    8dp  - Chips, badges
Medium:   12dp - Cards, inputs
Large:    16dp - Major cards
Pill:     28dp - Buttons, full-round
Circle:   50%  - Icons, avatars
```

### Spacing Scale

```
4dp  - Tight (icon-to-text)
8dp  - Small (between related items)
12dp - Medium (between card content)
16dp - Default (card padding)
20dp - Large (screen padding)
24dp - XLarge (section spacing)
32dp - XXLarge (major sections)
```

### Icon Sizes

```
16dp - Inline icons
20dp - Button icons
24dp - Standard icons
32dp - Large icons
40dp - Avatar/Feature icons
48dp - Touch targets
```

### Typography Usage

```
Display Small:   App title (Splash)
Headline Medium: Screen titles
Title Large:     Card headers
Title Medium:    List item titles
Body Large:      Primary content
Body Medium:     Secondary content
Body Small:      Helper text
Label Large:     Button text
```

## 🎭 Animation Patterns

### Fade In

- Duration: 300ms
- Used for: Content appearing
- Curve: FastOutSlowIn

### Scale

- Range: 0.95 → 1.05
- Duration: 2000ms
- Used for: Logo, icons
- Mode: Reverse (ping-pong)

### Shimmer

- Duration: 1500ms
- Used for: Loading states
- Direction: Left → Right

### Slide

- Duration: 250ms
- Used for: Navigation
- Curve: FastOutSlowIn

## 💡 Interaction Patterns

### Tap

- Shows ripple effect
- Material color with 12% opacity
- Centered on touch point

### Long Press

- Triggers haptic feedback
- Shows contextual menu
- 500ms delay

### Swipe

- 50dp threshold
- Shows reveal action
- Animates back on cancel

### Pull to Refresh

- 80dp trigger distance
- Shows loading indicator
- Haptic on trigger

## 🎯 Accessibility

### Touch Targets

- Minimum: 48x48dp
- Buttons: 56dp height
- Icons: 48dp touch area

### Contrast Ratios

- Title text: 14:1 (white on black)
- Body text: 8:1 (light gray)
- Disabled: 3:1 (dark gray)
- All meet WCAG AA standards

### Content Descriptions

- All icons have descriptions
- Interactive elements labeled
- Screen reader optimized

---

**Remember**: The design is deliberately minimalist and modern. Every shadow, color, and animation has a purpose. Maintain consistency across all screens!
