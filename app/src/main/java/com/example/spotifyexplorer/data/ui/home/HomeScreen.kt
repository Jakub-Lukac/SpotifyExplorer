package com.example.spotifyexplorer.data.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyexplorer.R
import com.example.spotifyexplorer.data.model.Album
import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.data.home.HomeUiState
import com.example.spotifyexplorer.ui.theme.SpotifyDarkGray
import com.example.spotifyexplorer.ui.theme.SpotifyGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Spotify Explorer",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("favoriteTracks")
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("about")
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Spotify Explorer") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search artist") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (query.isNotBlank()) {
                                    scope.launch { viewModel.searchArtist(query) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val state = uiState) {
                    is HomeUiState.Idle -> Text("Search for an artist to begin")
                    is HomeUiState.Loading -> CircularProgressIndicator()
                    is HomeUiState.Success -> {
                        if (isLandscape) {
                            // 🖥 Landscape layout: side-by-side
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ArtistDetailsCard(state.artist, navController)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    AlbumList(
                                        albums = state.albums,
                                        onAlbumClick = { album ->
                                            viewModel.selectAlbum(album)
                                            navController.navigate("album_detail/${album.id}")
                                        }
                                    )
                                }
                            }
                        } else {
                            // Portrait layout: stacked vertically
                            ArtistDetailsCard(state.artist, navController)
                            Spacer(modifier = Modifier.height(8.dp))
                            AlbumList(
                                albums = state.albums,
                                onAlbumClick = { album ->
                                    viewModel.selectAlbum(album)
                                    navController.navigate("album_detail/${album.id}")
                                }
                            )
                        }
                    }
                    is HomeUiState.Error -> Text("Error: ${state.message}")
                }
            }
        }
    }
}



@Composable
fun ArtistDetailsCard(
    artist: Artist,
    navController: NavController
) {
    val currentArtistId = artist.id // capture latest ID at recomposition
    Log.d("Current Artist ID", currentArtistId)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SpotifyDarkGray)
            .clickable {
                // Navigate to artist detail screen with artist ID
                navController.navigate("artist_detail/$currentArtistId") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .padding(20.dp)
    ) {
        ArtistDetailsContent(artist)
    }
}


@Composable
fun ArtistDetailsContent(artist: Artist) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp

    val imageUrl = when {
        screenWidth >= 768 && artist.images.isNotEmpty() -> artist.images[0].url
        artist.images.size > 1 -> artist.images[1].url
        artist.images.isNotEmpty() -> artist.images[0].url
        else -> null
    }

    if (isLandscape) {
        // Landscape: image + info side by side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val imageSize = 100.dp

            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.noimage),
                    contentDescription = "Missing artist image",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Followers: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyGreen
                    )
                    Text(
                        text = "${artist.followers.total}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Popularity: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyGreen
                    )
                    Text(
                        text = "${artist.popularity} / 100",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    } else {
        // Portrait: image on top, all info stacked vertically
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageSize = 180.dp

            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.noimage),
                    contentDescription = "Missing artist image",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = artist.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Followers: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyGreen
                )
                Text(
                    text = "${artist.followers.total}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Popularity: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyGreen
                )
                Text(
                    text = "${artist.popularity} / 100",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@Composable
fun AlbumList(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
    ) {
        items(albums) { album ->
            AlbumCard(album = album, onAlbumClick = { onAlbumClick(album) })
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    onAlbumClick: (Album) -> Unit = {} // default lambda for future navigation
) {
    val imageUrl = album.images.firstOrNull()?.url

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp) // margin
            .clip(RoundedCornerShape(12.dp))
            .background(SpotifyDarkGray)
            .clickable { onAlbumClick(album) } // make clickable
            .padding(10.dp) // padding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = album.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.noimage),
                    contentDescription = "Missing album image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = album.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Released: ${album.release_date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}
