package com.foundation.netty.repository

import com.foundation.netty.entity.Film
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FilmRepository : R2dbcRepository<Film, UUID>
