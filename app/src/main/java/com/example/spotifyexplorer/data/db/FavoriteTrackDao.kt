package com.example.spotifyexplorer.data.db

import androidx.room.*
import com.example.spotifyexplorer.data.model.FavoriteTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: FavoriteTrack): Long
    // returns -1 if already exists

    @Query("SELECT * FROM favorite_tracks")
    fun getAllFavoriteTracks(): Flow<List<FavoriteTrack>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE id = :trackId)")
    suspend fun isFavorite(trackId: String): Boolean

    @Query("DELETE FROM favorite_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: String)
}