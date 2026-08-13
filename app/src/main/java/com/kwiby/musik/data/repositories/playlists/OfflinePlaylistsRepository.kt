package com.kwiby.musik.data.repositories.playlists

import com.kwiby.musik.data.daos.playlists.PlaylistDao
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

class OfflinePlaylistsRepository(
	private val playlistsDao: PlaylistDao
) : PlaylistsRepository {
	override fun getAllPlaylistsWithSongCounts(): Flow<List<PlaylistWithSongCount>> =
		playlistsDao.getAllPlaylistsWithSongCounts()
	override fun getAllSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>> =
		playlistsDao.getAllSongsInPlaylist(playlistId)
	override suspend fun createPlaylist(name: String) =
		playlistsDao.createPlaylist(name)
	override suspend fun deletePlaylists(playlists: List<Playlist>) =
		playlistsDao.deletePlaylists(playlists)
	override suspend fun reorderPlaylists(newOrder: List<Playlist>) =
		playlistsDao.reorderPlaylists(newOrder)
	override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) =
		playlistsDao.addSongToPlaylist(playlistId, songId)
	override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) =
		playlistsDao.removeSongFromPlaylist(playlistId, songId)
	override suspend fun reorderSongsInPlaylist(playlistId: Long, newSongOrder: List<Long>) =
		playlistsDao.reorderSongsInPlaylist(playlistId, newSongOrder)
}