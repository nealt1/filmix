package org.filmix.app.di

import org.filmix.app.Platform
import org.filmix.app.data.FileCache
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun platformModule(platform: Platform) = module {
    single { platform }
    singleOf(::FileCache)
}