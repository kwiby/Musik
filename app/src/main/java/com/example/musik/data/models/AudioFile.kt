package com.example.musik.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_files")
data class AudioFile(
	@PrimaryKey val id: Long,
	val contentUri: String,
	val title: String,
	val artist: String,
	val duration: Long, // Milliseconds
)