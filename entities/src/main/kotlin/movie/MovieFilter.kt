package ru.appkode.base.entities.core.movie

import java.util.*

data class MovieFilter(
  val name: String? = null,
  val releaseDate: ClosedRange<Date>? = null,
  val rating: ClosedRange<Float>? = null,
  val cast: List<String>? = null,
  val genres: List<String>? = null,
  val keywords: List<String>? = null
)

fun MovieFilter.buildFilterParameters(): Map<String, String> {
  val queryMap = mutableMapOf<String, String>()
  releaseDate?.let {
    queryMap["release_date.gte"] = it.start.toString()
    queryMap["release_date.lte"] = it.endInclusive.toString()
  }
  rating?.let {
    queryMap["vote_average.gte"] = it.start.toString()
    queryMap["vote_average.lte"] = it.endInclusive.toString()
  }
  cast?.let { queryMap["with_cast"] = it.joinToString(separator = ",") }
  genres?.let { queryMap["with_genres"] = it.joinToString(separator = ",") }
  keywords?.let { queryMap["with_keywords"] = it.joinToString(separator = ",") }
  return queryMap
}