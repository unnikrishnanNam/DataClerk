package com.unnikrishnan.dataclerk.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit client singleton for API communication
 */
object RetrofitClient {
    
    private const val DEFAULT_BASE_URL = "http://10.144.98.81:8090/api/" // Android emulator localhost
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    
    private var baseUrl: String = DEFAULT_BASE_URL
    
    /**
     * Update the base URL for API calls
     */
    fun updateBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
    }
    
    /**
     * OkHttp client with logging and timeouts
     */
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Separate OkHttp client for Gemini API with longer timeouts
     */
    private val geminiOkHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                try {
                    android.util.Log.d("GeminiAPI", "Request URL: ${request.url}")
                    android.util.Log.d("GeminiAPI", "Request Headers: ${request.headers}")
                    val response = chain.proceed(request)
                    android.util.Log.d("GeminiAPI", "Response Code: ${response.code}")
                    android.util.Log.d("GeminiAPI", "Response Message: ${response.message}")
                    if (!response.isSuccessful) {
                        val errorBody = response.peekBody(Long.MAX_VALUE).string()
                        android.util.Log.e("GeminiAPI", "Error Response Body: $errorBody")
                    }
                    response
                } catch (e: Exception) {
                    android.util.Log.e("GeminiAPI", "Request failed: ${e.javaClass.simpleName} - ${e.message}")
                    throw e
                }
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Retrofit instance
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * API service instance
     */
    val apiService: DataClerkApiService by lazy {
        retrofit.create(DataClerkApiService::class.java)
    }
    
    /**
     * Create a new Retrofit instance with updated base URL
     */
    fun recreateApiService(): DataClerkApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataClerkApiService::class.java)
    }
    
    /**
     * Gemini API service instance
     */
    val geminiApiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(geminiOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}
