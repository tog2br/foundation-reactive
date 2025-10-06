package com.foundation.undertow.service

import com.foundation.undertow.dto.PlanetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PlanetService {
    
    private val restTemplate = RestTemplate()
    
    suspend fun getPlanet(): PlanetResponse = withContext(Dispatchers.IO) {
        restTemplate.getForObject("https://swapi.dev/api/planets/1/", PlanetResponse::class.java)!!
    }
}