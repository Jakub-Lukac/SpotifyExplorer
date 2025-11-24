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
        coEvery { anyConstructed<SpotifyService>().searchArtist(query) } returns mockArtist
        coEvery { anyConstructed<SpotifyService>().getArtistAlbums(mockArtist.id) } returns mockAlbumResponse

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

        coVerify { anyConstructed<SpotifyService>().searchArtist(query) }
        coVerify { anyConstructed<SpotifyService>().getArtistAlbums(mockArtist.id) }
    }
}
