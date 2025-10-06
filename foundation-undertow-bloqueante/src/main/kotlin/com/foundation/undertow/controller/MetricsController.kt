package com.foundation.undertow.controller

import com.foundation.undertow.service.MetricsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MetricsController(private val metricsService: MetricsService) {
    
    @GetMapping("/metrics")
    fun getMetrics(): Map<String, Any> {
        return metricsService.getMetrics()
    }
}
