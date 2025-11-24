package com.example.spotifyexplorer.data.ui.home

import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockTokenStore: TokenDataStore
    private lateinit var mockSpotifyService: SpotifyService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockTokenStore = mockk(relaxed = true)
        
        // Mock SpotifyService constructor
        mockkConstructor(SpotifyService::class)
        mockSpotifyService = mockk(relaxed = true)

        coEvery {
            anyConstructed<SpotifyService>().searchArtist(any())
        } coAnswers { null } // default, override in tests

        coEvery {
            anyConstructed<SpotifyService>().getArtistAlbums(any())
        } coAnswers { null }
        
        viewModel = HomeViewModel(mockTokenStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}
