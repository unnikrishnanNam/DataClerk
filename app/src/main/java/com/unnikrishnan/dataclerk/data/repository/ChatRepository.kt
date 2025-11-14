package com.unnikrishnan.dataclerk.data.repository

import com.unnikrishnan.dataclerk.data.api.*
import com.unnikrishnan.dataclerk.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Repository for chat and AI operations
 */
class ChatRepository {
    
    private val dataClerkApiService = RetrofitClient.apiService
    private val geminiApiService = RetrofitClient.geminiApiService
    private val databaseRepository = DatabaseRepository()
    
    /**
     * Process a user query through the complete AI pipeline
     */
    suspend fun processUserQuery(
        databaseName: String,
        userPrompt: String,
        geminiApiKey: String
    ): Result<ChatResponse> = withContext(Dispatchers.IO) {
        try {
            // Step 1: Get database schema
            val schemaResult = databaseRepository.getDatabaseSchema(databaseName)
            if (schemaResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Failed to load schema: ${schemaResult.exceptionOrNull()?.message}")
                )
            }
            
            val schema = schemaResult.getOrNull()!!
            val schemaText = formatSchemaForPrompt(schema)
            
            // Step 2: Generate SQL query using Gemini
            val queryPrompt = buildQueryGenerationPrompt(userPrompt, schemaText)
            val sqlQueryResult = generateSqlQuery(queryPrompt, geminiApiKey)
            
            if (sqlQueryResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Failed to generate SQL: ${sqlQueryResult.exceptionOrNull()?.message}")
                )
            }
            
            val sqlQuery = sqlQueryResult.getOrNull()!!.trim()
            
            // Step 3: Execute the query
            val queryResult = databaseRepository.executeQuery(databaseName, sqlQuery)
            
            if (queryResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Query execution failed: ${queryResult.exceptionOrNull()?.message}")
                )
            }
            
            val queryData = queryResult.getOrNull()!!
            
            // Add delay to avoid rate limiting (2 API calls per message)
            kotlinx.coroutines.delay(2000) // 2 second delay
            
            // Step 4: Format results using Gemini with retry logic
            val formattingPrompt = buildResultFormattingPrompt(userPrompt, sqlQuery, queryData)
            var formattedResult = formatQueryResults(formattingPrompt, geminiApiKey)
            
            // Retry once if rate limited
            if (formattedResult.isFailure && 
                formattedResult.exceptionOrNull()?.message?.contains("Rate limit") == true) {
                android.util.Log.w("ChatRepository", "Rate limited, waiting 3 seconds and retrying...")
                kotlinx.coroutines.delay(3000)
                formattedResult = formatQueryResults(formattingPrompt, geminiApiKey)
            }
            
            if (formattedResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Failed to format results: ${formattedResult.exceptionOrNull()?.message}")
                )
            }
            
            val response = ChatResponse(
                sqlQuery = sqlQuery,
                queryResults = queryData,
                formattedResponse = formattedResult.getOrNull()!!,
                executionTime = 0L // Could be measured if needed
            )
            
            Result.success(response)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate SQL query using Gemini AI
     */
    private suspend fun generateSqlQuery(
        prompt: String,
        apiKey: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.2, // Low temperature for precise SQL
                    maxOutputTokens = 2048
                )
            )
            
            val response = geminiApiService.generateContent(apiKey, request)
            
            android.util.Log.d("ChatRepository", "Response successful: ${response.isSuccessful}")
            android.util.Log.d("ChatRepository", "Response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                android.util.Log.d("ChatRepository", "Response body: $body")
                android.util.Log.d("ChatRepository", "Candidates: ${body?.candidates}")
                android.util.Log.d("ChatRepository", "Candidates size: ${body?.candidates?.size}")
                
                val firstCandidate = body?.candidates?.firstOrNull()
                android.util.Log.d("ChatRepository", "First candidate: $firstCandidate")
                android.util.Log.d("ChatRepository", "Content: ${firstCandidate?.content}")
                android.util.Log.d("ChatRepository", "Parts: ${firstCandidate?.content?.parts}")
                android.util.Log.d("ChatRepository", "Parts size: ${firstCandidate?.content?.parts?.size}")
                
                val text = firstCandidate?.content?.parts?.firstOrNull()?.text
                android.util.Log.d("ChatRepository", "Extracted text: $text")
                
                if (text != null && text.isNotBlank()) {
                    // Clean up the response - remove markdown, extra spaces, etc.
                    val cleanedSql = cleanSqlQuery(text)
                    Result.success(cleanedSql)
                } else {
                    android.util.Log.e("ChatRepository", "Text is null or blank!")
                    Result.failure(Exception("No content in Gemini response"))
                }
            } else {
                android.util.Log.e("ChatRepository", "Response not successful or body is null")
                android.util.Log.e("ChatRepository", "Response message: ${response.message()}")
                val errorMessage = when (response.code()) {
                    400 -> "Bad request: Check your API key and request format"
                    401 -> "Unauthorized: Invalid API key"
                    403 -> "Forbidden: API key doesn't have permission for this model"
                    404 -> "Not found: Model may not be available with your API key"
                    429 -> "Rate limit exceeded: Too many requests. Please wait a moment and try again"
                    500 -> "Gemini server error: Try again later"
                    503 -> "Service unavailable: Gemini is temporarily down"
                    else -> "API error (${response.code()}): ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("DNS error: Cannot resolve generativelanguage.googleapis.com. Check DNS settings."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Timeout: Gemini API is not responding. Check your internet connection."))
        } catch (e: javax.net.ssl.SSLException) {
            Result.failure(Exception("SSL error: ${e.message}. Try restarting the emulator."))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.javaClass.simpleName} - ${e.message}"))
        }
    }
    
    /**
     * Format query results using Gemini AI
     */
    private suspend fun formatQueryResults(
        prompt: String,
        apiKey: String
    ): Result<FormattedResult> = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.7,
                    maxOutputTokens = 2048
                )
            )
            
            val response = geminiApiService.generateContent(apiKey, request)
            
            android.util.Log.d("ChatRepository", "Format - Response successful: ${response.isSuccessful}")
            android.util.Log.d("ChatRepository", "Format - Response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                android.util.Log.d("ChatRepository", "Format - Extracted text length: ${text?.length ?: 0}")
                
                if (text != null && text.isNotBlank()) {
                    val formatted = parseFormattedResponse(text)
                    Result.success(formatted)
                } else {
                    Result.failure(Exception("No content in Gemini response"))
                }
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Bad request: Check your API key and request format"
                    401 -> "Unauthorized: Invalid API key"
                    403 -> "Forbidden: API key doesn't have permission for this model"
                    404 -> "Not found: Model may not be available with your API key"
                    429 -> "Rate limit exceeded: Too many requests. Please wait a moment and try again"
                    500 -> "Gemini server error: Try again later"
                    503 -> "Rate limit exceeded: Too many requests. Please wait 10-30 seconds and try again"
                    else -> "API error (${response.code()}): ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("DNS error: Cannot resolve generativelanguage.googleapis.com. Check DNS settings."))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Timeout: Gemini API is not responding. Check your internet connection."))
        } catch (e: javax.net.ssl.SSLException) {
            Result.failure(Exception("SSL error: ${e.message}. Try restarting the emulator."))
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.javaClass.simpleName} - ${e.message}"))
        }
    }
    
    /**
     * Build prompt for SQL query generation
     */
    private fun buildQueryGenerationPrompt(userPrompt: String, schema: String): String {
        return """
You are a PostgreSQL expert. Generate ONLY the SQL query without any explanation, markdown formatting, or additional text.

Database Schema:
$schema

User Question: $userPrompt

Requirements:
- Return ONLY the SQL query as plain text
- NO markdown code blocks (no ```sql or ```)
- NO explanations before or after the query
- NO comments in the query
- Use PostgreSQL syntax
- Ensure the query is safe and won't modify data unless explicitly asked
- Use appropriate JOINs, WHERE clauses, and aggregations as needed

SQL Query:
        """.trimIndent()
    }
    
    /**
     * Build prompt for result formatting
     */
    private fun buildResultFormattingPrompt(
        userPrompt: String,
        sqlQuery: String,
        results: List<Map<String, Any>>
    ): String {
        val resultsJson = JSONArray(results).toString()
        
        return """
You are a helpful data analyst. Format the query results in a user-friendly way.

User's Question: $userPrompt

SQL Query Executed: $sqlQuery

Query Results (JSON):
$resultsJson

Instructions:
1. Analyze if the results need to be displayed as:
   - TEXT: Simple answer (for counts, single values, or short explanations)
   - TABLE: Tabular data (for multiple rows/columns)
   - CHART_BAR: Bar chart (for comparisons)
   - CHART_LINE: Line chart (for trends over time)
   - CHART_PIE: Pie chart (for distributions/percentages)

2. Format your response EXACTLY as follows:

For TEXT responses:
TYPE: TEXT
CONTENT: Your human-readable explanation here

For TABLE responses:
TYPE: TABLE
CONTENT: Your explanation here
HEADERS: Column1|Column2|Column3
ROWS:
Row1Value1|Row1Value2|Row1Value3
Row2Value1|Row2Value2|Row2Value3

For CHART responses:
TYPE: CHART_BAR (or CHART_LINE or CHART_PIE)
CONTENT: Brief explanation of the chart
LABELS: Label1|Label2|Label3
VALUES: Value1|Value2|Value3

3. Rules:
   - Keep explanations concise and clear
   - Use pipe (|) as separator for tables and charts
   - For simple answers (like counts or single values), use TEXT
   - For complex data with multiple rows, use TABLE
   - For numerical comparisons, use CHART_BAR
   - For time-series data, use CHART_LINE
   - For percentage distributions, use CHART_PIE
   - If results are empty, explain that no data was found

4. You can return multiple messages if needed by separating them with:
---MESSAGE---

Example for multiple messages:
TYPE: TEXT
CONTENT: Here's what I found about your question.
---MESSAGE---
TYPE: TABLE
CONTENT: Detailed breakdown:
HEADERS: Product|Sales|Profit
ROWS:
Product A|1000|500
Product B|800|400

Now format the results:
        """.trimIndent()
    }
    
    /**
     * Format schema for prompt
     */
    private fun formatSchemaForPrompt(schema: List<TableSchema>): String {
        return schema.joinToString("\n\n") { table ->
            val columns = table.columns.joinToString("\n") { (name, type, constraints) ->
                "  - $name: $type ${if (constraints.isNotEmpty()) "($constraints)" else ""}"
            }
            "Table: ${table.tableName}\nColumns:\n$columns"
        }
    }
    
    /**
     * Clean SQL query from markdown and extra formatting
     */
    private fun cleanSqlQuery(rawSql: String): String {
        var cleaned = rawSql.trim()
        
        // Remove markdown code blocks
        cleaned = cleaned.replace(Regex("```sql\\n?"), "")
        cleaned = cleaned.replace(Regex("```\\n?"), "")
        
        // Remove "SQL Query:" prefix if present
        cleaned = cleaned.replace(Regex("^SQL Query:\\s*", RegexOption.IGNORE_CASE), "")
        
        // Remove any leading/trailing whitespace
        cleaned = cleaned.trim()
        
        // Remove trailing semicolon if present (backend might add it)
        if (cleaned.endsWith(";")) {
            cleaned = cleaned.dropLast(1).trim()
        }
        
        return cleaned
    }
    
    /**
     * Parse formatted response from Gemini
     */
    private fun parseFormattedResponse(response: String): FormattedResult {
        val messages = response.split("---MESSAGE---").map { it.trim() }.filter { it.isNotEmpty() }
        
        val parsedMessages = messages.mapNotNull { messageText ->
            parseMessage(messageText)
        }
        
        return FormattedResult(messages = parsedMessages)
    }
    
    /**
     * Parse a single message from formatted response
     */
    private fun parseMessage(text: String): MessageContent? {
        val lines = text.lines().map { it.trim() }
        
        val typeLine = lines.firstOrNull { it.startsWith("TYPE:") } ?: return null
        val type = typeLine.substringAfter("TYPE:").trim()
        
        return when (type) {
            "TEXT" -> {
                val content = extractField(lines, "CONTENT") ?: return null
                MessageContent.Text(content)
            }
            "TABLE" -> {
                val content = extractField(lines, "CONTENT") ?: ""
                val headers = extractField(lines, "HEADERS")?.split("|") ?: emptyList()
                val rowsText = extractMultiLineField(lines, "ROWS")
                val rows = rowsText.map { it.split("|") }
                MessageContent.Table(content, headers, rows)
            }
            "CHART_BAR", "CHART_LINE", "CHART_PIE" -> {
                val content = extractField(lines, "CONTENT") ?: ""
                val labels = extractField(lines, "LABELS")?.split("|") ?: emptyList()
                val values = extractField(lines, "VALUES")?.split("|")?.mapNotNull { 
                    it.toFloatOrNull() 
                } ?: emptyList()
                
                val chartType = when (type) {
                    "CHART_BAR" -> ChartType.BAR
                    "CHART_LINE" -> ChartType.LINE
                    "CHART_PIE" -> ChartType.PIE
                    else -> ChartType.BAR
                }
                
                MessageContent.Chart(content, chartType, labels, values)
            }
            else -> null
        }
    }
    
    /**
     * Extract a single field value
     */
    private fun extractField(lines: List<String>, fieldName: String): String? {
        return lines.firstOrNull { it.startsWith("$fieldName:") }
            ?.substringAfter("$fieldName:")?.trim()
    }
    
    /**
     * Extract multi-line field (like ROWS)
     */
    private fun extractMultiLineField(lines: List<String>, fieldName: String): List<String> {
        val startIndex = lines.indexOfFirst { it.startsWith("$fieldName:") }
        if (startIndex == -1) return emptyList()
        
        val result = mutableListOf<String>()
        for (i in (startIndex + 1) until lines.size) {
            val line = lines[i]
            if (line.isEmpty() || line.contains(":")) break
            result.add(line)
        }
        return result
    }
}

/**
 * Chat response containing all processing results
 */
data class ChatResponse(
    val sqlQuery: String,
    val queryResults: List<Map<String, Any>>,
    val formattedResponse: FormattedResult,
    val executionTime: Long
)

/**
 * Formatted result containing multiple message contents
 */
data class FormattedResult(
    val messages: List<MessageContent>
)

/**
 * Different types of message content
 */
sealed class MessageContent {
    data class Text(val content: String) : MessageContent()
    data class Table(
        val description: String,
        val headers: List<String>,
        val rows: List<List<String>>
    ) : MessageContent()
    data class Chart(
        val description: String,
        val type: ChartType,
        val labels: List<String>,
        val values: List<Float>
    ) : MessageContent()
}

/**
 * Chart types
 */
enum class ChartType {
    BAR, LINE, PIE
}
