package com.foundation.netty.controller

import com.foundation.netty.dto.FilmRequest
import com.foundation.netty.service.FilmService
import com.foundation.netty.service.MetricsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class FilmController(
    private val filmService: FilmService,
    private val metricsService: MetricsService
) {
    
    @PutMapping("/film")
    fun createFilm(@RequestBody filmRequest: FilmRequest): Mono<ResponseEntity<Any>> {
        val startTime = System.currentTimeMillis()
        
        return filmService.createFilm(filmRequest)
            .map { film ->
                val duration = System.currentTimeMillis() - startTime
                metricsService.recordRequest("/film", duration).subscribe()
                ResponseEntity.ok(mapOf(
                    "id" to film.id,
                    "title" to film.title,
                    "message" to "Filme criado com sucesso"
                ) as Any)
            }
            .onErrorResume { error ->
                val duration = System.currentTimeMillis() - startTime
                metricsService.recordRequest("/film", duration).subscribe()
                Mono.error(error)
            }
    }
}
