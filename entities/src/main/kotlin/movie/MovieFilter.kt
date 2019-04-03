package ru.appkode.base.entities.core.movie

import android.util.Range

data class MovieFilter(val name: String,
                       val year: Range<Int>,
                       val rating: Range<Float>,
                       val cast: List<String>,
                       val genres: List<String>,
                       val keywords: List<String>)