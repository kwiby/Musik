package com.kwiby.musik.data.data_classes.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.kwiby.musik.data.data_classes.audio_file.AudioFile

@Entity(
	tableName = "playlist_songs",
	primaryKeys = ["playlistId", "songId"],
	foreignKeys = [
		ForeignKey(
			entity = Playlist::class,
			parentColumns = ["id"],
			childColumns = ["playlistId"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = AudioFile::class,
			parentColumns = ["id"],
			childColumns = ["songId"],
			onDelete = ForeignKey.CASCADE
		),
	],
	indices = [Index("playlistId"), Index("songId")]
)
data class PlaylistSong(
	val playlistId: Long,
	val songId: Long,
	val orderPos: Int = 0
)
