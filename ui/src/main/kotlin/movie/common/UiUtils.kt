package ru.appkode.base.ui.movie.common

import android.view.View


object UiUtils {
  fun hitTest(view: View, x: Int, y: Int): Boolean =
     x >= view.left + (view.translationX + 0.5f) && x <= view.right + (view.translationX + 0.5f) &&
     y >= view.top + (view.translationY + 0.5f) && y <= view.bottom + (view.translationY + 0.5f)
}