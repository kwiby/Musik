package com.example.musik.data.data_classes

data class VideoInfo(
	val title: String,
	val artist: String,
	val duration: Long, // Seconds
	val thumbnailUrl: String?
)
