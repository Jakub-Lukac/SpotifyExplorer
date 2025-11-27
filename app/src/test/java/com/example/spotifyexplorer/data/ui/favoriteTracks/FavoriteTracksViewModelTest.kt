package com.example.spotifyexplorer.data.ui.favoriteTracks

import app.cash.turbine.test
import com.example.spotifyexplorer.data.db.FavoriteTrackRepository
import com.example.spotifyexplorer.data.model.FavoriteTrack
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteTracksViewModelTest {

    private lateinit var viewModel: FavoriteTracksViewModel
    private lateinit var repository: FavoriteTrackRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // default return of favorites flow
        every { repository.getFavorites() } returns flowOf(
            listOf(
                FavoriteTrack("1", "Song A", 50000, 1, albumImage = "image1.png"),
                FavoriteTrack("2", "Song B", 80000, 2, albumImage = "image2.png")
            )
        )

        viewModel = FavoriteTracksViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `removeFavorite should call repository removeTrack`() = runTest {

        viewModel.removeFavorite("1")

        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository was called
        coVerify { repository.removeTrack("1") }
    }
}