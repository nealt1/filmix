package org.filmix.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.filmix.app.models.VideoData
import org.filmix.app.paging.IntPage
import org.filmix.app.paging.IntPagingSource

@Composable
fun MoviesSection(section: MoviesSectionModel) {
    val movies = section.movies.collectAsLazyPagingItems()

    Column {
        SectionTitle(
            text = section.title,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { "${section.title}-${it.id}" },
            ) { index ->
                val movie = movies[index] ?: return@items
                MovieOverview(movie)
            }

            item {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(220.dp).height(346.dp)
                ) {
                    LoadingIndicator(movies.loadState)
                }
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