package com.plcoding.bookpedia.di

import com.plcoding.bookpedia.book.data.database.DatabaseFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

// provide the implementations of our dependencies on desktop

actual val platformModule: Module
    get() = module {
        // provide the thing that koin needs to instantiate http client
        single<HttpClientEngine> { OkHttp.create() }
        single { DatabaseFactory() }
    }