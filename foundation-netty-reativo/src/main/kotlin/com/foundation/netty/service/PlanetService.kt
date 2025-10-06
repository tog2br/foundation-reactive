package com.foundation.netty.service

import com.foundation.netty.dto.PlanetResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class PlanetService {
    
    private val webClient = WebClient.builder()
        .baseUrl("https://swapi.dev/api")
        .build()
    
    fun getPlanet(): Mono<PlanetResponse> {
        return webClient.get()
            .uri("/planets/1/")
            .retrieve()
            .bodyToMono(PlanetResponse::class.java)
    }
}
