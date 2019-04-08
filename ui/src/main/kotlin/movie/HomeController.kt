package movie

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
import kotlinx.android.synthetic.main.home_controller.view.*
import movie.history.HistoryController
import ru.appkode.base.entities.core.movie.filter.MovieFilterController
import ru.appkode.base.ui.R
import ru.appkode.base.ui.movie.wishlist.MovieWishListController

private const val ROUTER_STATES_KEY = "router_states"
private const val SEARCH_CONTROLLER = "search_controller"

class HomeController : Controller(), BottomNavigationView.OnNavigationItemSelectedListener {
  /**
   * This will hold all the information about the tabs.
   * This needs to be a var because we have to reassign it in [onRestoreInstanceState]
   */
  private var routerStates: SparseArray<Bundle>? = SparseArray()

  private lateinit var childRouter: Router
  /**
   * This is the current selected item id from the [BottomNavigationView]
   */
  @IdRes
  private var currentSelectedItemId: Int = -1

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val view = inflater.inflate(R.layout.home_controller, container, false)
    return view.apply {
      childRouter = getChildRouter(child_container)

      bottom_navigation.setOnNavigationItemSelectedListener(this@HomeController)

      // We have not a single bundle/state saved.
      // Looks like this [HomeController] was created for the first time
      if (routerStates?.size() == 0) {
        // Select the first item
        childRouter.setRoot(RouterTransaction.with(MovieWishListController()))
      } else {
        // We have something in the back stack. Maybe an orientation change happen?
        // We can just rebind the current router
        childRouter.rebindIfNeeded()
      }

      bottom_navigation.selectedItemId = R.id.menu_favorite

      fab.setOnClickListener {
        //childRouter.replaceTopController(RouterTransaction.with(MovieFilterController()))
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

  /**
   * Listener which get called if a item from the [BottomNavigationView] is selected
   */
  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    // Save the state from the current tab so that we can restore it later - if needed
    saveStateFromCurrentTab(currentSelectedItemId)
    currentSelectedItemId = item.itemId
    // Clear all the hierarchy and backstack from the router. We have saved it already in the [routerStates]
    clearStateFromChildRouter()
    // Try to restore the state from the new selected tab.
    val bundleState = tryToRestoreStateFromNewTab(currentSelectedItemId)

    //TODO: что-то тут не так. Если раскомментить, то после переход к поиску, на экран с вишлистом вернуться не получится
//    if (bundleState is Bundle) {
//      // We have found a state (hierarchy/backstack etc.) and can just restore it to the [childRouter]
//      childRouter.restoreInstanceState(bundleState)
//      childRouter.rebindIfNeeded()
//      return true
//    }

    // There is no state (hierarchy/backstack etc.) saved in the [routerBundles].
    // We have to create a new [Controller] and set as root
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
}