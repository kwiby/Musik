package com.kwiby.musik.data.coil

import androidx.compose.runtime.mutableStateMapOf

object ArtworkCacheKeys {
	private val keys = mutableStateMapOf<String, Long>()

	fun markEdited(trackId: String) {
		keys[trackId] = System.currentTimeMillis()
	}

	fun getTime(trackId: String): Long = keys[trackId] ?: 0L
}