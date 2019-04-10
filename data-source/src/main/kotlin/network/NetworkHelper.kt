package ru.appkode.base.data.network

import com.squareup.moshi.Moshi
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.appkode.base.data.network.movie.ListWrapperConverterFactory
import ru.appkode.base.data.network.movie.MovieAPI

object NetworkHelper {

  const val API_MOVIES = "https://api.themoviedb.org/"
  const val API_KEY = "1774b3433274ef2d5a7baff526aa7f23"

  private val moshi = Moshi.Builder().build()

  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(BODY))
    .addInterceptor(object : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url().newBuilder()
          .addQueryParameter("api_key", API_KEY)
          .build()
        return chain.proceed(original.newBuilder().url(url).build())
      }
    }).build()

  private val movieApi = Retrofit.Builder()
    .baseUrl(API_MOVIES)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(ListWrapperConverterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(MovieAPI::class.java)

  fun getMovieApi(): MovieAPI = movieApi
}
