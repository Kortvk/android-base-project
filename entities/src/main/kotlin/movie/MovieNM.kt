package ru.appkode.base.entities.core.movie

data class MovieDetailNM(
  val id: Int,
  val title: String,
  val imdb_id: String,
  val genres: List<GenreNM>,
  val originalTitle: String,
  val overview: String,
  val posterPath: String,
  val productionCompanies: List<ProductionCompanyNM>?,
  val releaseDate: String,
  val runtime: Int,
  val tagline: String,
  val voteAverage: Float,
  val credits: CreditsNM?,
  val images: ImagesNM?,
  val keywords: List<KeywordNM>?
)

data class MovieBriefNM(
  val id: Int,
  val title: String,
  val genreIds: List<Int>,
  val overview: String,
  val posterPath: String,
  val backdropPath: String,
  val releaseDate: String,
  val voteAverage: Float
)

data class GenreNM(val id: Int, val name: String)

data class KeywordNM(val id: Int, val name: String)

data class ImageNM(val filePath: String, val voteAverage: Float)

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
  val profilePath: String
)



