package pages


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import android.os.Parcel
import android.os.Parcelable
import ru.appkode.base.ui.R
import kotlinx.android.synthetic.main.search_controller.view.*


class SearchController() : Controller(), Parcelable {

  constructor(parcel: Parcel) : this() {
  }

  constructor(scearch: String) : this()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val view = inflater.inflate(R.layout.search_controller, container, false)
    view.search.onActionViewExpanded()


    return view
  }


  override fun writeToParcel(parcel: Parcel, flags: Int) {

  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<SearchController> {
    override fun createFromParcel(parcel: Parcel): SearchController {
      return SearchController(parcel)
    }

    override fun newArray(size: Int): Array<SearchController?> {
      return arrayOfNulls(size)
    }
  }
}