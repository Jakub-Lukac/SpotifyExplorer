package com.example.spotifyexplorer.data.ui.artist

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotifyexplorer.R
import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.data.ui.home.HomeViewModel
import com.example.spotifyexplorer.ui.theme.SpotifyDarkGray
import com.example.spotifyexplorer.ui.theme.SpotifyGreen
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    artist: Artist,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current

    // Get custom photo for this artist from HomeViewModel
    var customPhotoUri by remember { mutableStateOf(homeViewModel.getCustomArtistPhoto(artist.id)) }

    // Check camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher to request camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // Launcher to take a picture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            bitmap?.let {
                bitmap.let {
                    val path = saveBitmapToCache(context, it)
                    customPhotoUri = path
                    homeViewModel.setCustomArtistPhoto(artist.id, path)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artist.name, style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack(route = "home", inclusive = false, saveState = true) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Home", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SpotifyDarkGray)
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Use custom photo if exists, otherwise API image
            val imageUrl = customPhotoUri ?: artist.images.firstOrNull()?.url

            Box {
                Image(
                    painter = if (imageUrl != null) rememberAsyncImagePainter(imageUrl)
                    else painterResource(id = R.drawable.noimage),
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Camera icon overlay
                IconButton(
                    onClick = {
                        if (hasCameraPermission) {
                            cameraLauncher.launch()
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(8.dp, 8.dp)
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Change Artist Image",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = artist.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                DetailRow(label = "Followers:", value = "${artist.followers.total}")
                DetailRow(label = "Popularity:", value = "${artist.popularity} / 100")
                DetailRow(label = "Genres:", value = artist.genres.joinToString(", ").ifEmpty { "Unknown" })
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(label + " ", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = SpotifyGreen)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp), color = MaterialTheme.colorScheme.onBackground)
    }
}

// Save Bitmap to cache and return the path
fun saveBitmapToCache(context: Context, bitmap: Bitmap): String {
    val file = File(context.cacheDir, "artist_image_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
    return file.absolutePath
}