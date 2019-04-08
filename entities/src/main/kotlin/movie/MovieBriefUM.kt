package ru.appkode.base.entities.core.movie

data class MovieDetailedUM(val id: Int)

data class MovieBriefUM(
  val id: Int,
  var isInWishList: Boolean,
  val genres: List<String?>,
  val overview: String,
  val poster: String?,
  val backdrop: String?,
  val releaseDate: String,
  val title: String,
  val rating: Float
) {
    data class Genre(
        val id: Int,
        val name: String
    )

    data class ProductionCompany(
        val name: String,
        val id: Int,
        val logo_path: String?,
        val origin_country: String
    )

    data class ProductionCountry(
        val iso_3166_1: String,
        val name: String
    )

    data class SpokenLanguage(
        val iso_639_1: String,
        val name: String
    )
}