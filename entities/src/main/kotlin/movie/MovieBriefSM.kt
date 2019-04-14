package ru.appkode.base.entities.core.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
class MovieBriefSM(
  @PrimaryKey val id: Int,
  var isInWishList: Boolean,
  var isInHistory: Boolean,
  val overview: String,
  val poster: String?,
  val backdrop: String?,
  val releaseDate: String,
  val title: String,
  val votes: Int,
  val rating: Float,
  val genres: List<String?>?,
  val sort: Int? = 1000
)

fun MovieBriefSM.toUIModel(): MovieBriefUM {
  return MovieBriefUM(
    id = id.toLong(),
    title = title,
    genres = emptyList(),
    overview = overview,
    poster = poster,
    backdrop = backdrop,
    releaseDate = releaseDate,
    rating = rating,
    votes = votes,
    isInWishList = isInWishList,
    isInHistory = isInHistory
  )
}

fun MovieBriefUM.toStorageModel(): MovieBriefSM {
  return MovieBriefSM(
    id = id.toInt(),
    title = title,
    genres = genres,
    overview = overview,
    poster = poster,
    backdrop = backdrop,
    releaseDate = releaseDate,
    rating = rating,
    votes = votes,
    isInWishList = isInWishList,
    isInHistory = isInHistory
  )
}


