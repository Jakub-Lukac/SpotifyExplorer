package com.example.spotifyexplorer.data.ui.home

import app.cash.turbine.test
import com.example.spotifyexplorer.data.api.SpotifyService
import com.example.spotifyexplorer.data.datastore.TokenDataStore
import com.example.spotifyexplorer.data.home.HomeUiState
import com.example.spotifyexplorer.data.model.Album
import com.example.spotifyexplorer.data.model.AlbumResponse
import com.example.spotifyexplorer.data.model.Artist
import com.example.spotifyexplorer.data.model.Followers
import com.example.spotifyexplorer.data.model.Image
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // lateinit allows for variable initialization later
    // Mock instance of HomeViewModel
    private lateinit var viewModel: HomeViewModel

    // Mock instance of TokenDataStore and SpotifyService
    private lateinit var mockTokenStore: TokenDataStore
    private lateinit var mockSpotifyService: SpotifyService

    // Mock dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    @Before // runs before every test
    fun setup() {
        // Replaces main dispatcher so coroutines can run in tests
        Dispatchers.setMain(testDispatcher)
        mockTokenStore = mockk(relaxed = true) // initialize the mock token store with relaxed = true
        
        // Mock SpotifyService constructor
        // It tells mocked view model of homeviewmodel, that it should use the mocked spotify service
        mockkConstructor(SpotifyService::class) // Specifies for Mockk that the SpotifyService will be mocked
        mockSpotifyService = mockk(relaxed = true) // initialize the mock spotify service with relaxed = true
        // relaxed to true, means it will create mock with no specific behaviour

        // Defines default behaviour, to return null
        coEvery {
            anyConstructed<SpotifyService>().searchArtist(any())
        } coAnswers { null } // default, override in tests

        coEvery {
            anyConstructed<SpotifyService>().getArtistAlbums(any())
        } coAnswers { null }

        // Create the mocked instance of HomeViewModel with mocked dependencies
        viewModel = HomeViewModel(mockTokenStore)
    }

    @After // clean up, runs after every test
    // resets dispatcher and all mocks after each test
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `searchArtist should transition from Idle to Loading to Success when artist is found`() = runTest {
        val query = "Adele"
        val mockArtist = Artist(
            id = "4dpARuHxo51G3z768sgnrY",
            name = "Adele",
            popularity = 95,
            followers = Followers(total = 50000000),
            images = listOf(Image(url = "https://example.com/image.jpg", height = 640, width = 640)),
            genres = listOf("pop", "soul")
        )
        val mockAlbums = listOf(
            Album(
                id = "album1",
                name = "25",
                total_tracks = 11,
                release_date = "2015-11-20",
                images = emptyList()
            )
        )
        // Mock the album response
        val mockAlbumResponse = AlbumResponse(total = 1, items = mockAlbums)

        // Mock the constructed SpotifyService used inside the ViewModel
        // anyConstructed is a MockK feature
        // coEvery for suspend functions, to mock behavior of functions
        // so mock spotify service calls search artist with mock query and returns mock artist response
        // same for album
        coEvery { anyConstructed<SpotifyService>().searchArtist(query) } returns mockArtist
        coEvery { anyConstructed<SpotifyService>().getArtistAlbums(mockArtist.id) } returns mockAlbumResponse

        // Test the UI state using Turbine
        // Turbine is testing library for collecting and asserting values emitted from Kotlin Flows
        viewModel.uiState.test {
            assertEquals(HomeUiState.Idle, awaitItem())

            viewModel.searchArtist(query)

            // advance dispatcher so coroutine completes
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(HomeUiState.Loading, awaitItem())

            val successState = awaitItem()
            assertTrue(successState is HomeUiState.Success)
            assertEquals(mockArtist, (successState as HomeUiState.Success).artist)
            assertEquals(mockAlbums, successState.albums)
        }

        // coVerify for suspend functions
        // check whether the the function was called with specific parameters during test
        coVerify { anyConstructed<SpotifyService>().searchArtist(query) }
        coVerify { anyConstructed<SpotifyService>().getArtistAlbums(mockArtist.id) }
    }
}
