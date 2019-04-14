package storage.db

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.Types.newParameterizedType


internal class Converters {

  val moshi = Moshi.Builder().build()
  val listOfStringsType = Types.newParameterizedType(
    List::class.java,
    String::class.java
  )

  @TypeConverter
  fun fromListOfStrings(value: List<String>?): String =
    moshi.adapter<List<String>>(listOfStringsType).toJson(value)

  @TypeConverter
  fun toListOfStrings(value: String): List<String?>? =
    moshi.adapter<List<String>>(listOfStringsType).fromJson(value)

}

