package org.filmix.app.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import org.filmix.app.state.LoadingValue

@Composable
fun LoadingIndicator(
    loadState: CombinedLoadStates,
    progressIndicator: @Composable () -> Unit = { CircularProgressIndicator() }
) {
    loadState.source.apply {
        when {
            refresh is LoadState.Loading || append is LoadState.Loading -> {
                progressIndicator()
            }

            append is LoadState.Error -> {
                val error = (append as LoadState.Error).error
                ShowError(error)
            }
        }
    }
}

@Composable
fun <T> LoadingIndicator(
    value: LoadingValue<T>,
    loading: @Composable () -> Unit = { CircularProgressIndicator() },
    failed: @Composable (Throwable) -> Unit = { ShowError(it) },
    content: @Composable T.() -> Unit
) {
    when (value) {
        LoadingValue.Loading -> {
            loading()
        }

        is LoadingValue.Failure -> {
            failed(value.error)
        }

        is LoadingValue.Loaded -> {
            content(value.value)
        }
    }
}

@Composable
fun ShowError(exception: Throwable) {
    exception.message?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall
        )
    }
}