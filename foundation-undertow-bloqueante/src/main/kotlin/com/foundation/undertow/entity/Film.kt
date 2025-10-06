package com.foundation.undertow.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "films")
data class Film(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    
    @Column(name = "characters", columnDefinition = "TEXT")
    val characters: String, // JSON string
    
    @Column(name = "created")
    val created: String,
    
    @Column(name = "director")
    val director: String,
    
    @Column(name = "edited")
    val edited: String,
    
    @Column(name = "episode_id")
    val episodeId: Int,
    
    @Column(name = "opening_crawl", columnDefinition = "TEXT")
    val openingCrawl: String,
    
    @Column(name = "planets", columnDefinition = "TEXT")
    val planets: String, // JSON string
    
    @Column(name = "producer")
    val producer: String,
    
    @Column(name = "release_date")
    val releaseDate: String,
    
    @Column(name = "species", columnDefinition = "TEXT")
    val species: String, // JSON string
    
    @Column(name = "starships", columnDefinition = "TEXT")
    val starships: String, // JSON string
    
    @Column(name = "title")
    val title: String,
    
    @Column(name = "url")
    val url: String,
    
    @Column(name = "vehicles", columnDefinition = "TEXT")
    val vehicles: String, // JSON string
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
