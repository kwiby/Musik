package com.kwiby.musik.data.daos.playlists

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kwiby.musik.data.data_classes.audio_file.AudioFile
import com.kwiby.musik.data.data_classes.playlist.Playlist
import com.kwiby.musik.data.data_classes.playlist.PlaylistSong
import com.kwiby.musik.data.data_classes.playlist.PlaylistWithSongCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	// --===-- Private Functions --===--
	@Insert
	suspend fun insertPlaylist(playlist: Playlist)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertPlaylistSongs(playlistEntries: List<PlaylistSong>)

	@Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId IN (:songIds)")
	suspend fun deletePlaylistSongs(playlistId: Long, songIds: List<Long>)

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
	@Query("UPDATE playlists SET name = :newName WHERE id = :playlistId")
	suspend fun renamePlaylist(playlistId: Long, newName: String)

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
	suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
		val startPos = getPlaylistSongCount(playlistId)
		val entries = songIds.mapIndexed { index, songId ->
			PlaylistSong(
				playlistId = playlistId,
				songId = songId,
				orderPos = startPos + index
			)
		}
		insertPlaylistSongs(entries)

		val remaining = getPlaylistSongs(playlistId)
		val reindexed = remaining.mapIndexed { index, entry ->
			entry.copy(orderPos = index)
		}
		updatePlaylistSongs(reindexed)
	}

	@Transaction
	suspend fun addSongsToPlaylists(playlistIds: List<Long>, songIds: List<Long>) {
		for (playlistId in playlistIds) {
			val startPos = getPlaylistSongCount(playlistId)
			val entries = songIds.mapIndexed { index, songId ->
				PlaylistSong(
					playlistId = playlistId,
					songId = songId,
					orderPos = startPos + index
				)
			}
			insertPlaylistSongs(entries)

			val remaining = getPlaylistSongs(playlistId)
			val reindexed = remaining.mapIndexed { index, entry ->
				entry.copy(orderPos = index)
			}
			updatePlaylistSongs(reindexed)
		}
	}

	@Transaction
	suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>) {
		deletePlaylistSongs(playlistId, songIds)
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