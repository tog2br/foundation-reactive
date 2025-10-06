package com.foundation.undertow.controller

import com.foundation.undertow.dto.FilmRequest
import com.foundation.undertow.service.FilmService
import com.foundation.undertow.service.MetricsService
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class FilmController(
    private val filmService: FilmService,
    private val metricsService: MetricsService
) {
    
    @PutMapping("/film")
    fun createFilm(@RequestBody filmRequest: FilmRequest): ResponseEntity<Any> = runBlocking {
        val startTime = System.currentTimeMillis()
        
        try {
            val film = filmService.createFilm(filmRequest)
            val duration = System.currentTimeMillis() - startTime
            
            metricsService.recordRequest("/film", duration)
            
            ResponseEntity.ok(mapOf(
                "id" to film.id,
                "title" to film.title,
                "message" to "Filme criado com sucesso"
            ))
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            metricsService.recordRequest("/film", duration)
            throw e
        }
    }
}
