package ru.appkode.base

import android.app.Application
import android.os.Looper
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class KodeApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    configureRxAndroid()
    configureLogging()
    AndroidThreeTen.init(this)
  }

  private fun configureRxAndroid() {
    // see https://medium.com/@sweers/rxandroids-new-async-api-4ab5b3ad3e93
    RxAndroidPlugins.setInitMainThreadSchedulerHandler {
      AndroidSchedulers.from(Looper.getMainLooper(), true)
    }
  }

  private fun configureLogging() {
    // смотри app/build.gradle, раздел buildTypes для информации о том, в каких сборках включен logging и прочее
    if (!BuildConfig.RELEASE) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
