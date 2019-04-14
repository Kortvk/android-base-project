package ru.appkode.base.data.network.movie

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import ru.appkode.base.entities.core.common.ListWrapperNM
import ru.appkode.base.entities.core.movie.GenreNM
import ru.appkode.base.entities.core.movie.GenresWrapper
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ListWrapperConverterFactory : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> =
    if (type is ParameterizedType) {
      when (type.actualTypeArguments[0]) {
        GenreNM::class.java -> getConverter<GenresWrapper, GenreNM>(retrofit, annotations)
        else -> retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
      }
    } else {
      retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
    }

  private inline fun <reified T : ListWrapperNM<V>, V> getConverter(
    retrofit: Retrofit,
    annotations: Array<Annotation>
  ): Converter<ResponseBody, List<V?>> {

    val delegate = retrofit.nextResponseBodyConverter<T>(this, T::class.java, annotations)
    return WrapperListConverter<T, V>(delegate)
  }
}

internal class WrapperListConverter<T : ListWrapperNM<V>, V>(private val delegate: Converter<ResponseBody, T>) :
  Converter<ResponseBody, List<V?>> {
  override fun convert(responseBody: ResponseBody): List<V?>? =
    delegate.convert(responseBody)?.getList()
}
