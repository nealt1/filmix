package org.filmix.app.screens.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.filmix.app.Platform
import org.filmix.app.components.ExpandableText
import org.filmix.app.components.LoadingIndicator
import org.filmix.app.components.MaterialIcons
import org.filmix.app.components.MovieOverview
import org.filmix.app.components.SectionTitle
import org.filmix.app.data.DownloadState
import org.filmix.app.models.UserInfo
import org.filmix.app.models.VideoDetails
import org.filmix.app.models.WatchedVideoData
import org.filmix.app.screens.player.PlayerScreen
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalUserInfo
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.LocalWindowSizeClass
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
data class VideoScreen(private val videoId: Int) : Screen {

    override val key = "VideoScreen(${videoId})"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val windowSizeClass = LocalWindowSizeClass.current
        val platform = LocalPlatform.current
        val model = getScreenModel<VideoScreenModel> { parametersOf(videoId) }

        val videoDetails by model.videoDetails.collectAsState()

        LoadingIndicator(
            value = videoDetails,
            loading = {
                DisplayLoadingVideo(
                    navigationIcon = { NavigationIcon(platform, navigator) }
                )
            },
            content = {
                DisplayLoadedVideo(
                    video = this,
                    navigationIcon = { NavigationIcon(platform, navigator) },
                    model, windowSizeClass
                )
            }
        )
    }

    @Composable
    private fun DisplayLoadingVideo(
        navigationIcon: @Composable () -> Unit
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = navigationIcon
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Composable
    private fun DisplayLoadedVideo(
        video: VideoDetails,
        navigationIcon: @Composable () -> Unit = {},
        model: VideoScreenModel,
        windowSizeClass: WindowSizeClass
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = navigationIcon,
                    actions = {
                        IconToggleButton(
                            onCheckedChange = { model.toggleFavourite() },
                            checked = model.favourite.value
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favourite")
                        }

                        IconToggleButton(
                            onCheckedChange = { model.toggleSaved() },
                            checked = model.saved.value
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Watch later")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
            ) {
                item {
                    VideoPlaylist(video, model)
                }

                if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            VideoPoster(video, modifier = Modifier.height(313.dp).width(220.dp))

                            Spacer(modifier = Modifier.size(size = 16.dp))

                            VideoDetails(
                                video,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    item {
                        VideoPoster(video)
                    }
                    item {
                        VideoDetails(video)
                    }
                }

                if (video.relates.isNotEmpty()) {
                    item {
                        SectionTitle("Related", modifier = Modifier.padding(bottom = 8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(video.relates.size) {
                                val videoRelated = video.relates[it]
                                MovieOverview(videoRelated)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationIcon(
        platform: Platform,
        navigator: Navigator
    ) {
        if (!platform.isTV && navigator.canPop) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }

    @Composable
    private fun VideoPlaylist(
        video: VideoDetails,
        model: VideoScreenModel,
        modifier: Modifier = Modifier,
    ) {
        val playlist = remember {
            PlaylistConverter.getPlaylist(video.player_links)
        }

        when (playlist) {
            is Playlist.Movie -> PlayMovie(modifier, video.title, playlist, model)
            is Playlist.Series -> PlaySeries(modifier, video.title, playlist, model)
            is Playlist.Empty -> {}
        }
    }

    @Composable
    private fun VideoPoster(video: VideoDetails, modifier: Modifier = Modifier) {
        val posterResource = asyncPainterResource(data = video.poster)
        KamelImage(
            resource = posterResource,
            contentDescription = null,
            onLoading = { progress ->
                CircularProgressIndicator({ progress })
            },
            modifier = modifier
        )
    }

    @Composable
    private fun VideoDetails(video: VideoDetails, modifier: Modifier = Modifier) = with(video) {
        Column(modifier) {
            Text("Category: ${categories.joinToString()}")
            Text("Director: ${directors.joinToString()}")
            Text("Actors: ${actors.joinToString()}")
            Text("Country: ${countries.joinToString()}")
            Text("Year: $year")
            Text("Duration: $duration minutes")

            val shortStory = short_story.replace("<br />", "\n")
            ExpandableText(
                text = shortStory,
                minimizedMaxLines = 5,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    @Composable
    private fun PlayMovie(
        modifier: Modifier = Modifier,
        title: String,
        playlist: Playlist.Movie,
        model: VideoScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val platform = LocalPlatform.current
        val windowSize = LocalWindowSize.current
        val user = LocalUserInfo.current
        val buttonFocusRequester = FocusRequester()
        val settings = model.videoSettings
        val download = model.download

        val translation = remember {
            mutableStateOf(
                value = getTranslation(settings, playlist.translations)
            )
        }
        val downloadState by remember { download.downloadState }

        LaunchedEffect(Unit) {
            buttonFocusRequester.requestFocus()
        }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = {
                    val (url, qualities) = download.videoUrl.value?.let {
                        it to emptyList()
                    } ?: with(translation.value.link) {
                        url to filterVideoQuality(user, quality)
                    }

                    if (user.isAuthorized) {
                        model.saveWatched(
                            WatchedVideoData(
                                translation = translation.value.name
                            )
                        )
                    }

                    val videoKey = model.getVideoId(videoId)
                    navigator.push(
                        PlayerScreen(
                            videoKey = videoKey,
                            title = title,
                            videoUrl = url,
                            qualities = qualities
                        )
                    )
                },
                modifier = modifier.focusRequester(buttonFocusRequester)
            ) {
                Icon(Icons.Default.PlayArrow, "Play")
                Text("Play movie")
            }

            Trailer(playlist, title)

            ShowTranslations(settings, translation, playlist.translations)

            when (val state = downloadState) {
                is DownloadState.Success -> {
                    DownloadDeleteButton(download::delete)
                }

                is DownloadState.None -> {
                    DownloadStartButton(onClick = {
                        val link = translation.value.link
                        val screenHeight = minOf(windowSize.width, windowSize.height)
                        download.download(platform, link, screenHeight)
                    })
                }

                is DownloadState.Downloading -> {
                    DownloadCancelButton(state.progress, download::cancel)
                }
            }
        }
    }

    @Composable
    private fun PlaySeries(
        modifier: Modifier = Modifier,
        title: String,
        playlist: Playlist.Series,
        model: VideoScreenModel
    ) {
        val user = LocalUserInfo.current
        val navigator = LocalNavigator.currentOrThrow
        val settings = model.videoSettings
        var showSeasons by remember { mutableStateOf(false) }
        var season by remember {
            mutableStateOf(
                value = settings.get<String>("season")?.let { name ->
                    playlist.seasons.firstOrNull { it.name == name }
                } ?: playlist.seasons.first()
            )
        }
        var showEpisodes by remember { mutableStateOf(false) }
        var episode by remember {
            mutableStateOf(
                value = settings.get<String>("episode")?.let { name ->
                    season.episodes.firstOrNull { it.name == name }
                } ?: season.episodes.first()
            )
        }
        val translation = remember {
            mutableStateOf(
                value = getTranslation(settings, episode.translations)
            )
        }
        val buttonFocusRequester = FocusRequester()

        val selectSeason = { s: Season ->
            season = s
            episode = s.episodes.first()
            settings.putString("season", s.name)
            translation.value = getTranslation(settings, episode.translations)
        }

        val selectEpisode = { e: Episode ->
            settings.putString("episode", e.name)
            episode = e
            translation.value = getTranslation(settings, episode.translations)
        }

        LaunchedEffect(Unit) {
            val episodeId = model.getEpisodeId(videoId, season.name, episode.name)
            if (model.wasEpisodeWatched(episodeId)) {
                println("VideoScreen#LaunchEffect episode $episodeId is watched")
                val episodeIndex = season.episodes.indexOf(episode)
                if (episodeIndex < season.episodes.lastIndex) {
                    selectEpisode(season.episodes[episodeIndex + 1])
                } else {
                    val seasonIndex = playlist.seasons.indexOf(season)
                    if (seasonIndex < season.episodes.lastIndex) {
                        selectSeason(playlist.seasons[seasonIndex + 1])
                    }
                }
            }
            buttonFocusRequester.requestFocus()
        }

        Column(modifier = modifier) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton(
                    onClick = {
                        if (user.isAuthorized) {
                            model.saveWatched(
                                WatchedVideoData(
                                    translation = translation.value.name,
                                    season = season.name,
                                    episode = episode.name
                                )
                            )
                        }
                        val videoKey = model.getEpisodeId(videoId, season.name, episode.name)
                        navigator.push(
                            PlayerScreen(
                                videoKey = videoKey,
                                title = title,
                                videoUrl = translation.value.link.url,
                                qualities = filterVideoQuality(user, translation.value.link.quality)
                            )
                        )
                    },
                    modifier = Modifier.focusRequester(buttonFocusRequester)
                ) {
                    Icon(Icons.Default.PlayArrow, "Play", Modifier.size(24.dp))
                    Text("Season ${season.name}, episode ${episode.name}")
                }

                Trailer(playlist, title)
            }

            Row {
                OutlinedButton(onClick = { showSeasons = true }) {
                    Text("Season ${season.name}")

                    DropdownMenu(
                        expanded = showSeasons,
                        onDismissRequest = { showSeasons = false }
                    ) {
                        playlist.seasons.forEach {
                            DropdownMenuItem(
                                text = { Text("Season ${it.name}") },
                                onClick = {
                                    selectSeason(it)
                                    showSeasons = false
                                },
                                enabled = it != season
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                OutlinedButton(onClick = { showEpisodes = true }) {
                    Text("Episode ${episode.name}")

                    DropdownMenu(
                        expanded = showEpisodes,
                        onDismissRequest = { showEpisodes = false }
                    ) {
                        season.episodes.forEach {
                            DropdownMenuItem(
                                text = { Text("Episode ${it.name}") },
                                onClick = {
                                    selectEpisode(it)
                                    showEpisodes = false
                                },
                                enabled = it != episode
                            )
                        }
                    }
                }

                ShowTranslations(settings, translation, episode.translations)
            }
        }
    }

    @Composable
    private fun ShowTranslations(
        settings: Settings,
        translation: MutableState<Translation>,
        translations: List<Translation>
    ) {
        var showTranslations by remember { mutableStateOf(false) }

        if (translations.size > 1) {
            IconButton(onClick = { showTranslations = true }) {
                Icon(MaterialIcons.Translate, "Language", Modifier.size(24.dp))

                DropdownMenu(
                    expanded = showTranslations,
                    onDismissRequest = { showTranslations = false }
                ) {
                    translations.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = it.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                translation.value = it
                                settings.putString("translation", it.name)
                                showTranslations = false
                            },
                            enabled = it != translation.value
                        )
                    }
                }
            }
        }
    }

    private fun getTranslation(settings: Settings, translations: List<Translation>): Translation {
        return settings.get<String>("translation")?.let { name ->
            translations.firstOrNull { it.name == name }
        } ?: translations.first()
    }

    @Composable
    private fun Trailer(playlist: Playlist, title: String) {
        val user = LocalUserInfo.current
        val navigator = LocalNavigator.currentOrThrow
        val trailer = playlist.trailers.firstOrNull()

        if (trailer != null) {
            IconButton(
                onClick = {
                    navigator.push(
                        PlayerScreen(
                            videoKey = videoId.toString(),
                            title = title,
                            videoUrl = trailer.link.url,
                            qualities = filterVideoQuality(user, trailer.link.quality)
                        )
                    )
                }
            ) {
                Icon(MaterialIcons.Movie, "Trailer", Modifier.size(24.dp))
            }
        }
    }

    @Composable
    private fun DownloadStartButton(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = MaterialIcons.Download,
                contentDescription = "Download",
                modifier = Modifier.size(32.dp)
            )
        }
    }

    @Composable
    private fun DownloadCancelButton(progress: Float, onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel"
                )
            }
        }
    }

    @Composable
    private fun DownloadDeleteButton(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }

    private fun filterVideoQuality(
        user: UserInfo,
        qualities: List<Int>
    ) = when {
        user.is_pro_plus -> qualities
        user.is_pro -> qualities.filter { it <= 720 }
        else -> qualities.filter { it <= 480 }
    }
}