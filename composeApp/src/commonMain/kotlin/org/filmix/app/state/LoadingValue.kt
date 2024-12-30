package org.filmix.app.state

import androidx.compose.runtime.Immutable

@Immutable
sealed class LoadingValue<out T> {
    data class Loaded<T>(val value: T) : LoadingValue<T>()

    data object Loading : LoadingValue<Nothing>()
    data class Failure<T>(val error: Throwable) : LoadingValue<T>()
}