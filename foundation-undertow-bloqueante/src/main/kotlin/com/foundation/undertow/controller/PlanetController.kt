package com.foundation.undertow.controller

import com.foundation.undertow.service.MetricsService
import com.foundation.undertow.service.PlanetService
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PlanetController(
    private val planetService: PlanetService,
    private val metricsService: MetricsService
) {
    
    @GetMapping("/planets")
    fun getPlanets(): ResponseEntity<Any> = runBlocking {
        val startTime = System.currentTimeMillis()
        
        try {
            val planet = planetService.getPlanet()
            val duration = System.currentTimeMillis() - startTime
            
            metricsService.recordRequest("/planets", duration)
            
            ResponseEntity.ok(planet)
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            metricsService.recordRequest("/planets", duration)
            throw e
        }
    }
}
