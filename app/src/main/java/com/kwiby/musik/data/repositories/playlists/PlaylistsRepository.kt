package com.kwiby.musik.data.repositories.playlists

import com.kwiby.musik.data.data_classes.audio_file.AudioFile
import com.kwiby.musik.data.data_classes.playlist.Playlist
import com.kwiby.musik.data.data_classes.playlist.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
	fun getAllPlaylistsWithSongCounts(): Flow<List<PlaylistWithSongCount>>
	fun getAllSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>>
	suspend fun renamePlaylist(playlistId: Long, newName: String)
	suspend fun createPlaylist(name: String)
	suspend fun deletePlaylists(playlists: List<Playlist>)
	suspend fun reorderPlaylists(newOrder: List<Playlist>)
	suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)
	suspend fun addSongsToPlaylists(playlistIds: List<Long>, songIds: List<Long>)
	suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>)
	suspend fun reorderSongsInPlaylist(playlistId: Long, newSongOrder: List<Long>)
	suspend fun getPlaylistSongCount(playlistId: Long): Int
}