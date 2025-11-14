# Chat Feature - Testing Guide

## 🚀 Quick Start

### 1. Get Gemini API Key
1. Visit https://makersuite.google.com/app/apikey
2. Sign in with Google account
3. Click "Create API Key"
4. Copy the API key

### 2. Configure App
1. Open Data Clerk app
2. Tap drawer menu (≡) → Settings
3. Scroll to "API Configuration"
4. Paste API key in "Gemini API Key" field
5. API key is automatically saved

### 3. Start Chatting
1. Return to Home screen
2. Select a database from the list
3. Tap "💬 Chat with DB" button
4. Start asking questions!

## 📝 Test Queries

### Basic Queries

```
Show me all tables
List the first 10 customers
What are the column names in the orders table?
Show me recent orders
```

### Aggregation Queries

```
How many customers do we have?
What's the total revenue?
Count orders by status
Show me monthly sales
```

### Analysis Queries

```
Which customers have the most orders?
What are the top selling products?
Show me revenue trends
Compare sales by region
```

### Chart-Worthy Queries

**Bar Charts:**
```
Show order counts by customer
Compare product sales
Revenue by month
```

**Line Charts:**
```
Show sales trend over time
Track customer growth
Daily revenue pattern
```

**Pie Charts:**
```
Order distribution by status
Sales breakdown by category
Customer segments
```

## ✅ Expected Behaviors

### Text Responses
- Natural language summary
- Single text bubble
- Assistant avatar on left
- Dark surface background

### Table Responses
- Header row with column names (blue accent)
- Data rows (alternating colors)
- Horizontal scroll for wide tables
- Row count at bottom
- Description text above table

### Chart Responses
- Bar: Vertical bars with gradients
- Line: Connected points with path
- Pie: Donut chart with legend
- Description text above chart
- Values/percentages labeled

### Multiple Messages
- Single query can generate multiple bubbles
- Example: Text summary + chart
- Stacked vertically
- Same timestamp

### Error Cases
- No API key: Warning icon, "API Key Required"
- Network error: Red bubble with error message
- Invalid query: Error explanation from Gemini
- Processing: Circular indicator in send button

## 🐛 Debugging

### Check API Key
```kotlin
// In PreferencesManager.kt
fun hasGeminiApiKey(): Boolean {
    return geminiApiKey.isNotBlank()
}
```

### Check Network
- Backend must be running at `http://10.0.2.2:8090/api`
- Test with: `curl http://10.0.2.2:8090/api/health`
- Gemini API must be accessible

### Check Logs
Look for these tags:
```
ChatRepository: Processing query
ChatRepository: Generated SQL
ChatRepository: Query results
ChatRepository: Formatted response
ChatViewModel: Sending message
ChatViewModel: Success/Error
```

### Common Issues

**Issue:** "API Key Required" message
**Fix:** Configure API key in Settings → API Configuration

**Issue:** No response from chat
**Fix:** Check backend is running, verify API key is valid

**Issue:** Table not rendering
**Fix:** Verify HEADERS and ROWS fields in response format

**Issue:** Chart not rendering
**Fix:** Verify LABELS and VALUES are numeric/valid format

**Issue:** SQL errors
**Fix:** Schema might be missing, check database connection

## 🔍 Response Format Validation

### Valid TEXT Response
```
TYPE: TEXT
CONTENT: The total revenue is $5,432.10 from 128 orders.
```

### Valid TABLE Response
```
TYPE: TABLE
CONTENT: Here are your recent orders
HEADERS: id|customer|amount|status
ROWS: 1|John Doe|100.00|completed
ROWS: 2|Jane Smith|250.00|pending
```

### Valid CHART Response
```
TYPE: CHART_BAR
CONTENT: Order distribution by customer
LABELS: John|Jane|Bob
VALUES: 15|23|8
```

### Multiple Messages
```
TYPE: TEXT
CONTENT: Analysis of your sales data shows strong growth.

---MESSAGE---

TYPE: CHART_LINE
CONTENT: Revenue trend over past 6 months
LABELS: Jan|Feb|Mar|Apr|May|Jun
VALUES: 1000|1200|1100|1500|1800|2000
```

## 📊 Sample Databases

Create test databases with these schemas:

### E-commerce Database
```sql
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    price DECIMAL(10,2),
    category VARCHAR(50)
);

-- Insert sample data
INSERT INTO customers (name, email) VALUES
('John Doe', 'john@example.com'),
('Jane Smith', 'jane@example.com'),
('Bob Wilson', 'bob@example.com');

INSERT INTO orders (customer_id, amount, status) VALUES
(1, 100.00, 'completed'),
(1, 150.00, 'completed'),
(2, 200.00, 'pending'),
(2, 300.00, 'completed'),
(3, 75.00, 'completed');

INSERT INTO products (name, price, category) VALUES
('Product A', 50.00, 'Electronics'),
('Product B', 75.00, 'Electronics'),
('Product C', 25.00, 'Books');
```

### Test Queries for This Schema

```
# Count queries
How many customers do we have?
How many orders are pending?

# Aggregation
What's the total revenue?
Show me average order value

# Grouping
Orders per customer
Revenue by customer

# Analysis
Which customers spent the most?
What's our order completion rate?
```

## 🎯 Success Criteria

- ✅ User can enter API key in Settings
- ✅ API key is persisted across app restarts
- ✅ Empty state shows when no API key configured
- ✅ Chat accepts natural language queries
- ✅ SQL is generated from user input
- ✅ Queries execute against backend database
- ✅ Results are formatted naturally
- ✅ TEXT responses display in bubbles
- ✅ TABLE responses render as scrollable tables
- ✅ CHART responses render as bar/line/pie charts
- ✅ Multiple messages can display for one query
- ✅ Errors are handled gracefully
- ✅ Processing state is indicated
- ✅ Messages auto-scroll to bottom

## 📝 Test Checklist

### Pre-Testing
- [ ] Backend server is running
- [ ] Database has sample data
- [ ] Gemini API key is obtained
- [ ] App is installed on device/emulator

### Settings Screen
- [ ] Can navigate to Settings
- [ ] API key field is visible
- [ ] Can paste API key
- [ ] Show/hide toggle works
- [ ] API key is saved automatically
- [ ] Navigate back to Home

### Chat Screen
- [ ] Can navigate to Chat from database
- [ ] Database name shows in header
- [ ] Empty state displays correctly
- [ ] Input field is functional
- [ ] Send button is clickable

### Text Queries
- [ ] Simple count query works
- [ ] Aggregation query works
- [ ] Summary query works
- [ ] Response displays in bubble
- [ ] Text is readable

### Table Queries
- [ ] List query generates table
- [ ] Headers display correctly
- [ ] Rows display correctly
- [ ] Horizontal scroll works
- [ ] Row count is shown

### Chart Queries
- [ ] Bar chart renders correctly
- [ ] Line chart renders correctly
- [ ] Pie chart renders correctly
- [ ] Labels are visible
- [ ] Values are accurate

### Error Handling
- [ ] No API key shows warning
- [ ] Network error shows error message
- [ ] Invalid query shows helpful message
- [ ] Can recover from errors

### Performance
- [ ] Response time is acceptable (< 10s)
- [ ] UI remains responsive during processing
- [ ] Scrolling is smooth
- [ ] Charts render without lag

## 🚀 Ready to Test!

Follow the Quick Start steps and run through the test queries. The chat feature should handle various query types and display results appropriately!
