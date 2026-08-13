package com.kwiby.musik.data.data_classes

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistDetails(
	@Embedded val playlist: Playlist,
	@Relation(
		parentColumn = "id", // Playlist's primary key
		entityColumn = "id", // AudioFile's primary key
		associateBy = Junction(
			value = PlaylistSong::class,
			parentColumn = "playlistId",
			entityColumn = "songId"
		)
	)
	val songs: List<AudioFile>
)