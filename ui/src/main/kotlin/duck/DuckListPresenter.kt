package ru.appkode.base.ui.duck

import io.reactivex.Observable
import ru.appkode.base.entities.core.duck.DuckUM
import ru.appkode.base.repository.duck.DuckRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.duck.DuckListScreen.View
import ru.appkode.base.ui.duck.DuckListScreen.ViewState

sealed class ScreenAction

data class UpdateList(val state: LceState<List<DuckUM>>) : ScreenAction()

class DuckListPresenter(
    schedulers: AppSchedulers,
    private val duckRepository: DuckRepository
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent { duckRepository.ducks() }
                .doLceAction { UpdateList(it) }
        )
    }

    override fun reduceViewState(
        previousState: ViewState,
        action: ScreenAction
    ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
        return when (action) {
            is UpdateList -> processUpdateList(previousState, action)
        }
    }

    private fun processUpdateList(
        previousState: ViewState,
        action: UpdateList
    ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(duckState = action.state) to null
    }

    override fun createInitialState(): ViewState {
        return ViewState(
            duckState = LceState.Loading()
        )
    }
}
