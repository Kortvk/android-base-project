package ru.appkode.base.data.network

import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.appkode.base.data.network.duck.DuckApi
import ru.appkode.base.data.network.movie.MovieAPI
import ru.appkode.ui.core.BuildConfig

object NetworkHelper {
  const val DUCK_API_BASE_URL = "https://duck-appkode.herokuapp.com"
  const val DUCK_API_IMAGE_URL = "$DUCK_API_BASE_URL/static"


  const val API_MOVIES = "https://api.themoviedb.org/3/"
  const val API_KEY = "1774b3433274ef2d5a7baff526aa7f23"

  private val moshi = Moshi.Builder()
    .build()

  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
    .addInterceptor(object : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url().newBuilder()
          .addQueryParameter("api_key", API_KEY)
          .build()

        return chain.proceed(original.newBuilder().url(url).build())
      }
    })
    .build()

  private val duckApi = Retrofit.Builder()
    .baseUrl(DUCK_API_BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(DuckApi::class.java)

  private val movieApi = Retrofit.Builder()
    .baseUrl(API_MOVIES)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(MovieAPI::class.java)

  fun getDuckApi(): DuckApi = duckApi
  fun getMovieApi(): MovieAPI = movieApi
}
