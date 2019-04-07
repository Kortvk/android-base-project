package pages


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import ru.appkode.base.ui.R


class HistoryController : Controller() {
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val view = inflater.inflate(R.layout.history_controller, container, false)
    return view
  }


}