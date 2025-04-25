package com.plcoding.bookpedia.di

import com.plcoding.bookpedia.book.data.database.DatabaseFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

// provide the implementations of our dependencies on android


actual val platformModule: Module
    get() = module {
        // provide the thing that koin needs to instantiate http client
        single<HttpClientEngine> { OkHttp.create() }

        // here is where we send the context needed to build Room db on android
        // androidApplication() gives us the context that we introduced to koin by saying
        // initKoin { androidContext(this@BookApplication } in the entry point of our app
        single { DatabaseFactory(androidApplication()) }
    }