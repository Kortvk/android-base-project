package ru.appkode.base.entities.core.movie

import ru.appkode.base.entities.core.util.requireField

data class MovieDetailNM(
  val id: Int,
  val title: String,
  val imdb_id: String,
  val genres: List<GenreNM>,
  val original_title: String,
  val overview: String,
  val poster_path: String,
  val production_companies: List<ProductionCompanyNM>?,
  val release_date: String,
  val runtime: Int,
  val tagline: String,
  val vote_average: Float,
  val credits: CreditsNM?,
  val images: ImagesNM?,
  val keywords: List<KeywordNM>?
)

data class MovieBriefNM(
  val id: Int,
  val title: String,
  val genre_ids: List<Int>,
  val overview: String,
  val poster_path: String,
  val backdrop_path: String,
  val release_date: String,
  val vote_average: Float
)

data class GenreNM(val id: Int, val name: String)

data class KeywordNM(val id: Int, val name: String)

data class ImageNM(val file_path: String, val vote_average: Float)

data class ProductionCompanyNM(val id: Int, val name: String)

data class ImagesNM(val posters: List<ImageNM>, val backdrops: List<ImageNM>)

data class CreditsNM(val crew: List<CrewNM>, val cast: List<CastNM>)

data class CrewNM(
  val id: String,
  val job: String,
  val name: String
)

data class CastNM(
  val id: Int,
  val name: String,
  val profile_path: String
)


fun MovieBriefNM.toUiModel(genresMapper: List<GenreNM>): MovieBriefUM {
  return MovieBriefUM(
    id = id.requireField("id"),
    title = title,
    isInWishList = false,
    overview = overview,
    backdrop = backdrop_path,
    poster = poster_path,
    releaseDate = release_date,
    rating = vote_average,
    genres = genre_ids.map { id -> genresMapper.find { it.id == id }?.name }
  )
}


