package ru.appkode.base.ui.core.core

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.skipFirstIf
import ru.appkode.base.ui.core.core.util.toLceEventObservable
import ru.appkode.ui.core.BuildConfig
import timber.log.Timber

@Suppress("TooManyFunctions")
abstract class BasePresenter<V : MviView<VS>, VS, A : Any>(
  protected val schedulers: AppSchedulers,
  private val skipRenderOfInitialState: Boolean = false,
  private val logStateChanges: Boolean = !BuildConfig.RELEASE
) : MviBasePresenter<V, VS>() {

  /**
   * Emits output actions from commands produced by reducer.
   * These actions will be delivered to the start of MVI-loop and be processed
   * by reducers.
   */
  private val outputActions = PublishRelay.create<A>()
  private val disposables = CompositeDisposable()

  final override fun bindIntents() {
    val stateChanges = Observable.merge(createIntents().plus(outputActions))
      .scan(Pair<VS, Command<Observable<A>>?>(createInitialState(), null)) { s, a ->
        val (ps, _) = s
        val (ns, cmd) = reduceViewState(ps, a)

        if (logStateChanges) logStateChanges(ns, a)

        Pair(ns, cmd)
      }
      .skipFirstIf(skipRenderOfInitialState)
      .observeOn(schedulers.ui)
      .doAfterNext { (_, cmd) ->
        cmd?.invoke()?.subscribe(outputActions)
      }
      .map {
          (vs, _) ->
        vs
      }
      .distinctUntilChanged()

    subscribeViewState(stateChanges) { view, viewState ->
      view.render(viewState) }
  }

  /**
   * Метод, возвращющий результат применения заданной команды к текущему состоянию
   * @param previousState состояние экрана до применения действия
   * @param action действие
   * @return Новое состояние и команда для его получения
   */
  protected abstract fun reduceViewState(previousState: VS, action: A): Pair<VS, Command<Observable<A>>?>

  protected abstract fun createIntents(): List<Observable<out A>>

  protected abstract fun createInitialState(): VS

  private fun logStateChanges(ns: VS, a: A) {
    Timber.tag(this.javaClass.simpleName)
    Timber.d(
      """
            ┌─────────────────────────────────────────────────────────
            │ Reduce after action:
            │
            │ $a
            │
            │ New state is:
            │
            │ $ns
            └──────────────────────────────────────────────────────────
            """.trimIndent()
    )
  }

  protected fun <T> Observable<T>.doLceAction(
    actionCreator: (LceState<T>) -> A
  ): Observable<A> {
    return this
      .toLceEventObservable(actionCreator)
      .observeOn(schedulers.ui)
  }

  protected fun <T> Observable<T>.doAction(
    actionCreator: (T) -> A
  ): Observable<A> {
    return this
      .map { actionCreator(it) }
      .observeOn(schedulers.ui)
  }

  protected fun Completable.doLceAction(
    actionCreator: (LceState<Unit>) -> A
  ): Observable<A> {
    return this
      .toLceEventObservable(actionCreator)
      .observeOn(schedulers.ui)
  }

  protected fun Completable.doAction(
    actionCreator: () -> A
  ): Completable {
    return this
      .observeOn(schedulers.ui)
  }

  /**
   * Функция для создания события при первом открытии экрана
   * Когда не важна последоватьльность выполнения intent'ов
   * Её следует использовать в функции ```createIntents()```
   */
  fun <T> bootstrapper(action: () -> Unit): Observable<T> {
    return Completable
      .fromAction(action)
      .toObservable()
  }

  /**
   * Функция для создания события при первом открытии экрана
   * Когда важно чтобы `action` выполнился после подписки на Observable
   * Её следует использовать в функции ```createIntents()```
   */
  fun <T> Observable<T>.withBootstrapper(action: () -> Unit): Observable<T> {
    return mergeWith(bootstrapper(action))
  }
}
