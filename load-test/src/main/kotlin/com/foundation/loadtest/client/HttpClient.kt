package com.foundation.loadtest.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.foundation.loadtest.model.FilmRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpClient {
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Logging) {
            level = LogLevel.INFO // Reduzir logs para melhor performance
        }
    }
    
    private val objectMapper = jacksonObjectMapper()
    
    suspend fun getPlanets(server: String): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val response = client.get("http://$server:8080/api/planets")
            Result.success(response.body<Any>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createFilm(server: String, filmRequest: FilmRequest): Result<Any> = withContext(Dispatchers.IO) {
        try {
            val port = if (server == "undertow") 8080 else 8081
            val response = client.put("http://$server:$port/api/film") {
                contentType(ContentType.Application.Json)
                setBody(filmRequest)
            }
            Result.success(response.body<Any>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMetrics(server: String, port: Int): Result<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get("http://$server:$port/api/metrics")
            Result.success(response.body<Map<String, Any>>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun close() {
        client.close()
    }
}
