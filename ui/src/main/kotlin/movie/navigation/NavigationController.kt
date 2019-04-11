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
import kotlinx.android.synthetic.main.controller_navigation.view.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.requireView
import ru.appkode.base.ui.movie.details.MovieDetailedController
import ru.appkode.base.ui.movie.filter.FilterController
import ru.appkode.base.ui.movie.wishlist.WishListController

private const val ROUTER_STATES_KEY = "router_states"

val navigationEventsRelay: PublishRelay<Pair<Int, Bundle>> = PublishRelay.create()

class NavigationController : Controller() {

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
      navigationEventsRelay.accept(R.id.fab to Bundle()) }
    processNavigationIntents()
  }
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
      val controller = when (it.first) {
        R.id.menu_favorite -> WishListController(it.second)
        R.id.fab -> FilterController(it.second)
        R.id.menu_history -> null
        EVENT_ID_NAVIGATION_DETAILS -> MovieDetailedController(it.second)
        else -> null
      }
      if (controller != null)
        pushController(controller, it.first)
      else showSnackbar("Sorry, not yet implemented")
    }

  private fun pushController(controller: Controller, controllerId: Int) {
    saveCurrentControllerState(currentControllerId)
    val bundleState = getSavedStateForId(controllerId)
    currentControllerId = controllerId
    //childRouter.getControllerWithInstanceId()
    childRouter.pushController(RouterTransaction.with(controller))
  }

  /**
   * Try to restore the state (which was saved via [saveCurrentControllerState]) from the [routerStates].
   * @return either a valid [Bundle] state or null if no state is available
   */
  private fun getSavedStateForId(id: Int): Bundle? = routerStates?.get(id)
  /**
   * This will clear the state (hierarchy/backstack etc.) from the [childRouter] and goes back to root.
   */
  private fun clearStateFromChildRouter() {
    childRouter.setPopsLastView(true)
    childRouter.popToRoot()
    childRouter.popCurrentController()
    childRouter.setPopsLastView(false)
  }
  /**
   * This will save the current state of the tab (hierarchy/backstack etc.) from the [childRouter] in a [Bundle]
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
