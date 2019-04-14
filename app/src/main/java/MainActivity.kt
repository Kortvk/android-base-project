package ru.appkode.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.activity_main.*
import movie.navigation.NavigationController
import ru.appkode.base.data.storage.DatabaseHelper

class MainActivity : AppCompatActivity() {

  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    DatabaseHelper.createDatabase(baseContext)
    router = Conductor.attachRouter(this, root_container, savedInstanceState)
    router.setRoot(RouterTransaction.with(NavigationController()))
  }

  override fun onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed()
    }
  }


}
