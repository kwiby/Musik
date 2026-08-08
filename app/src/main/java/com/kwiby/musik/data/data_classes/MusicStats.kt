package com.kwiby.musik.data.data_classes

data class MusicStats(
	val id: Long = 0L,
	val playCount: Int = 0,
	val totalListenTimeMs: Long = 0L,
	val contentUri: String = "",
	val title: String = "",
	val artist: String = "",
)
