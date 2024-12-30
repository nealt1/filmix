package org.filmix.app.di

import org.filmix.app.screens.favourite.FavouriteScreenModel
import org.filmix.app.screens.home.HomeScreenModel
import org.filmix.app.screens.player.PlayerScreenModel
import org.filmix.app.screens.search.SearchScreenModel
import org.filmix.app.app.Preferences
import org.filmix.app.screens.settings.SettingsScreenModel
import org.filmix.app.app.AppState
import org.filmix.app.screens.video.VideoScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val screenModelModule = module {
    factoryOf(::HomeScreenModel)
    factoryOf(::FavouriteScreenModel)
    factoryOf(::PlayerScreenModel)
    factoryOf(::SearchScreenModel)
    factoryOf(::SettingsScreenModel)
    factoryOf(::VideoScreenModel)
    singleOf(::AppState)
    singleOf(::Preferences)
}