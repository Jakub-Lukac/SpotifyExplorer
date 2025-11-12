package com.example.spotifyexplorer.data.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spotifyexplorer.data.ui.about.AboutScreen
import com.example.spotifyexplorer.data.ui.favoriteTracks.FavoriteTracksScreen
import com.example.spotifyexplorer.data.ui.home.HomeScreen

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
        startDestination = SpotifyScreens.Home.name
    ) {
        composable(SpotifyScreens.Home.name) { HomeScreen(modifier, navController) }
        composable(SpotifyScreens.FavoriteTracks.name) { FavoriteTracksScreen(navController) }
        composable(SpotifyScreens.About.name) { AboutScreen(navController) }
    }
}
