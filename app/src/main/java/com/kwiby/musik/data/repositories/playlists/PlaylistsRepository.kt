package com.kwiby.musik.data.repositories.playlists

import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistDetails
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
	suspend fun createPlaylist(playlist: Playlist): Long
	suspend fun deletePlaylist(playlist: Playlist)
	fun getAllPlaylists(): Flow<List<Playlist>>
	suspend fun reorderPlaylists(newOrder: List<Playlist>)
	fun getPlaylistDetails(playlistId: Long): Flow<PlaylistDetails>
	fun getAllPlaylistDetails(): Flow<List<PlaylistDetails>>
	suspend fun getPlaylistEntryCount(playlistId: Long): Int
	fun getOrderedSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>>
	suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
	suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
	suspend fun reorderPlaylistEntries(playlistId: Long, newSongOrder: List<Long>)
}