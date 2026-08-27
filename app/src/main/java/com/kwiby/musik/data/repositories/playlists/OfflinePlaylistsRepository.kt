package com.kwiby.musik.data.repositories.playlists

import com.kwiby.musik.data.daos.playlists.PlaylistDao
import com.kwiby.musik.data.data_classes.audio_file.AudioFile
import com.kwiby.musik.data.data_classes.playlist.Playlist
import com.kwiby.musik.data.data_classes.playlist.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

class OfflinePlaylistsRepository(
	private val playlistsDao: PlaylistDao
) : PlaylistsRepository {
	override fun getAllPlaylistsWithSongCounts(): Flow<List<PlaylistWithSongCount>> =
		playlistsDao.getAllPlaylistsWithSongCounts()
	override fun getAllSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>> =
		playlistsDao.getAllSongsInPlaylist(playlistId)
	override suspend fun renamePlaylist(playlistId: Long, newName: String) =
		playlistsDao.renamePlaylist(playlistId, newName)
	override suspend fun createPlaylist(name: String) =
		playlistsDao.createPlaylist(name)
	override suspend fun deletePlaylists(playlists: List<Playlist>) =
		playlistsDao.deletePlaylists(playlists)
	override suspend fun reorderPlaylists(newOrder: List<Playlist>) =
		playlistsDao.reorderPlaylists(newOrder)
	override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) =
		playlistsDao.addSongsToPlaylist(playlistId, songIds)
	override suspend fun addSongsToPlaylists(playlistIds: List<Long>, songIds: List<Long>) =
		playlistsDao.addSongsToPlaylists(playlistIds, songIds)
	override suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>) =
		playlistsDao.removeSongsFromPlaylist(playlistId, songIds)
	override suspend fun reorderSongsInPlaylist(playlistId: Long, newSongOrder: List<Long>) =
		playlistsDao.reorderSongsInPlaylist(playlistId, newSongOrder)
	override suspend fun getPlaylistSongCount(playlistId: Long): Int =
		playlistsDao.getPlaylistSongCount(playlistId)
}