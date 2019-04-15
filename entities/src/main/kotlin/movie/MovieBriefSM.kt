package ru.appkode.base.entities.core.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
class MovieBriefSM(
  @PrimaryKey val id: Long,
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
    id = id,
    title = title,
    genres = genres ?: emptyList(),
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
    id = id,
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


