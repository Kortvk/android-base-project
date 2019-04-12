package ru.appkode.base.ui.movie.common

import android.view.View


object UiUtils {
  fun hitTest(view: View, x: Int, y: Int): Boolean {
    val offsetX = (view.translationX + 0.5f).toInt()
    val offsetY = (view.translationY + 0.5f).toInt()
    return x >= view.left + offsetX && x <= view.right + offsetX &&
        y >= view.top + offsetY && y <= view.bottom + offsetY
  }
}