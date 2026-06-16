package com.example.musik.ui.misc

import android.util.Log

fun Long.formatDuration(): String {
	val totalSeconds = this / 1000

	val minutes = totalSeconds / 60
	val seconds = totalSeconds % 60

	return "%d:%02d".format(minutes, seconds)
}

fun String.unformatDuration(): Long {
	val sections = this.split(":")
	require(sections.size == 2) {
		val eMsg = "Duration format is NOT proper, must be 'M...M:SS'!"

		Log.e("DurationConverter", eMsg)
		eMsg
	}

	val minutes = sections[0].toLong()
	val seconds = sections[1].toLong()

	return (minutes * 60 + seconds) * 1000L
}