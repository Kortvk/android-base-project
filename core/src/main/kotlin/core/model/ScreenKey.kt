package ru.appkode.base.ui.core.core.model

import android.os.Parcelable
import com.bluelinelabs.conductor.Controller

/**
 * A routing key representing the screen.
 * Create a Parcelable data class which inherits this class, add all required
 * screen arguments into that Parcelable and then call [createController] -
 * they will be passed into the [Controller] to use them.
 *
 * [ru.appkode.base.core.BaseMviController] implementations can access their keys via
 * [ru.appkode.base.core.BaseMviController.key] function
 *
 * Example implementation:
 *
 * ```
 * @Parcelize
 * data class OrderListKey(
 *   val listId: String
 * ) : ScreenKey() {
 *   override fun createController(): Controller {
 *     return OrderListController()
 *   }
 * }
 *
 * // ...to create Controller use ScreenKey.newController()
 *
 * val key = OrderListKey(listId = "some_id")
 * val controller = key.newController()
 *
 * // ...inside your Controller subclass use BaseMviController.key() to retrieve key values
 *
 * class OrderListController {
 *   override fun initializeView(...) {
 *     val key = key<OrderListKey>()
 *     Timber.d("list id is: ${key.listId}")
 *   }
 * }
 * ```
 */
abstract class ScreenKey : Parcelable {
  protected abstract fun createController(): Controller
  /**
   * Creates a new [Controller] by passing `this` Parcelable to it as an arguments to use.
   */
  fun newController(): Controller {
    return createController().apply {
      args.putParcelable(SCREEN_KEY_ARG_NAME, this@ScreenKey)
    }
  }
}

internal const val SCREEN_KEY_ARG_NAME = "key"
