package com.foundation.undertow.service

import com.foundation.undertow.dto.FilmRequest
import com.foundation.undertow.entity.Film
import com.foundation.undertow.repository.FilmRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class FilmService(
    private val filmRepository: FilmRepository,
    private val objectMapper: ObjectMapper
) {
    
    suspend fun createFilm(filmRequest: FilmRequest): Film = withContext(Dispatchers.IO) {
        val film = Film(
            characters = objectMapper.writeValueAsString(filmRequest.characters),
            created = filmRequest.created,
            director = filmRequest.director,
            edited = filmRequest.edited,
            episodeId = filmRequest.episodeId,
            openingCrawl = filmRequest.openingCrawl,
            planets = objectMapper.writeValueAsString(filmRequest.planets),
            producer = filmRequest.producer,
            releaseDate = filmRequest.releaseDate,
            species = objectMapper.writeValueAsString(filmRequest.species),
            starships = objectMapper.writeValueAsString(filmRequest.starships),
            title = filmRequest.title,
            url = filmRequest.url,
            vehicles = objectMapper.writeValueAsString(filmRequest.vehicles)
        )
        
        filmRepository.save(film)
    }
}
