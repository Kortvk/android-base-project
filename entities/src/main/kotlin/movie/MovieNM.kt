package ru.appkode.base.entities.core.movie

data class MovieNM(val id: Int,
                   val imdb_id: String,
                   val genres: List<GenreNM>,
                   val originalTitle: String,
                   val overview: String,
                   val posterPath: String,
                   val productionCompanies: List<ProductionCompanyNM>,
                   val releaseDate: String,
                   val runtime: Int,
                   val tagline: String,
                   val title: String,
                   val voteAverage: Float,
                   val cast: List<CastNM>,
                   val crew: List<CrewNM>,
                   val backdrops: List<ImageNM>,
                   val posters: List<ImageNM>,
                   val keywords: List<KeywordNM>)


data class GenreNM(val id: Int,
                   val name: String)

data class KeywordNM (val id: Int,
                      val name: String)

data class ImageNM(val filePath: String,
                   val voteAverage: Float)

data class CrewNM(val creditId: String,
                  val job: String,
                  val name: String)


data class CastNM (val castId: Int,
                   val character: String,
                   val creditId: String,
                   val name: String,
                   val profilePath: String)

data class ProductionCompanyNM(val id: Int,
                               val name: String)