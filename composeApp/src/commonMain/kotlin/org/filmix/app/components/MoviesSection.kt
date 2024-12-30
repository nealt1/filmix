package org.filmix.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
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
import org.filmix.app.ui.conditional
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MoviesSection(section: MoviesSectionModel) {
    val movies = section.movies.collectAsLazyPagingItems()
    val firstItemFocusRequester = remember { FocusRequester() }

    Column {
        SectionTitle(stringResource(section.title))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.focusRestorer { firstItemFocusRequester }
        ) {
            items(
                count = movies.itemCount,
                key = movies.itemKey { "${section.title.key}-${it.id}" },
            ) { index ->
                val movie = movies[index] ?: return@items
                MovieOverview(
                    video = movie,
                    modifier = Modifier.conditional(index == 0) {
                        focusRequester(firstItemFocusRequester)
                    }
                )
            }

            item {
                LoadingMovie(movies.loadState)
            }
        }
    }
}

data class MoviesSectionModel(
    val title: StringResource,
    val movies: Flow<PagingData<VideoData>>
)

fun CoroutineScope.createSectionModel(
    title: StringResource,
    pagingConfig: PagingConfig,
    source: suspend (Int?) -> IntPage<VideoData>
) = MoviesSectionModel(
    title = title,
    movies = Pager(pagingConfig) {
        IntPagingSource { source(it) }
    }.flow.cachedIn(this)
)