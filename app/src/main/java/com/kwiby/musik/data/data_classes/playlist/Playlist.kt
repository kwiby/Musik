package com.kwiby.musik.data.data_classes.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val orderPos: Int = 0
)
