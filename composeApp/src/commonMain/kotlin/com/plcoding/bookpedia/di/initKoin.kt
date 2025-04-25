package com.plcoding.bookpedia.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// We will call this initKoin at the entry point of each platform
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this) // invoke this config extension function

        // declare the modules we want to use
        // we alreayd provided implementation for platformModule in each platform
        modules(sharedModule, platformModule)
    }
}