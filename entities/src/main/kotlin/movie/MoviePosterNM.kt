package ru.appkode.base.entities.core.movie

class MoviePosterNM(
    val id: Int,
    val backdrops: ArrayList<Backdrop>,
    val posters: ArrayList<Poster>
) {
    class Backdrop(
        val aspect_ratio: Float,
        val file_path: String,
        val height: Int,
        val iso_639_1: String?,
        val vote_average: Int,
        val vote_count: Int,
        val width: Int
    )

    class Poster(
        val aspect_ratio: Float,
        val file_path: String,
        val height: Int,
        val iso_639_1: String?,
        val vote_average: Int,
        val vote_count: Int,
        val width: Int
    )
}