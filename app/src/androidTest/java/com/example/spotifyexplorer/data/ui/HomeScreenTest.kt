package com.example.spotifyexplorer.data.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.spotifyexplorer.MainActivity
import com.example.spotifyexplorer.data.home.HomeUiState
import com.example.spotifyexplorer.data.ui.home.HomeScreen
import com.example.spotifyexplorer.data.ui.home.HomeViewModel
import com.example.spotifyexplorer.ui.theme.SpotifyExplorerTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // Fake ViewModel for UI testing
    private val fakeViewModel = mockk<HomeViewModel>(relaxed = true)
    private val fakeState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)

    fun shows_idle_message_initially() {
        every { fakeViewModel.uiState } returns MutableStateFlow(HomeUiState.Idle)

        composeRule.setContent {
            SpotifyExplorerTheme {
                val navController =
                    androidx.navigation.compose.rememberNavController() // ← CREATE HERE
                HomeScreen(
                    navController = navController,
                    viewModel = fakeViewModel
                )
            }
        }

        composeRule.onNodeWithText("Search for an artist to begin").assertExists()
    }

    @Test
    fun search_bar_input_triggers_searchArtist() {
        // Mock ViewModel
        every { fakeViewModel.uiState } returns fakeState

        composeRule.setContent {
            SpotifyExplorerTheme {
                val navController =
                    androidx.navigation.compose.rememberNavController() // ← CREATE HERE
                HomeScreen(
                    navController = navController,
                    viewModel = fakeViewModel
                )
            }
        }

        // Perform actions
        composeRule.onNodeWithText("Search artist").performTextInput("Adele")
        composeRule.onNodeWithContentDescription("Search").performClick()

        // Assert that ViewModel called
        verify { fakeViewModel.searchArtist("Adele") }
    }
}