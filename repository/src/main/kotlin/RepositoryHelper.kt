package ru.appkode.base.repository

import movie.local.MockLocalMovieRepository
import movie.local.MovieRepositoryImpl
import movie.remote.RemoteMovieRepositoryImpl
import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.repository.movie.*

object RepositoryHelper {
  fun getMovieService(): MovieService {
    return MockMovieServiceImpl(MovieRepositoryImpl(), RemoteMovieRepositoryImpl(NetworkHelper.getMovieApi()))
  }
}
