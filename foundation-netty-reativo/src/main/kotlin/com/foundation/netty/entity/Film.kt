package com.foundation.netty.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("films")
data class Film(
    @Id
    val id: UUID? = null,
    
    @Column("characters")
    val characters: String, // JSON string
    
    @Column("created")
    val created: String,
    
    @Column("director")
    val director: String,
    
    @Column("edited")
    val edited: String,
    
    @Column("episode_id")
    val episodeId: Int,
    
    @Column("opening_crawl")
    val openingCrawl: String,
    
    @Column("planets")
    val planets: String, // JSON string
    
    @Column("producer")
    val producer: String,
    
    @Column("release_date")
    val releaseDate: String,
    
    @Column("species")
    val species: String, // JSON string
    
    @Column("starships")
    val starships: String, // JSON string
    
    @Column("title")
    val title: String,
    
    @Column("url")
    val url: String,
    
    @Column("vehicles")
    val vehicles: String, // JSON string
    
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
