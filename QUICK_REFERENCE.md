# Data Clerk - Quick Reference Guide

## 📁 Project Structure

```
app/src/main/java/com/unnikrishnan/dataclerk/
├── MainActivity.kt                      # App entry point
├── data/
│   └── models/
│       ├── ChatModels.kt               # Chat-related data classes
│       ├── CommonModels.kt             # Shared models (Routes, UiState, Settings)
│       └── DatabaseModels.kt           # Database-related data classes
├── navigation/
│   └── AppNavigation.kt                # Navigation graph
├── ui/
│   ├── components/
│   │   ├── Buttons.kt                  # Reusable button components
│   │   ├── Cards.kt                    # Reusable card components
│   │   └── LoadingComponents.kt        # Loading indicators
│   ├── screens/
│   │   ├── chat/
│   │   │   └── ChatScreen.kt           # Chat interface
│   │   ├── error/
│   │   │   └── ErrorScreen.kt          # Error page with retry
│   │   ├── home/
│   │   │   └── HomeScreen.kt           # Main dashboard
│   │   ├── schema/
│   │   │   └── SchemaViewerScreen.kt   # Database schema viewer
│   │   ├── settings/
│   │   │   └── SettingsScreen.kt       # Settings page
│   │   └── splash/
│   │       └── SplashScreen.kt         # Splash screen
│   └── theme/
│       ├── Color.kt                    # Color palette
│       ├── Theme.kt                    # Material theme configuration
│       └── Type.kt                     # Typography system
```

## 🎨 Design System

### Colors

```kotlin
// Primary surfaces
BackgroundDark       = #0A0A0A
SurfaceDark          = #141414
SurfaceElevated1     = #242424
SurfaceElevated2     = #2A2A2A

// Text
TextPrimary          = #E8E8E8
TextSecondary        = #B0B0B0
TextTertiary         = #808080

// Accents
AccentPrimary        = #6B9EFF (Blue)
AccentSecondary      = #7C4DFF (Purple)
AccentSuccess        = #4CAF50 (Green)
AccentWarning        = #FFA726 (Orange)
AccentError          = #EF5350 (Red)
AccentInfo           = #29B6F6 (Light Blue)
```

### Component Usage

#### Buttons

```kotlin
// Primary action
PillButton(
    text = "Chat with Database",
    onClick = { },
    icon = Icons.Default.Chat,
    backgroundColor = AccentPrimary
)

// Quick action pill
IconPillButton(
    text = "View Schema",
    icon = Icons.Default.TableChart,
    onClick = { }
)
```

#### Cards

```kotlin
// Database info
DatabaseCard(
    databaseInfo = DatabaseInfo(...),
    onClick = { }
)

// Status indicator
StatusBadge(status = DatabaseStatus.UP)
```

#### Loading

```kotlin
// Pulsing loader
PulsingLoadingIndicator()

// Shimmer effect
ShimmerBox(height = 20)
```

## 🧭 Navigation Routes

```kotlin
Routes.SPLASH              // "splash"
Routes.HOME                // "home"
Routes.CHAT                // "chat"
Routes.SETTINGS            // "settings"
Routes.SCHEMA_VIEWER       // "schema_viewer"
Routes.ERROR               // "error"

// With parameters
"chat/{databaseName}"
"schema_viewer/{databaseName}"
```

## 📊 Data Models

### Database Models

```kotlin
DatabaseInfo(
    name: String,
    status: DatabaseStatus,
    tableCount: Int,
    health: String,
    lastUpdated: String
)

TableSchema(
    tableName: String,
    columns: List<Triple<String, String, String>>
    // Triple = (columnName, dataType, constraints)
)
```

### Chat Models

```kotlin
ChatMessage(
    id: String,
    content: String,
    role: MessageRole,        // USER or ASSISTANT
    timestamp: Long,
    contentType: MessageContentType,
    metadata: MessageMetadata?
)

MessageContentType:
- TEXT       // Markdown text
- TABLE      // Tabular data
- CHART      // Chart/graph
- CODE       // Code snippet
- ERROR      // Error message
```

## 🔌 API Integration (TODO)

### Required Endpoints

```kotlin
// GET /databases
// Returns: List<String>

// GET /database/{dbname}/health
// Returns: DatabaseHealth

// GET /database/{dbname}/schema
// Returns: List<SchemaInfo>

// POST /database/execute
// Body: ExecuteQueryRequest
// Returns: QueryResult
```

### Retrofit Service (to be created)

```kotlin
interface DataClerkApiService {
    @GET("databases")
    suspend fun getDatabases(): List<String>

    @GET("database/{dbname}/health")
    suspend fun getDatabaseHealth(
        @Path("dbname") dbname: String
    ): DatabaseHealth

    @GET("database/{dbname}/schema")
    suspend fun getDatabaseSchema(
        @Path("dbname") dbname: String
    ): List<SchemaInfo>

    @POST("database/execute")
    suspend fun executeQuery(
        @Body request: ExecuteQueryRequest
    ): QueryResult
}
```

## 🎯 Key Features

### Splash Screen

- ⏱️ Animated logo (scale + fade)
- 📡 Backend connection check
- 🔄 Auto-navigation on success/error

### Home Screen

- 🗄️ Database selector dropdown
- 📊 Database health & info card
- ⚡ Quick action buttons
- 💬 Recent chats list
- 🍔 Navigation drawer

### Chat Screen

- 💬 Real-time chat interface
- 👤 User/Assistant message bubbles
- 📊 Message metadata support
- 🎨 Empty state UI
- ⬆️ Auto-scroll to latest

### Schema Viewer

- 🔍 Search tables/columns
- 📂 Expandable table cards
- 📋 Column details view
- ⚡ Fast filtering

### Settings

- 🌐 Backend URL config
- 🎮 Haptics toggle
- ✨ Animations toggle
- 🔄 Auto-refresh toggle
- ℹ️ Version info

## 🚀 Running the App

### Prerequisites

- Android Studio (latest)
- JDK 11 or higher
- Android SDK 27+ (API 27)

### Steps

1. Open project in Android Studio
2. Wait for Gradle sync
3. Run on emulator or device
4. App starts at Splash Screen

### Testing Navigation

1. **Splash** → Auto-navigates to Home after 3s
2. **Home** → Tap "Chat with Database" → Chat Screen
3. **Home** → Tap "View Schema" → Schema Viewer
4. **Home** → Drawer icon → Opens drawer
5. **Drawer** → Settings → Settings Screen

## 📝 Mock Data Locations

Replace these with real API calls:

1. **HomeScreen.kt** (lines 35-70)

   - databases list
   - databaseInfo
   - recentChats

2. **SplashScreen.kt** (lines 60-80)

   - Backend connection check

3. **SchemaViewerScreen.kt** (lines 25-55)

   - tables with columns

4. **ChatScreen.kt** (lines 75-90)
   - Mock assistant responses

## 🎨 Customization

### Change Primary Color

```kotlin
// In Color.kt
val AccentPrimary = Color(0xFFYOURCOLOR)
```

### Add New Screen

1. Create screen file in `ui/screens/`
2. Add route in `CommonModels.kt`
3. Add composable in `AppNavigation.kt`
4. Navigate from existing screen

### Add New Component

1. Create in `ui/components/`
2. Follow existing patterns
3. Use theme colors and typography
4. Add shadow elevation for depth

## 🐛 Common Issues

### Build Errors

- Ensure Gradle sync completed
- Check JDK version (11+)
- Invalidate caches & restart

### Navigation Issues

- Check route strings match
- Verify parameter passing
- Check back stack management

### Theme Not Applying

- Verify imports from `ui.theme`
- Check MaterialTheme wrapping
- Ensure Activity extends ComponentActivity

## 📚 Resources

- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Material 3 Guidelines](https://m3.material.io/)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Retrofit](https://square.github.io/retrofit/)

---

**Status**: ✅ Phase 1 Complete (UI Foundation)
**Next**: 🔄 Phase 2 (Backend Integration)
