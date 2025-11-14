package com.unnikrishnan.dataclerk.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Gemini API service for AI-powered query generation and result formatting
 */
interface GeminiApiService {
    
    /**
     * Generate content using Gemini AI
     */
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

/**
 * Gemini API request body
 */
data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig? = null
)

data class Content(
    @SerializedName("parts")
    val parts: List<Part>
)

data class Part(
    @SerializedName("text")
    val text: String
)

data class GenerationConfig(
    @SerializedName("temperature")
    val temperature: Double = 0.7,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 2048
)

/**
 * Gemini API response
 */
data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>?
)

data class Candidate(
    @SerializedName("content")
    val content: Content?,
    @SerializedName("finishReason")
    val finishReason: String?
)
