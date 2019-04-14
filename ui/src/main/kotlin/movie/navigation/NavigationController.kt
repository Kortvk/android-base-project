package movie.navigation

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.material.itemSelections
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.controller_navigation.view.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.requireView
import ru.appkode.base.ui.movie.details.MovieDetailedController
import ru.appkode.base.ui.movie.filter.FilterController
import ru.appkode.base.ui.movie.history.HistoryController
import ru.appkode.base.ui.movie.wishlist.WishListController

private const val ROUTER_STATES_KEY = "router_states"

val navigationEventsRelay: PublishRelay<Pair<Int, Bundle>> = PublishRelay.create()

class NavigationController : Controller() {

  var disposable = CompositeDisposable()
    get() = if (field.isDisposed) CompositeDisposable() else field

  private var routerStates: SparseArray<Bundle>? = SparseArray()

  private lateinit var childRouter: Router

  @IdRes
  private var currentControllerId: Int = -1

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    return inflater.inflate(R.layout.controller_navigation, container, false)
      .apply { childRouter = getChildRouter(child_container) }
  }

  override fun onAttach(view: View) {
    requireView.fab.setOnClickListener {
      navigationEventsRelay.accept(R.id.fab to Bundle())
    }
    disposable.add(processNavigationIntents())
  }

  override fun onDetach(view: View) = disposable.dispose()

  /**
   * Отправить нажатия кнопок в боттом навигации в глобальные события шины событий навигации
   * с menu res id в качестве параметра
   */
  private fun navigationIntents() = listOf(
    requireView.bottom_navigation.itemSelections().map { it.itemId to Bundle() },
    requireView.fab.clicks().map { R.id.fab to Bundle() },
    navigationEventsRelay
  )

  /**
   * Обработать события навигации
   */
  private fun processNavigationIntents() =
    Observable.merge(navigationIntents()).subscribe {
      setNavigationItemsSelectable(it.first != R.id.fab)
      val controller = when (it.first) {
        R.id.menu_favorite -> WishListController(it.second)
        R.id.fab -> {
          requireView.bottom_navigation.menu.setGroupCheckable(0, false, true)
          FilterController(it.second)
        }
        R.id.menu_history -> HistoryController(it.second)
        EVENT_ID_NAVIGATION_DETAILS -> MovieDetailedController(it.second)
        else -> null
      }
      if (controller != null)
        pushController(controller, it.first)
      else showSnackbar("Not implemented")
    }

  private fun pushController(controller: Controller, controllerId: Int) {
    saveCurrentControllerState(currentControllerId)
    val bundleState = getSavedStateForId(controllerId)
    currentControllerId = controllerId
    //childRouter.getControllerWithInstanceId()
    childRouter.setRoot(RouterTransaction.with(controller))
  }

  private fun setNavigationItemsSelectable(selector: Boolean) =
    requireView.bottom_navigation.menu.setGroupCheckable(0, selector, true)

  /**
   * Try to restore the content (which was saved via [saveCurrentControllerState]) from the [routerStates].
   * @return either a valid [Bundle] content or null if no content is available
   */
  private fun getSavedStateForId(id: Int): Bundle? = routerStates?.get(id)

  /**
   * This will clear the content (hierarchy/backstack etc.) from the [childRouter] and goes back to root.
   */
  private fun clearStateFromChildRouter() {
    childRouter.setPopsLastView(true)
    childRouter.popToRoot()
    childRouter.popCurrentController()
    childRouter.setPopsLastView(false)
  }

  /**
   * This will save the current content of the tab (hierarchy/backstack etc.) from the [childRouter] in a [Bundle]
   * and put it into the [routerStates] with the tab id as key
   */
  private fun saveCurrentControllerState(itemId: Int) {
    val routerBundle = Bundle()
    childRouter.saveInstanceState(routerBundle)
    routerStates?.put(itemId, routerBundle)
  }

  /** Save our [routerStates] into the instanceState so we don't loose them on orientation change */
  override fun onSaveInstanceState(outState: Bundle) {
    saveCurrentControllerState(currentControllerId)
    outState.putSparseParcelableArray(ROUTER_STATES_KEY, routerStates)
    super.onSaveInstanceState(outState)
  }

  /** Restore our [routerStates] */
  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    routerStates = savedInstanceState.getSparseParcelableArray(ROUTER_STATES_KEY)
  }

  fun showSnackbar(message: String) =
    view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
}

const val EVENT_ID_NAVIGATION_DETAILS = 0
const val DETAIL_SCREEN_ID_KEY = "DETAIL_SCREEN_ID_KEY"
