package com.foundation.netty.controller

import com.foundation.netty.service.MetricsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class MetricsController(private val metricsService: MetricsService) {
    
    @GetMapping("/metrics")
    fun getMetrics(): Mono<Map<String, Any>> {
        return metricsService.getMetrics()
    }
}
