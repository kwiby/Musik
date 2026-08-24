package com.kwiby.musik.data.data_classes.music

data class MusicDetails(
	val id: Long = 0L,
	val contentUri: String = "",
	val title: String = "",
	val artist: String = "",
	val durationMs: String = "",
	val orderPos: Int = 0
)