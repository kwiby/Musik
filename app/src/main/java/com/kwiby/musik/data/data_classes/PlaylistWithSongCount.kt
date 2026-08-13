package com.kwiby.musik.data.data_classes

import androidx.room.Embedded

data class PlaylistWithSongCount(
	@Embedded val playlist: Playlist,
	val songCount: Int
)
