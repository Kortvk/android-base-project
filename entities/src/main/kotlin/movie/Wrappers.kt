package ru.appkode.base.entities.core.movie

import ru.appkode.base.entities.core.common.ListWrapperNM

data class GenresWrapper(
  val genres: List<GenreNM>?
) : ListWrapperNM<GenreNM> {
  override fun getList(): List<GenreNM>? = genres
}