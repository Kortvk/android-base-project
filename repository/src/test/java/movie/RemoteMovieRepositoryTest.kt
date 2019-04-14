package movie

import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.entities.core.movie.MovieFilter
import movie.remote.RemoteMovieRepositoryImpl
import java.sql.Date

class RemoteMovieRepositoryTest {

  private val testTimeout: Long = 50
  private val testId: Long = 1891
  private val testTitle = "The Empire Strikes Back"
  private val testKeyword = "action"
  private val movieRepository = RemoteMovieRepositoryImpl(NetworkHelper.getMovieApi())
  private val testGenreId = 12
  private val testGenreTitle = "Adventure"
  private val testCastId: Long = 3
  private val testCastName = "Harrison Ford"
  private val filter = MovieFilter(
    genres = listOf(testGenreId.toString()),
    releaseDate = Date.valueOf("1979-01-01")..Date.valueOf("1981-01-01"),
    rating = 5.0F..9.9F,
    cast = listOf(testCastId.toString())
  )

  @Test
  fun getMovieById() {
    movieRepository.getMovieById(testId).assertResult {
      it.title == testTitle &&
        it.images?.posters?.isNotEmpty() ?: false &&
        it.credits?.cast?.isNotEmpty() ?: false
    }
  }

  @Test
  fun getGenres() {
    movieRepository.getGenres().assertResult { genres ->
      genres.isNotEmpty() &&
        genres.find { it.id == testGenreId }?.name == testGenreTitle
    }
  }

  @Test
  fun getPopularMovies() {
    movieRepository.getPopularMoviesPaged(Observable.just(Unit, Unit), Observable.just(Unit))
      .assertResultAtPage(1) {
        it.isNotEmpty()
      }
  }

  @Test
  fun searchCast() {
    movieRepository.searchCastPaged(testCastName, Observable.just(Unit), Observable.just(Unit))
      .assertResultAtPage(1) { casts ->
      casts.isNotEmpty() &&
        casts.find { it.id == testCastId }?.name == testCastName
    }
  }

  @Test
  fun filterMovies() {
    movieRepository.filterMoviesPaged(filter, Observable.just(Unit), Observable.just(Unit))
      .assertResultAtPage(1) { movies ->
      movies.isNotEmpty() &&
        movies.find { it.title == testTitle } != null
    }
  }

  @Test
  fun searchMovie() {
    movieRepository.searchMoviesPaged(testTitle, Observable.just(Unit), Observable.just(Unit)).assertResultAtPage(1) {
      it.isNotEmpty()
    }
  }

  @Test
  fun searchKeywords() {
    movieRepository.searchKeywordsPaged(testKeyword, Observable.just(Unit), Observable.just(Unit))
      .assertResultAtPage(1) {
      it.isNotEmpty() &&
        it.first().name.contains(testKeyword)
    }
  }

  private fun <T> Single<T>.assertResult(assertion: (T) -> Boolean) =
    this.test().awaitDone(testTimeout, java.util.concurrent.TimeUnit.SECONDS)
      .assertComplete().assertValue(assertion)

  private fun <T> Observable<T>.assertResultAtPage(page: Int, assertion: (T) -> Boolean) =
    this.test().awaitCount(page).assertValueAt(page - 1, assertion)
}