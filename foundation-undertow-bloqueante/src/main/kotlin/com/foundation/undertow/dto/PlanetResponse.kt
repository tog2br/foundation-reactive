package com.foundation.undertow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PlanetResponse(
    val name: String,
    @JsonProperty("rotation_period")
    val rotationPeriod: String,
    @JsonProperty("orbital_period")
    val orbitalPeriod: String,
    val diameter: String,
    val climate: String,
    val gravity: String,
    val terrain: String,
    @JsonProperty("surface_water")
    val surfaceWater: String,
    val population: String,
    val residents: List<String>,
    val films: List<String>,
    val created: String,
    val edited: String,
    val url: String
)
