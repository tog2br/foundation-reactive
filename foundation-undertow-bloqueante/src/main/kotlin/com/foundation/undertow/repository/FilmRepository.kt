package com.foundation.undertow.repository

import com.foundation.undertow.entity.Film
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FilmRepository : JpaRepository<Film, UUID>
