package org.filmix.app.di

import org.filmix.app.data.Downloader
import org.filmix.app.data.VideoRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::VideoRepository)
    singleOf(::Downloader)
}