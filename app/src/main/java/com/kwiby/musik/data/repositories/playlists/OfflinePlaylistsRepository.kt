package com.kwiby.musik.data.repositories.playlists

import com.kwiby.musik.data.daos.playlists.PlaylistDao
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistDetails
import kotlinx.coroutines.flow.Flow

class OfflinePlaylistsRepository(
	private val playlistsDao: PlaylistDao
) : PlaylistsRepository {
	override suspend fun createPlaylist(playlist: Playlist): Long =
		playlistsDao.createPlaylist(playlist)
	override suspend fun deletePlaylist(playlist: Playlist) = playlistsDao.deletePlaylist(playlist)
	override fun getAllPlaylists(): Flow<List<Playlist>> = playlistsDao.getAllPlaylists()
	override suspend fun reorderPlaylists(newOrder: List<Playlist>) =
		playlistsDao.reorderPlaylists(newOrder)
	override fun getPlaylistDetails(playlistId: Long): Flow<PlaylistDetails> =
		playlistsDao.getPlaylistDetails(playlistId)
	override fun getAllPlaylistDetails(): Flow<List<PlaylistDetails>> =
		playlistsDao.getAllPlaylistDetails()
	override suspend fun getPlaylistEntryCount(playlistId: Long): Int =
		playlistsDao.getPlaylistEntryCount(playlistId)
	override fun getOrderedSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>> =
		playlistsDao.getOrderedSongsInPlaylist(playlistId)
	override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) =
		playlistsDao.addSongToPlaylist(playlistId, songId)
	override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) =
		playlistsDao.removeSongFromPlaylist(playlistId, songId)
	override suspend fun reorderPlaylistEntries(playlistId: Long, newSongOrder: List<Long>) =
		playlistsDao.reorderPlaylistEntries(playlistId, newSongOrder)
}