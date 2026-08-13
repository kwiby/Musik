package com.kwiby.musik.data.daos.playlists

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistSong
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	// --===-- Private Functions --===--
	@Insert
	suspend fun insertPlaylist(playlist: Playlist)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertPlaylistSong(playlistEntry: PlaylistSong)

	@Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
	suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

	@Update
	suspend fun updatePlaylists(playlists: List<Playlist>)

	@Query("SELECT COUNT(*) FROM playlists")
	suspend fun getPlaylistCount(): Int

	@Update
	suspend fun updatePlaylistSongs(entries: List<PlaylistSong>)

	@Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY orderPos ASC")
	suspend fun getPlaylistSongs(playlistId: Long): List<PlaylistSong>

	@Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
	suspend fun getPlaylistSongCount(playlistId: Long): Int

	@Query("DELETE FROM sqlite_sequence WHERE name = 'playlists'")
	suspend fun resetPlaylistIdSequence()

	@Delete
	suspend fun deletePlaylistsFromList(playlists: List<Playlist>)


	// --===-- Public Playlist Functions --===--
	@Transaction
	suspend fun createPlaylist(name: String) {
		val nextPos = getPlaylistCount()
		insertPlaylist(
			Playlist(
				name = name,
				orderPos = nextPos
			)
		)
	}

	@Transaction
	suspend fun deletePlaylists(playlists: List<Playlist>) {
		deletePlaylistsFromList(playlists)

		if (getPlaylistCount() == 0) {
			resetPlaylistIdSequence()
		}
	}

	@Transaction
	suspend fun reorderPlaylists(newPlaylistOrder: List<Playlist>) {
		val updatedPlaylists = newPlaylistOrder.mapIndexed { index, playlist ->
			playlist.copy(orderPos = index)
		}
		updatePlaylists(updatedPlaylists)
	}

	@Query("""
		SELECT playlists.*, COUNT(playlist_songs.songId) AS songCount
		FROM playlists
		LEFT JOIN playlist_songs ON playlists.id = playlist_songs.playlistId
		GROUP BY playlists.id
		ORDER BY playlists.orderPos ASC
	""")
	fun getAllPlaylistsWithSongCounts(): Flow<List<PlaylistWithSongCount>>


	// --===-- Public Song Functions --===--
	@Transaction
	suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
		val nextPos = getPlaylistSongCount(playlistId)
		insertPlaylistSong(
			PlaylistSong(
				playlistId = playlistId,
				songId = songId,
				orderPos = nextPos
			)
		)
	}

	@Transaction
	suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
		deletePlaylistSong(playlistId, songId)
		val remaining = getPlaylistSongs(playlistId)
		val reindexed = remaining.mapIndexed { index, entry ->
			entry.copy(orderPos = index)
		}
		updatePlaylistSongs(reindexed)
	}

	@Transaction
	suspend fun reorderSongsInPlaylist(playlistId: Long, newSongOrder: List<Long>) {
		val updatedSongs = newSongOrder.mapIndexed { index, songId ->
			PlaylistSong(
				playlistId = playlistId,
				songId = songId,
				orderPos = index
			)
		}
		updatePlaylistSongs(updatedSongs)
	}

	@Query("""
		SELECT music_list.* FROM music_list
		INNER JOIN playlist_songs ON music_list.id = playlist_songs.songId
		WHERE playlist_songs.playlistId = :playlistId
		ORDER BY playlist_songs.orderPos ASC
	""")
	fun getAllSongsInPlaylist(playlistId: Long): Flow<List<AudioFile>>
}