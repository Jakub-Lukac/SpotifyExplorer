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
import com.example.spotifyexplorer.data.home.HomeUiState
import com.example.spotifyexplorer.data.ui.about.AboutScreen
import com.example.spotifyexplorer.data.ui.artist.ArtistDetailScreen
import com.example.spotifyexplorer.data.ui.favoriteTracks.FavoriteTracksScreen
import com.example.spotifyexplorer.data.ui.home.HomeScreen
import com.example.spotifyexplorer.data.ui.home.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.db.AppDatabase
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository
import com.example.spotifyexplorer.data.home.HomeViewModelFactory
import com.example.spotifyexplorer.data.ui.album.AlbumDetailScreen
import com.example.spotifyexplorer.data.ui.album.AlbumDetailViewModel
import com.example.spotifyexplorer.data.ui.album.AlbumDetailViewModelFactory
import com.example.spotifyexplorer.data.ui.favoriteTracks.FavoriteTracksViewModel
import com.example.spotifyexplorer.data.ui.favoriteTracks.FavoriteTracksViewModelFactory

// Navigation has three main parts
// NavController: Responsible for navigating between destinations—that is, the screens in app.
// NavGraph: Maps composable destinations to navigate to.
// NavHost: Composable acting as a container for displaying the current destination of the NavGraph.

enum class SpotifyScreens() {
    Home,
    FavoriteTracks,
    About
}

/**
 * Navigation graph for the app.
 * Defines all the routes
 * Handles navigation arguments like artistId or albumId
 * Provides shared ViewModel when multiple screens need the same state
 */

@Composable
fun SpotifyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Need of DataStore in multiple ViewModels
    // Using remember so recreation doesn't happen
    val tokenStore = remember { TokenDataStore(context) }

    // NavHost is a container that displays the composable corresponding to the current route.
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { backStackEntry ->
            // backStackEntry means the current backstack (backstack stands for the history of navigation)
            // In this case backStackEntry is for "home" so it provides with its lifecycle and state
            // HomeViewModel has parameter tokenStore so it needs to be created with factory
            // By default viewModel() can only create ViewModels that don't have parameters
            // Purpose of backStackEntry in this case is that when user navigates from Home Screen to Artist Detail
            // or to Album Detail, these screens are on top of the stack, but they need the state of home screen
            // and also when we navigate back we still gonna be using the same preserved state with our ViewModel
            val viewModel: HomeViewModel = viewModel(backStackEntry, factory = HomeViewModelFactory(tokenStore))
            HomeScreen(
                modifier = modifier,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("artist_detail/{artistId}") { backStackEntry ->
            // the artistDetail screen is child screen of home
            // we want to reuse the HomeViewModel instance created for the home screen
            // use of remember will ensure that the same instance is used
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeEntry, factory = HomeViewModelFactory(tokenStore))


            val uiState = homeViewModel.uiState.collectAsState().value
            Log.d("State", "$uiState")
            val artist = (uiState as? HomeUiState.Success)?.artist
            Log.d("Artist", "$artist")

            if (artist != null) {
                ArtistDetailScreen(
                    modifier = modifier,
                    artist = artist,
                    navController = navController,
                    homeViewModel = homeViewModel
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

        composable(
            route = "album_detail/{albumId}"
        ) { backStackEntry ->
            // Get the home backStack entry to access its ViewModel
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeEntry, factory = HomeViewModelFactory(tokenStore))

            val context = LocalContext.current

            val albumDetailViewModel: AlbumDetailViewModel = viewModel(
                backStackEntry,
                factory = AlbumDetailViewModelFactory(
                    tokenStore,
                    repository = FavoriteTrackRepository(
                        AppDatabase.getInstance(context).favoriteTrackDao()
                    )
                )
            )

            val selectedAlbum by homeViewModel.selectedAlbum.collectAsState()

            if (selectedAlbum != null) {
                AlbumDetailScreen(
                    modifier = modifier,
                    album = selectedAlbum!!,
                    navController = navController,
                    viewModel = albumDetailViewModel
                )
            } else {
                Text("Album details unavailable")
            }
        }

        composable(SpotifyScreens.FavoriteTracks.name) {
            val context = LocalContext.current
            val favoriteTracksViewModel: FavoriteTracksViewModel = viewModel(
                factory = FavoriteTracksViewModelFactory(
                    FavoriteTrackRepository(
                        AppDatabase.getInstance(context).favoriteTrackDao()
                    )
                )
            )

            FavoriteTracksScreen(
                modifier = modifier,
                navController = navController,
                viewModel = favoriteTracksViewModel
            )
        }

        composable(SpotifyScreens.About.name) {
            AboutScreen(modifier, navController)
        }
    }
}

