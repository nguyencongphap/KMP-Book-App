package com.plcoding.bookpedia.di

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.bookpedia.book.data.database.DatabaseFactory
import com.plcoding.bookpedia.book.data.database.FavoriteBookDatabase
import com.plcoding.bookpedia.book.data.network.KtorRemoteBookDataSource
import com.plcoding.bookpedia.book.data.network.RemoteBookDataSource
import com.plcoding.bookpedia.book.data.repository.DefaultBookRepository
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailViewModel
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

// This  file is where we tell koin how to instantiate certain classes and inject them

// A module is a container of dependencies. Some dependencies can be shared across platform and
// some cannot.
// That's why we have many modules

// Koin is a so-called "service locator" meaning we just have pool of objects and when we need
// one, we just try to access it from that pool of objects. So, it's not really "Dependency Injection"
// as Dagger in Android, but we get the same benefits

// "expect" is like "interface", by using this, KMP expects us to provide implementations for this
// expectation in each platform
// this module contains platform-specific dependencies
expect val platformModule: Module

// singleton means a single instance
var sharedModule = module {
    // The order of injection matters

    // get() tells koin to try to get a dependency
    single { HttpClientFactory.create(get()) }

    // Inject the instance into components as the type of the interface instead of the implementation
    // because the interface is the type that the components expect
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()

    singleOf(::DefaultBookRepository).bind<BookRepository>()


    // Inject db. Here we use the common interface. Platforms will provide implementations
    single {
        get<DatabaseFactory>().create() // get Room db builder which we can use to configure
            .setDriver(BundledSQLiteDriver()) // what drives the db
            .build() // get the db that we can use to retrieve our DAO's, query, ...
    }
    // Inject DAO
    single { get<FavoriteBookDatabase>().favoriteBookDao }


    // View models are provided a bit differently in Koin
    // don't need to bind because we don't use abstraction for view models
    viewModelOf(::BookListViewModel)

    viewModelOf(::SelectedBookViewModel)

    viewModelOf(::BookDetailViewModel)
}