package ru.appkode.base.entities.core.common

interface ListWrapperNM<T> {
  fun getList(): List<T>?
}

data class PagedListWrapper<T>(
  val page: Int,
  val totalResults: Int,
  val totalPages: Int,
  val results: List<T>
) : ListWrapperNM<T> {
  override fun getList() = results
}