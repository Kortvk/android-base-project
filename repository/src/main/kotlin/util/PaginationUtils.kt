package ru.appkode.base.repository.util

import androidx.paging.PagedList
import io.reactivex.*
import ru.appkode.base.entities.core.common.PagedListWrapper

typealias NetworkPageSupplier<T> = (Int) -> Single<PagedListWrapper<T>>
typealias PersistancePageSupplier<T> = (Int) -> Single<PagedList<T>>

fun <T> paginatedFlowable(intent: Observable<Unit>, supplier: NetworkPageSupplier<T>) =
  Flowable.create<List<T>>(PaginatedOnSubscribe(intent, supplier), BackpressureStrategy.DROP)

class PaginatedOnSubscribe<T>(
  private val intents: Observable<Unit>,
  private val supplier: NetworkPageSupplier<T>
) : FlowableOnSubscribe<List<T>> {
  override fun subscribe(emitter: FlowableEmitter<List<T>>) {
    //Отправить первую страницу при подписке без запроса
    supplier.invoke(1).doOnSuccess { emitter.onNext(it.results) }.subscribe()

    intents.scan(1) { nextPage, _ -> nextPage + 1 }
      .flatMapSingle { supplier.invoke(it) }
      .doOnNext { if (it.page + 1 <= it.totalPages) emitter.onNext(it.results) }
      .doOnError { emitter.onError(it) }.subscribe()
  }
}