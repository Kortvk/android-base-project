package ru.appkode.base.repository

import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.repository.books.BooksNetworkRepositoryImpl
import ru.appkode.base.repository.duck.DuckRepository
import ru.appkode.base.repository.duck.DuckRepositoryImpl
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.repository.task.TaskRepositoryImpl
import ru.appkode.base.ui.core.core.util.AppSchedulers

object RepositoryHelper {
  fun getTaskRepository(schedulers: AppSchedulers): TaskRepository {
    return TaskRepositoryImpl(schedulers, DatabaseHelper.getTaskPersistence())
  }

  fun getDuckRepository(): DuckRepository {
    return DuckRepositoryImpl(NetworkHelper.getDuckApi())
  }

  fun getBooksNetworkRepository(schedulers: AppSchedulers): BooksNetworkRepository {
    return BooksNetworkRepositoryImpl(schedulers, NetworkHelper.getBooksApi())
  }
}
