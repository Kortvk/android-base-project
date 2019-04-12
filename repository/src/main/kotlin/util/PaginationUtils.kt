package ru.appkode.base.repository.util

import androidx.paging.PagedList
import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.appkode.base.entities.core.common.PagedListWrapper

private typealias NetworkPageSupplier<T> = (Int) -> Single<PagedListWrapper<T>>
private typealias PersistancePageSupplier<T> = (Int) -> Single<PagedList<T>>

fun <T> paginatedObservable(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>, supplier: NetworkPageSupplier<T>): Observable<List<T>> {
  val maxPageSupplier = BehaviorSubject.createDefault(1)
    return reloadIntent.switchMap {
      Observable
        .zip(nextPageIntent.scan(1) { page, _ -> page + 1 }, maxPageSupplier,
          PaginationCreator {
              page, maxPage -> page to maxPage })
        .filterPagination(CAP_AT_MAXPAGE).flatMapSingle {
          supplier.invoke(it) }
        .doOnNext { maxPageSupplier.onNext(it.total_pages) }.map { it.results }
    }
}

private fun Observable<Pair<Int, Int>>.filterPagination(strategy: PaginationStrategy): Observable<Int> = strategy(this)
/**
 * Различные стратегии паджинации: На вход принимают пару (Запрашиваемая страница, Максимальная страница),
 * на выходе отдают новый номер страницы для запроса
 */
val CAP_AT_MAXPAGE: PaginationStrategy = { pages ->
  pages.filter {
    it.first <= it.second
  }
    .map {
      it.first
    }
}
val START_FROM_FIRST: PaginationStrategy = { pages ->
  pages.map { if (it.first <= it.second) it.first else 1 }
}

private typealias PaginationStrategy = (Observable<Pair<Int, Int>>) -> Observable<Int>
private typealias PaginationCreator = BiFunction<Int, Int, Pair<Int, Int>>