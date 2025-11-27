package com.example.spotifyexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.spotifyexplorer.data.navigation.SpotifyNavGraph
import com.example.spotifyexplorer.ui.theme.SpotifyExplorerTheme
import kotlinx.coroutines.launch


/**
 * Entry point of the project
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    // onCreate function is called when the activity starts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // inside setContent we define the layout (COMPOSE UI) of the activity
            SpotifyExplorerTheme {
                // wraps the entire UI in a custom theme defined in Theme.kt

                // navigation controller for Jetpack Compose
                // rememberNavController creates a new instance of the NavController
                // and remembers it across configuration changes
                val navController = rememberNavController()
                SpotifyNavGraph(navController = navController, modifier = Modifier)
            }
        }
    }
}