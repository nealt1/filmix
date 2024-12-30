package org.filmix.app.di

import androidx.paging.PagingConfig
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule(settingsFactory: Settings.Factory) = module {
    includes(
        repositoryModule,
        httpModule,
        screenModelModule
    )

    fun providePagingConfig(): PagingConfig = PagingConfig(
        pageSize = 20,
        enablePlaceholders = false
    )

    singleOf(::providePagingConfig)
    single { settingsFactory }
}