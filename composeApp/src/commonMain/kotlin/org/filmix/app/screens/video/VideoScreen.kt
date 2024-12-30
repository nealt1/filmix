package org.filmix.app.screens.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import filmix.composeapp.generated.resources.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.filmix.app.Platform
import org.filmix.app.components.*
import org.filmix.app.data.DownloadState
import org.filmix.app.models.UserInfo
import org.filmix.app.models.VideoDetails
import org.filmix.app.models.WatchedVideoData
import org.filmix.app.screens.player.PlayerScreen
import org.filmix.app.ui.LocalPlatform
import org.filmix.app.ui.LocalUserInfo
import org.filmix.app.ui.LocalWindowSize
import org.filmix.app.ui.LocalWindowSizeClass
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
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
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResource(Res.string.action_favourite)
                            )
                        }

                        IconToggleButton(
                            onCheckedChange = { model.toggleSaved() },
                            checked = model.saved.value
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(Res.string.action_watch_later)
                            )
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
                            VideoPoster(video)

                            Spacer(modifier = Modifier.size(size = 16.dp))

                            VideoDetails(
                                video,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            VideoPoster(video)
                        }
                    }
                    item {
                        VideoDetails(video)
                    }
                }

                if (video.relates.isNotEmpty()) {
                    item {
                        SectionTitle(
                            stringResource(Res.string.category_related),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.action_back)
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
    private fun VideoPoster(video: VideoDetails) {
        val posterResource = asyncPainterResource(data = video.poster)
        KamelImage(
            resource = posterResource,
            contentDescription = video.title,
            onLoading = { progress ->
                CircularProgressIndicator({ progress })
            },
            modifier = Modifier.height(313.dp).width(220.dp)
        )
    }

    @Composable
    private fun VideoDetails(video: VideoDetails, modifier: Modifier = Modifier) = with(video) {
        Column(modifier) {
            Text(
                text = buildAnnotatedString {
                    appendEntry(stringResource(Res.string.video_category), categories.joinToString())
                    appendEntry(pluralStringResource(Res.plurals.video_director, directors.size), directors.joinToString())
                    appendEntry(pluralStringResource(Res.plurals.video_actor, actors.size), actors.joinToString())
                    appendEntry(stringResource(Res.string.video_country), countries.joinToString())
                    appendEntry(stringResource(Res.string.video_year), year.toString())
                    appendEntry(stringResource(Res.string.video_duration), buildString {
                        val hours = duration / 60
                        val minutes = duration % 60
                        if (hours > 0) {
                            append("$hours ")
                            append(pluralStringResource(Res.plurals.video_duration_hour, hours))
                            append(" ")
                        }
                        append("$minutes ")
                        append(pluralStringResource(Res.plurals.video_duration_minute, minutes))
                    })
                }
            )

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
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    stringResource(Res.string.action_play)
                )
                Text(stringResource(Res.string.video_play_movie))
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
                    Icon(
                        Icons.Default.PlayArrow,
                        stringResource(Res.string.action_play),
                        Modifier.size(24.dp)
                    )
                    Text(stringResource(Res.string.video_play_season, season.name, episode.name))
                }

                Trailer(playlist, title)
            }

            Row {
                OutlinedButton(onClick = { showSeasons = true }) {
                    Text(stringResource(Res.string.video_season, season.name))

                    DropdownMenu(
                        expanded = showSeasons,
                        onDismissRequest = { showSeasons = false }
                    ) {
                        playlist.seasons.forEach {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.video_season, it.name)) },
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
                    Text(stringResource(Res.string.video_episode, episode.name))

                    DropdownMenu(
                        expanded = showEpisodes,
                        onDismissRequest = { showEpisodes = false }
                    ) {
                        season.episodes.forEach {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.video_episode, it.name)) },
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
                Icon(
                    imageVector = MaterialIcons.Translate,
                    contentDescription = stringResource(Res.string.video_language),
                    modifier = Modifier.size(24.dp)
                )

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
                Icon(
                    imageVector = MaterialIcons.Movie,
                    contentDescription = stringResource(Res.string.video_trailer),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    @Composable
    private fun DownloadStartButton(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = MaterialIcons.Download,
                contentDescription = stringResource(Res.string.action_download),
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
                    contentDescription = stringResource(Res.string.action_cancel)
                )
            }
        }
    }

    @Composable
    private fun DownloadDeleteButton(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.action_delete)
            )
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

    @Composable
    private fun AnnotatedString.Builder.appendEntry(key: String, value: String) {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append(key)
        }
        appendLine(" $value")
    }
}