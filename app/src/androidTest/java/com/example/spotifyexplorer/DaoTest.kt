package com.example.spotifyexplorer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spotifyexplorer.data.db.AppDatabase
import com.example.spotifyexplorer.data.db.FavoriteTrackDao
import com.example.spotifyexplorer.data.model.FavoriteTrack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class FavoriteTrackDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteTrackDao

    // Create mock tracks
    private val track1 = FavoriteTrack(
        id = "1",
        name = "Song A",
        duration_ms = 50000,
        track_number = 1,
        albumImage = "image1.png"
    )

    private val track2 = FavoriteTrack(
        id = "2",
        name = "Song B",
        duration_ms = 80000,
        track_number = 2,
        albumImage = "image2.png"
    )

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Create in memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // for testing only
            .build()
        dao = database.favoriteTrackDao()
    }

    @After // after tests close the database
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    // Helper functions
    private suspend fun insertOneTrack() {
        dao.insertTrack(track1)
    }

    private suspend fun insertTwoTracks() {
        dao.insertTrack(track1)
        dao.insertTrack(track2)
    }

    @Test
    fun insertTrack_insertsIntoDb() = runBlocking {
        insertOneTrack()
        val allTracks = dao.getAllFavoriteTracks().first()
        assertEquals(1, allTracks.size)
        assertEquals(track1, allTracks[0])
    }

    @Test
    fun getAllFavoriteTracks_returnsAllTracks() = runBlocking {
        insertTwoTracks()
        val allTracks = dao.getAllFavoriteTracks().first()
        assertEquals(2, allTracks.size)
        assertEquals(track1, allTracks[0])
        assertEquals(track2, allTracks[1])
    }

    @Test
    fun isFavorite_returnsTrueIfTrackExists() = runBlocking {
        insertOneTrack()
        val exists = dao.isFavorite(track1.id)
        assertTrue(exists)
    }

    @Test
    fun deleteById_removesTrack() = runBlocking {
        insertTwoTracks()
        dao.deleteById(track1.id)
        val allTracks = dao.getAllFavoriteTracks().first()
        assertEquals(1, allTracks.size)
        assertEquals(track2, allTracks[0])
    }

    @Test
    fun updateTrack_modifiesTrack() = runBlocking {
        insertOneTrack()
        val updatedTrack = track1.copy(name = "Updated Song")
        dao.update(updatedTrack)
        val allTracks = dao.getAllFavoriteTracks().first()
        assertEquals("Updated Song", allTracks[0].name)
    }
}