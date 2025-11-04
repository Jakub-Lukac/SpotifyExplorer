package com.example.spotifyexplorer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.ui.theme.SpotifyExplorerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val clientId = "03619e7aa0344a45a9fe014a969a62ea"
    private val clientSecret = "6c994bd714a94ee79d8f6c985716c3a9"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val spotifyService = SpotifyService(clientId, clientSecret)

        setContent {
            SpotifyExplorerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var artist by remember { mutableStateOf<Artist?>(null) }
                    var error by remember { mutableStateOf<String?>(null) }
                    var query by remember { mutableStateOf("") }
                    val scope = rememberCoroutineScope()

                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = "Spotify Explorer",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        },
                        content = { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Search bar
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
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
                                                        scope.launch {
                                                            try {
                                                                error = null
                                                                artist = null
                                                                artist = spotifyService.searchArtist(query)
                                                                Log.d("SpotifyUI", "Loaded artist: $artist")
                                                            } catch (e: Exception) {
                                                                error = e.message
                                                                Log.e("SpotifyUI", "Error fetching artist", e)
                                                            }
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "Search"
                                                )
                                            }
                                        }
                                    )
                                }

                                ArtistDetailsView(artist = artist, error = error)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistDetailsView(artist: Artist?, error: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            artist == null -> Text("Search for an artist to begin")
            else -> ArtistDetailsContent(artist)
        }
    }
}

@Composable
fun ArtistDetailsContent(artist: Artist) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val imageUrl = when {
        screenWidth >= 768 && artist.images.isNotEmpty() -> artist.images[0].url // Large screens
        artist.images.size > 1 -> artist.images[1].url // Smaller screens
        artist.images.isNotEmpty() -> artist.images[0].url
        else -> null
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = artist.name,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.noimage),
                contentDescription = "Missing artist image",
                modifier = Modifier
                    .size(180.dp)
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
        Text(
            text = "Followers: ${artist.followers.total}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Popularity: ${artist.popularity} / 100",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}