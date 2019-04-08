package ru.appkode.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.activity_main.*
import ru.appkode.base.pages.HomeController
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.entities.core.movie.filter.MovieFilterController
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import ru.appkode.base.ui.duck.DuckListController
import ru.appkode.base.ui.movie.wishlist.MovieWishListController
import ru.appkode.base.ui.task.list.TaskListController

class MainActivity : AppCompatActivity() {

  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    router = Conductor.attachRouter(this, root_container, savedInstanceState)
    router.setRoot(RouterTransaction.with(HomeController())
  }

  override fun onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed()
    }
  }
}
