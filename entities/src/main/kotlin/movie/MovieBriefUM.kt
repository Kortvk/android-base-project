package ru.appkode.base.entities.core.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


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
    id.toLong(),
    isInWishList,
    listOf(""),
    overview,
    poster,
    backdrop,
    releaseDate,
    title,
    rating,
    0
  )
}

fun MovieBriefUM.toStorageModel(): MovieBriefSM {
  return MovieBriefSM(
    id.toInt(),
    isInWishList,
    overview,
    poster,
    backdrop,
    releaseDate,
    title,
    rating
  )
}


