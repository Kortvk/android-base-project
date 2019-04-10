package movie.navigation

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hannesdorfmann.mosby3.MviController
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.home_controller.view.*
import movie.history.HistoryController
import movie.filter.MovieFilterController
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.movie.details.MovieDetailedController
import ru.appkode.base.ui.movie.wishlist.MovieWishListController

private const val ROUTER_STATES_KEY = "router_states"
private const val SEARCH_CONTROLLER = "search_controller"

class NavigationController : Controller(), BottomNavigationView.OnNavigationItemSelectedListener {

  val eventsRelay: PublishRelay<Int> = PublishRelay.create()

  private var routerStates: SparseArray<Bundle>? = SparseArray()

  private lateinit var childRouter: Router

  @IdRes
  private var currentSelectedItemId: Int = -1

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    return inflater.inflate(R.layout.home_controller, container, false)
    .apply {
      childRouter = getChildRouter(child_container)
      bottom_navigation.setOnNavigationItemSelectedListener(this@NavigationController)
      if (routerStates?.size() == 0) {
        childRouter.setRoot(RouterTransaction.with(MovieWishListController()))
      } else {
        childRouter.rebindIfNeeded()
      }
      bottom_navigation.selectedItemId = R.id.menu_favorite
      fab.setOnClickListener {
        childRouter.pushController(RouterTransaction.with(MovieFilterController()))
        fab.hide()
      }
    }
  }

  override fun handleBack(): Boolean {
    if (childRouter.getControllerWithTag(SEARCH_CONTROLLER) == null) {
      view?.fab?.show()
    }
    return super.handleBack()
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    saveStateFromCurrentTab(currentSelectedItemId)
    currentSelectedItemId = item.itemId
    clearStateFromChildRouter()
    val bundleState = tryToRestoreStateFromNewTab(currentSelectedItemId)
    return when (item.itemId) {
      R.id.menu_favorite -> {
        childRouter.replaceTopController(RouterTransaction.with(MovieWishListController()))
        view?.fab?.show()
        true
      }
      R.id.menu_history -> {
        childRouter.replaceTopController(RouterTransaction.with(HistoryController()))
        true
      }
      else -> false
    }
  }

  /**
   * Try to restore the state (which was saved via [saveStateFromCurrentTab]) from the [routerStates].
   * @return either a valid [Bundle] state or null if no state is available
   */
  private fun tryToRestoreStateFromNewTab(itemId: Int): Bundle? {
    return routerStates?.get(itemId)
  }

  /**
   * This will clear the state (hierarchy/backstack etc.) from the [childRouter] and goes back to root.
   */
  private fun clearStateFromChildRouter() {
    childRouter.setPopsLastView(true) /* Ensure the last view can be removed while we do this */
    childRouter.popToRoot()
    childRouter.popCurrentController()
    childRouter.setPopsLastView(false)
  }

  /**
   * This will save the current state of the tab (hierarchy/backstack etc.) from the [childRouter] in a [Bundle]
   * and put it into the [routerStates] with the tab id as key
   */
  private fun saveStateFromCurrentTab(itemId: Int) {
    val routerBundle = Bundle()
    childRouter.saveInstanceState(routerBundle)
    routerStates?.put(itemId, routerBundle)
  }

  /**
   * Save our [routerStates] into the instanceState so we don't loose them on orientation change
   */
  override fun onSaveInstanceState(outState: Bundle) {
    saveStateFromCurrentTab(currentSelectedItemId)
    outState.putSparseParcelableArray(ROUTER_STATES_KEY, routerStates)
    super.onSaveInstanceState(outState)
  }

  /**
   * Restore our [routerStates]
   */
  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    routerStates = savedInstanceState.getSparseParcelableArray(ROUTER_STATES_KEY)
  }

  fun navigateToFilter() {  }
  fun navigateToWishList() {  }
  fun navigateToHistory() {  }
  fun navigateToDetails() {  }
}
