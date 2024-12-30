package org.filmix.app.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.filmix.app.components.LoadingIndicator
import org.filmix.app.components.MovieOverview
import org.filmix.app.models.VideoData
import org.filmix.app.ui.LocalWindowSizeClass

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
                placeholder = { Text("Search films or series") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(searchFocusRequester),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
                trailingIcon = {
                    if (model.searchText.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.clickable { model.updateQuery("") }
                        )
                    }
                }
            )

            if (model.searchText.isNotEmpty()) {
                val movies = model.searchResults.collectAsLazyPagingItems()

                SearchResults(movies)

                if (movies.itemCount == 0) {
                    Text(
                        text = "Nothing found, try to change search query",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxSize()
                            .wrapContentHeight()
                    )
                }
            } else {
                Text(
                    text = "Write search query to see results",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxSize()
                        .wrapContentHeight()
                )
            }
        }
    }

    @Composable
    private fun SearchResults(movies: LazyPagingItems<VideoData>) {
        val windowSizeClass = LocalWindowSizeClass.current

        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
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
                    MovieOverview(movie)
                }

                item {
                    LoadingIndicator(movies.loadState) {
                        LinearProgressIndicator()
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
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
                    MovieOverview(movie)
                }

                item {
                    LoadingIndicator(movies.loadState) {
                        LinearProgressIndicator()
                    }
                }
            }
        }
    }
}