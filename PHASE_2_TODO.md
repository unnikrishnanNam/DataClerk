# Phase 2: Backend Integration - TODO List

## 🔧 Setup & Architecture

### 1. Create Repository Layer

- [ ] Create `data/repository/DatabaseRepository.kt`
  - Database operations
  - Schema fetching
  - Query execution
- [ ] Create `data/repository/ChatRepository.kt`
  - Chat management
  - Message history
  - LLM integration

### 2. Create API Service

- [ ] Create `data/api/DataClerkApiService.kt`
  - Define all endpoint interfaces
  - Add proper annotations
- [ ] Create `data/api/RetrofitClient.kt`
  - Configure Retrofit instance
  - Add interceptors (logging, auth)
  - Base URL configuration

### 3. Create ViewModels

- [ ] `SplashViewModel.kt`
  - Load databases on startup
  - Check backend connection
  - Handle navigation events
- [ ] `HomeViewModel.kt`
  - Manage database list
  - Load database info
  - Fetch recent chats
  - Handle database selection
- [ ] `ChatViewModel.kt`
  - Send/receive messages
  - Execute queries via LLM
  - Format responses
  - Handle errors
- [ ] `SchemaViewModel.kt`
  - Load database schema
  - Filter tables/columns
  - Expand/collapse state
- [ ] `SettingsViewModel.kt`
  - Save/load settings
  - Update preferences

## 📡 API Integration

### 1. Implement API Endpoints

#### GET /databases

```kotlin
@GET("databases")
suspend fun getDatabases(): Response<List<String>>
```

- [ ] Implement call in repository
- [ ] Update HomeViewModel
- [ ] Update UI to show real data
- [ ] Handle errors

#### GET /database/{dbname}/health

```kotlin
@GET("database/{dbname}/health")
suspend fun getDatabaseHealth(
    @Path("dbname") dbname: String
): Response<DatabaseHealth>
```

- [ ] Implement call in repository
- [ ] Update HomeViewModel
- [ ] Update database card UI
- [ ] Handle DOWN status

#### GET /database/{dbname}/schema

```kotlin
@GET("database/{dbname}/schema")
suspend fun getDatabaseSchema(
    @Path("dbname") dbname: String
): Response<List<SchemaInfo>>
```

- [ ] Implement call in repository
- [ ] Group by table in repository
- [ ] Update SchemaViewModel
- [ ] Update SchemaViewer UI

#### POST /database/execute

```kotlin
@POST("database/execute")
suspend fun executeQuery(
    @Body request: ExecuteQueryRequest
): Response<QueryResult>
```

- [ ] Implement call in repository
- [ ] Integrate with ChatViewModel
- [ ] Parse and format results
- [ ] Handle query errors

### 2. Error Handling

- [ ] Create custom exceptions
- [ ] Network error handling
- [ ] API error response parsing
- [ ] User-friendly error messages
- [ ] Retry mechanisms

## 🎨 UI Updates

### 1. Replace Mock Data

- [ ] Remove hardcoded databases in HomeScreen
- [ ] Remove mock DatabaseInfo
- [ ] Remove mock RecentChat list
- [ ] Remove mock TableSchema data
- [ ] Remove mock chat responses

### 2. Add Loading States

- [ ] Show loading in SplashScreen
- [ ] Add shimmer to HomeScreen cards
- [ ] Add loading to database selector
- [ ] Add loading to chat messages
- [ ] Add loading to schema viewer

### 3. Add Error States

- [ ] Handle API errors in each screen
- [ ] Show error messages with retry
- [ ] Add offline state handling
- [ ] Add timeout handling

### 4. Improve Chat UI

- [ ] Add typing indicator
- [ ] Add message timestamps
- [ ] Add copy message functionality
- [ ] Add query result tables
- [ ] Add chart/graph rendering
- [ ] Add markdown rendering

## 💾 Local Storage

### 1. SharedPreferences

- [ ] Save selected database
- [ ] Save API base URL
- [ ] Save app settings
- [ ] Save user preferences

### 2. Room Database (Optional)

- [ ] Cache recent chats
- [ ] Cache schemas
- [ ] Offline support
- [ ] Query history

## 🔐 Security & Performance

### 1. Security

- [ ] Add API authentication
- [ ] Secure storage for credentials
- [ ] Input validation
- [ ] SQL injection prevention

### 2. Performance

- [ ] Add request caching
- [ ] Implement pagination
- [ ] Optimize large result sets
- [ ] Add debouncing to search
- [ ] Lazy loading for lists

## 🧪 Testing

### 1. Unit Tests

- [ ] Repository tests
- [ ] ViewModel tests
- [ ] Model tests
- [ ] Utility function tests

### 2. Integration Tests

- [ ] API service tests
- [ ] Navigation tests
- [ ] End-to-end flows

### 3. UI Tests

- [ ] Screen composition tests
- [ ] Interaction tests
- [ ] Navigation tests

## 📱 Advanced Features

### 1. Chat Enhancements

- [ ] Message search
- [ ] Export chat history
- [ ] Voice input
- [ ] Save favorite queries
- [ ] Query templates

### 2. Schema Features

- [ ] Export schema
- [ ] Schema comparison
- [ ] ER diagram generation
- [ ] Table relationships

### 3. Analytics

- [ ] Query performance tracking
- [ ] Usage statistics
- [ ] Error tracking
- [ ] User behavior analytics

### 4. Notifications

- [ ] Query completion alerts
- [ ] Database health alerts
- [ ] Background sync

## 🎯 Priority Tasks (Start Here)

### High Priority

1. ✅ Create RetrofitClient.kt
2. ✅ Create DataClerkApiService.kt
3. ✅ Create DatabaseRepository.kt
4. ✅ Create HomeViewModel.kt
5. ✅ Update HomeScreen to use ViewModel
6. ✅ Test database fetching

### Medium Priority

7. ✅ Create ChatRepository.kt
8. ✅ Create ChatViewModel.kt
9. ✅ Implement query execution
10. ✅ Update ChatScreen to use ViewModel

### Low Priority

11. ⏱️ Add caching
12. ⏱️ Implement offline support
13. ⏱️ Add advanced features

## 📋 Implementation Order

### Week 1: Core Setup

- Day 1-2: Retrofit + API service + Repositories
- Day 3-4: ViewModels for Home & Splash
- Day 5: Connect Home screen to backend

### Week 2: Chat Integration

- Day 1-2: ChatViewModel + Repository
- Day 3-4: Query execution & formatting
- Day 5: Chat UI improvements

### Week 3: Schema & Settings

- Day 1-2: Schema loading & display
- Day 3: Settings persistence
- Day 4-5: Polish & bug fixes

### Week 4: Testing & Polish

- Day 1-2: Error handling
- Day 3-4: Loading states
- Day 5: Testing & optimization

## 🚀 Ready to Start!

All UI is complete and ready for backend integration. Start with the High Priority tasks above to get data flowing into the app.

---

**Current Phase**: ✅ Phase 1 Complete (UI)
**Next Phase**: 🔄 Phase 2 Starting (Backend Integration)
**Estimated Time**: 3-4 weeks
