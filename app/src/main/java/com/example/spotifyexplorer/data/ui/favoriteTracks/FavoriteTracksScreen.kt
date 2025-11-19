package com.example.spotifyexplorer.data.ui.favoriteTracks

import com.example.spotifyexplorer.R.drawable
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyexplorer.R
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository
import com.example.spotifyexplorer.data.model.FavoriteTrack
import com.example.spotifyexplorer.data.model.Track
import com.example.spotifyexplorer.data.utils.showCustomToast
import com.example.spotifyexplorer.ui.theme.SpotifyGreen
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.spotifyexplorer.data.ui.album.formatDuration
import kotlinx.serialization.json.Json.Default.configuration
import kotlinx.serialization.json.JsonConfiguration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTracksScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    repository: FavoriteTrackRepository
) {
    val viewModel: FavoriteTracksViewModel = viewModel(
        factory = FavoriteTracksViewModelFactory(repository)
    )

    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val favorites by viewModel.favorites.collectAsState()

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
                        navController.popBackStack("home", inclusive = false, saveState = true)
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
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
            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorite tracks yet!",
                        color = SpotifyGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(favorites) { track ->
                        FavoriteTrackCard(
                            track = track,
                            onRemoveFavorite = {
                                viewModel.removeFavorite(track.id)

                                showCustomToast(
                                    context = context,
                                    message = "Track removed from favorites!",
                                    iconRes = R.drawable.check
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteTrackCard(
    track: FavoriteTrack,
    onRemoveFavorite: (String) -> Unit
){
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val maxCardWidth = if (isLandscape) 500.dp else Dp.Unspecified
    val cardHeight = if (isLandscape) 180.dp else Dp.Unspecified

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .widthIn(max = maxCardWidth)
            .heightIn(max = cardHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        if (isLandscape) {
            // LANDSCAPE: fixed height, split with weights
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(track.albumImage),
                        contentDescription = track.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    SmallFloatingActionButton(
                        onClick = {
                            onRemoveFavorite(track.id)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp), // slightly smaller padding
                        containerColor = SpotifyGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Add track to favorites",
                            modifier = Modifier.size(16.dp) // optional: shrink icon
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                        .padding(12.dp)
                ) {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        color = SpotifyGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track #${track.track_number}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDuration(track.duration_ms),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // PORTRAIT: natural height using aspect ratio for image
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.4f) // keeps 70/30 visual split
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(track.albumImage),
                        contentDescription = track.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    SmallFloatingActionButton(
                        onClick = {
                            onRemoveFavorite(track.id)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp), // slightly smaller padding
                        containerColor = SpotifyGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Add track to favorites",
                            modifier = Modifier.size(16.dp) // optional: shrink icon
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        color = SpotifyGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track #${track.track_number}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDuration(track.duration_ms),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}



fun formatDuration(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}


