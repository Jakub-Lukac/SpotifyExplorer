package com.example.spotifyexplorer.data.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spotifyexplorer.data.ui.UiState
import com.example.spotifyexplorer.data.ui.about.AboutScreen
import com.example.spotifyexplorer.data.ui.artist.ArtistDetailScreen
import com.example.spotifyexplorer.data.ui.favoriteTracks.FavoriteTracksScreen
import com.example.spotifyexplorer.data.ui.home.HomeScreen
import com.example.spotifyexplorer.data.ui.home.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment

enum class SpotifyScreens() {
    Home,
    FavoriteTracks,
    About
}

@Composable
fun SpotifyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { backStackEntry ->
            val viewModel: HomeViewModel = viewModel(backStackEntry)
            HomeScreen(
                modifier = modifier,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("artist_detail/{artistId}") { backStackEntry ->
            // 👇 Safely get parent HomeViewModel when available
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeEntry)

            val uiState = homeViewModel.uiState.collectAsState().value
            Log.d("State", "$uiState")
            val artist = (uiState as? UiState.Success)?.artist
            Log.d("Artist", "$artist")

            if (artist != null) {
                ArtistDetailScreen(
                    artist = artist,
                    navController = navController
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Artist details unavailable.")
                }
            }
        }

        composable(SpotifyScreens.FavoriteTracks.name) {
            FavoriteTracksScreen(modifier, navController)
        }

        composable(SpotifyScreens.About.name) {
            AboutScreen(modifier, navController)
        }
    }
}

