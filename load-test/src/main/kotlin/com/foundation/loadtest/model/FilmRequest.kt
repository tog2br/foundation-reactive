package com.foundation.loadtest.model

import com.fasterxml.jackson.annotation.JsonProperty

data class FilmRequest(
    val characters: List<String>,
    val created: String,
    val director: String,
    val edited: String,
    @JsonProperty("episode_id")
    val episodeId: Int,
    @JsonProperty("opening_crawl")
    val openingCrawl: String,
    val planets: List<String>,
    val producer: String,
    @JsonProperty("release_date")
    val releaseDate: String,
    val species: List<String>,
    val starships: List<String>,
    val title: String,
    val url: String,
    val vehicles: List<String>
)
