package com.example.musik.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_files")
data class AudioFile(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val filePath: String,
	val title: String,
	val artist: String,
	val duration: Long, // Milliseconds
)