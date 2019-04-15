package ru.appkode.base.entities.core.movie

import ru.appkode.base.entities.core.util.requireField

data class MovieDetailedUM(
  val id: Long,
  val title: String,
  val imdbId: String,
  val genres: List<String>,
  val status: String,
  val overview: String,
  val posterPath: String,
  val productionCompanies: List<String>?,
  val releaseDate: String,
  val runtime: Int,
  val tagline: String,
  val voteAverage: Float,
  val cast: List<CastUM>?,
  val crew: List<CrewUM>?,
  val backdrop: String,
  val images: List<String>?,
  val keywords: List<String>?,
  val votes: Int,
  val isInWishList: Boolean = false,
  val isInHistory: Boolean = false
)

data class MovieBriefUM(
  val id: Long,
  val genres: List<String?>,
  val overview: String,
  val poster: String?,
  val backdrop: String?,
  val releaseDate: String,
  val title: String,
  val rating: Float,
  val votes: Int,
  val isExpanded: Boolean = false,
  val isInWishList: Boolean = false,
  val isInHistory: Boolean = false
)

data class CastUM(
  val id: Long,
  val name: String,
  val character: String,
  val profilePath: String?
)

data class CrewUM(
  val id: String,
  val job: String,
  val name: String
)

fun MovieDetailedUM.toBrief(): MovieBriefUM {
  return MovieBriefUM(
    id = id.requireField("id"),
    title = title,
    isInWishList = isInWishList,
    overview = overview,
    backdrop = backdrop,
    poster = posterPath,
    releaseDate = releaseDate,
    rating = voteAverage,
    genres = genres,
    votes = votes
  )
}

fun CastUM.getProfilePath() = BASE_IMAGE_URL + IMAGE_PROFILE_SIZE + profilePath
