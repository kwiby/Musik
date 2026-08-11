package com.kwiby.musik.data.data_classes

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
	tableName = "playlist_entries",
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
data class PlaylistEntry(
	val playlistId: Long,
	val songId: Long,
	val orderPos: Int = 0
)
