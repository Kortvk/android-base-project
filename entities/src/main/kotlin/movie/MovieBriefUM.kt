package ru.appkode.base.entities.core.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

data class MovieDetailedUM(val id: Int)

@Entity(tableName = "movie")
class MovieBriefSM(
  @PrimaryKey val id: Int,
  var isInWishList: Boolean,
  val overview: String,
  val poster: String?,
  val backdrop: String?,
  val releaseDate: String,
  val title: String,
  val rating: Float,
  val sort: Int? = 1000
)

fun MovieBriefSM.toUIModel(): MovieBriefUM {
  return MovieBriefUM(
    id,
    isInWishList,
    listOf(""),
    overview,
    poster,
    backdrop,
    releaseDate,
    title,
    rating
  )
}

fun MovieBriefUM.toStorageModel(): MovieBriefSM {
  return MovieBriefSM(
    id,
    isInWishList,
    overview,
    poster,
    backdrop,
    releaseDate,
    title,
    rating
  )
}





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