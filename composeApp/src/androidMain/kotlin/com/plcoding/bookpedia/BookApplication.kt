package com.plcoding.bookpedia

import android.app.Application
import com.plcoding.bookpedia.di.initKoin
import org.koin.android.ext.koin.androidContext

// we also need to register this Application in our Android manifest
class BookApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            // pass in this lambda to further configure this just for Android
            // This is where we would inject dependencies just for Android here

            // Something we need for Koin Android:
            // We have this concept of context that is unique to Android
            // and we need to instruct Koin to know this context so that if this context is needed
            // to construct certain dependencies. Koin knows how to retrieve this
            androidContext(this@BookApplication)
            // This is where we link our Android app class with Android context

        }
    }
}