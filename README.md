# Spotify Explorer

This project is built using Kotlin and Jetpack Compose
integrating the Spotify Web API and applying MVVM architecture
with Navigation and StateFlow for a clean, scalable structure.
And makes use of Room database for storing favorite tracks.

## Technologies used

- Kotlin + Jetpack Compose – Declarative UI
- Spotify Web API – Artists, albums, tracks
- MVVM Architecture – ViewModel + StateFlow
- Retrofit – API communication
- Room – Database for favorite tracks
- Navigation Component – Screen routing

## Project features

- Search and browse artists
- View albums and tracks
- Save favorite tracks locally
- Clean modular structure
- Responsive material design

## Testing

### Unit Tests

- HomeViewModelTest - searchArtist
  This test verifies that the searchArtist function in HomeViewModel correctly changes the uiState from Idle to Loading to Success when a valid artist is found. It mocks the Spotify API responses for artist and album data. The expected outcome is that the uiState contains the correct Artist and associated Album list, and the API calls are called as intended.
- FavoriteTracksViewModel - removeFavorite
  This test verifies that the removeFavorite function in FavoriteTracksViewModel correctly calls the repository to remove a track. It uses a mocked FavoriteTrackRepository with predefined favorite tracks. The expected outcome is that the repository’s removeTrack method is called with the correct track ID when removeFavorite is executed.

### Instrumentation Tests

- DaoTest - Room DAO tests for favorite tracks
  This test suite validates the behavior of FavoriteTrackDao in an in-memory Room database. It ensures that track insertion, retrieval, update, deletion, and existence checks work as expected. The expected outcomes are that tracks are correctly added, retrieved, updated, deleted, and identified as favorites using DAO methods.