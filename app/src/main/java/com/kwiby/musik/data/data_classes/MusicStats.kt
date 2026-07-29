package com.kwiby.musik.data.data_classes

data class MusicStats(
	val id: Long = 0L,
	val playCount: Int = 0,
	val totalListenTimeMs: Long = 0L,
	val albumArtUri: String = "",
	val title: String = "",
	val artist: String = "",
)
