package com.example.musik.data.data_classes

data class MusicDetails(
	val id: Long = 0L,
	val contentUri: String = "",
	val albumArtUri: String = "",
	val title: String = "",
	val artist: String = "",
	val duration: String = "",
	val orderPos: Int = 0
)