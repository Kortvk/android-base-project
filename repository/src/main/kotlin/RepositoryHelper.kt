package ru.appkode.base.repository

import movie.local.MockLocalMovieRepository
import movie.remote.RemoteMovieRepositoryImpl
import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.repository.movie.*
import ru.appkode.base.repository.movie.local.LocalMovieRepositoryImpl

object RepositoryHelper {
  fun getMovieService(): MovieService {
    return MockMovieServiceImpl(MockLocalMovieRepository, RemoteMovieRepositoryImpl(NetworkHelper.getMovieApi()))
  }
}
