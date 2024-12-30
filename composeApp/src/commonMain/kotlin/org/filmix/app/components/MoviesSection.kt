package org.filmix.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.filmix.app.models.VideoData
import org.filmix.app.paging.IntPage
import org.filmix.app.paging.IntPagingSource

@Composable
fun MoviesSection(
    sectionName: String,
    moviesFlow: Flow<PagingData<VideoData>>
) {
    val movies = moviesFlow.collectAsLazyPagingItems()

    Column(modifier = Modifier.height(416.dp).padding(8.dp)) {
        Text(
            text = sectionName,
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { it.id },
                contentType = movies.itemContentType()
            ) { index ->
                val movie = movies[index] ?: return@items

                MovieOverview(movie)
            }

            item {
                LoadingIndicator(movies.loadState)
            }
        }
    }
}

data class MoviesSectionModel(
    val title: String,
    val movies: Flow<PagingData<VideoData>>
)

fun CoroutineScope.createSectionModel(
    title: String,
    pagingConfig: PagingConfig,
    source: suspend (Int?) -> IntPage<VideoData>
) = MoviesSectionModel(
    title = title,
    movies = Pager(pagingConfig) {
        IntPagingSource { source(it) }
    }.flow.cachedIn(this)
)