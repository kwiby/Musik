package com.kwiby.musik.data.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_list")
data class AudioFile(
	@PrimaryKey val id: Long,
	val contentUri: String,
	val albumArtUri: String,
	val title: String,
	val artist: String,
	val durationMs: Long,
	val orderPos: Int = 0
)