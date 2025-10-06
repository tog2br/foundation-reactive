package com.foundation.netty.service

import com.foundation.netty.dto.FilmRequest
import com.foundation.netty.entity.Film
import com.foundation.netty.repository.FilmRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class FilmService(
    private val filmRepository: FilmRepository,
    private val objectMapper: ObjectMapper
) {
    
    fun createFilm(filmRequest: FilmRequest): Mono<Film> {
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
        
        return filmRepository.save(film)
    }
}
