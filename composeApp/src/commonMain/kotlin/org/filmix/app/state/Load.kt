package org.filmix.app.state

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T>.load(loadFunction: suspend (T) -> R): Flow<LoadingValue<R>> {
    return flatMapLatest {
        flow {
            emit(LoadingValue.Loading)
            try {
                val result = loadFunction(it)
                emit(LoadingValue.Loaded(result))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(LoadingValue.Failure(e))
            }
        }
    }
}

inline fun <T, R> Flow<LoadingValue<T>>.mapLoaded(
    crossinline transform: suspend (value: T) -> R
): Flow<LoadingValue<R>> = map {
    when (it) {
        is LoadingValue.Loaded -> LoadingValue.Loaded(transform(it.value))
        is LoadingValue.Loading -> LoadingValue.Loading
        is LoadingValue.Failure -> LoadingValue.Failure(it.error)
    }
}

suspend fun <T> Flow<LoadingValue<T>>.collectLoaded(
    collector: FlowCollector<T>
) = collect {
    when (it) {
        is LoadingValue.Loaded -> collector.emit(it.value)
        else -> {}
    }
}