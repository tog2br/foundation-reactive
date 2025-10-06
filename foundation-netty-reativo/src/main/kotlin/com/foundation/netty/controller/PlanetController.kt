package com.foundation.netty.controller

import com.foundation.netty.service.MetricsService
import com.foundation.netty.service.PlanetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class PlanetController(
    private val planetService: PlanetService,
    private val metricsService: MetricsService
) {
    
    @GetMapping("/planets")
    fun getPlanets(): Mono<ResponseEntity<Any>> {
        val startTime = System.currentTimeMillis()
        
        return planetService.getPlanet()
            .map { planet ->
                val duration = System.currentTimeMillis() - startTime
                metricsService.recordRequest("/planets", duration).subscribe()
                ResponseEntity.ok(planet as Any)
            }
            .onErrorResume { error ->
                val duration = System.currentTimeMillis() - startTime
                metricsService.recordRequest("/planets", duration).subscribe()
                Mono.error(error)
            }
    }
}
