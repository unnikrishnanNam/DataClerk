package com.unnikrishnan.dataclerk.data.api

import com.unnikrishnan.dataclerk.data.models.DatabaseHealth
import com.unnikrishnan.dataclerk.data.models.ExecuteQueryRequest
import com.unnikrishnan.dataclerk.data.models.SchemaInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API service interface for Data Clerk backend
 */
interface DataClerkApiService {
    
    /**
     * Get list of available databases
     */
    @GET("databases")
    suspend fun getDatabases(): Response<List<String>>
    
    /**
     * Get health status of a specific database
     */
    @GET("database/{dbname}/health")
    suspend fun getDatabaseHealth(
        @Path("dbname") dbname: String
    ): Response<DatabaseHealth>
    
    /**
     * Get schema details of a specific database
     */
    @GET("database/{dbname}/schema")
    suspend fun getDatabaseSchema(
        @Path("dbname") dbname: String
    ): Response<List<SchemaInfo>>
    
    /**
     * Execute a SQL query on a specific database
     */
    @POST("database/execute")
    suspend fun executeQuery(
        @Body request: ExecuteQueryRequest
    ): Response<List<Map<String, Any>>>
}
