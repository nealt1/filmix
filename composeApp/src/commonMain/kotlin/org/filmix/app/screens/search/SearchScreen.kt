package org.filmix.app.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.composeapp.generated.resources.*
import org.filmix.app.components.LoadingMovie
import org.filmix.app.components.MovieOverview
import org.filmix.app.components.TextCenter
import org.filmix.app.models.VideoData
import org.filmix.app.ui.LocalWindowSizeClass
import org.filmix.app.ui.conditional
import org.jetbrains.compose.resources.stringResource

object SearchScreen : Screen {

    override val key = "SearchScreen"

    @Composable
    override fun Content() {
        val model = getScreenModel<SearchScreenModel>()
        val searchFocusRequester = FocusRequester()

        LaunchedEffect(Unit) {
            searchFocusRequester.requestFocus()
        }

        Column(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = model.searchText,
                onValueChange = { model.updateQuery(it) },
                placeholder = { Text(stringResource(Res.string.search_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(searchFocusRequester),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
                trailingIcon = {
                    if (model.searchText.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.search_clear),
                            modifier = Modifier.clickable { model.updateQuery("") }
                        )
                    }
                }
            )

            if (model.searchText.isNotEmpty()) {
                val movies = model.searchResults.collectAsLazyPagingItems()

                SearchResults(movies)

                if (movies.itemCount == 0) {
                    TextCenter(stringResource(Res.string.search_no_results))
                }
            } else {
                TextCenter(stringResource(Res.string.search_empty))
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun SearchResults(movies: LazyPagingItems<VideoData>) {
        val windowSizeClass = LocalWindowSizeClass.current
        val firstItemFocusRequester = remember(movies) { FocusRequester() }

        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .focusRestorer { firstItemFocusRequester },
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    count = movies.itemCount,
                    key = movies.itemKey { it.id },
                    contentType = movies.itemContentType()
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
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth()
                    .focusRestorer { firstItemFocusRequester },
                columns = GridCells.FixedSize(size = 220.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                items(
                    count = movies.itemCount,
                    key = movies.itemKey { it.id },
                    contentType = movies.itemContentType()
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
}