package org.filmix.app.di

import org.filmix.app.data.FileCache
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun fileModule(cacheDir: String) = module {
    println("Using cache $cacheDir")

    fun provideFileCache(): FileCache {
        return FileCache(cacheDir)
    }

    singleOf(::provideFileCache)
}