package com.kwiby.musik.data.data_classes

data class VideoInfo(
	val title: String,
	val artist: String,
	val durationMs: Long, // Milliseconds
	val thumbnailUrl: String?
)
