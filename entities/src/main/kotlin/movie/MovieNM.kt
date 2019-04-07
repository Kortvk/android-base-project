package ru.appkode.base.entities.core.movie

data class MovieNM(
    val adult: Boolean,
    val backdrop_path: String?,
//val belongs_to_collection:object?,
    val budget: Int,
    val genres: ArrayList<Genre>,
    val homepage: String?,
    val id: Int,
    val imdb_id: String?,
    val original_language: String,
    val original_title: String,
    val overview: String?,
    val popularity: Float,
    val poster_path: String?,
    val production_companies: ArrayList<ProductionCompany>,
    val production_countries: ArrayList<ProductionCountry>,
    val release_date: String,
    val revenue: Int,
    val runtime: Int?,
    val spoken_languages: ArrayList<SpokenLanguage>,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    val vote_average: Float,
    val vote_count: Int
) {
    data class Genre(
        val id: Int,
        val name: String
    )

    data class ProductionCompany(
        val name: String,
        val id: Int,
        val logo_path: String?,
        val origin_country: String
    )

    data class ProductionCountry(
        val iso_3166_1: String,
        val name: String
    )

    data class SpokenLanguage(
        val iso_639_1: String,
        val name: String
    )
}