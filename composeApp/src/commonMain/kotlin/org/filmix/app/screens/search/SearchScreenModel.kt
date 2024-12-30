package org.filmix.app.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.time.Duration.Companion.milliseconds
import org.filmix.app.data.VideoRepository
import org.filmix.app.paging.IntPagingSource

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchScreenModel(
    private val repository: VideoRepository,
    pagingConfig: PagingConfig
) : ScreenModel {

    var searchText by mutableStateOf("")
        private set

    val searchResults = snapshotFlow { searchText }
        .debounce(1_000.milliseconds)
        .filter { it.isNotEmpty() }
        .flatMapLatest { query ->
            Pager(pagingConfig) {
                IntPagingSource { page ->
                    repository.search(query, page)
                }
            }.flow
        }.cachedIn(screenModelScope)

    fun updateQuery(query: String) {
        searchText = query
    }
}