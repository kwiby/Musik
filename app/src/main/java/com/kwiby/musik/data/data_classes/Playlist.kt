package com.kwiby.musik.data.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
	@PrimaryKey(autoGenerate = true) val id: Long = 0L,
	val name: String,
	val entryCount: Int,
	val orderPos: Int = 0
)
