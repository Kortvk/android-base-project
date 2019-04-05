package ru.appkode.base.entities.core.movie

import java.util.*

data class MovieFilter(val name: String? = null,
                       val releaseDate: ClosedRange<Date>? = null,
                       val rating: ClosedRange<Float>? = null,
                       val cast: List<String>? = null,
                       val genres: List<String>? = null,
                       val keywords: List<String>? = null)