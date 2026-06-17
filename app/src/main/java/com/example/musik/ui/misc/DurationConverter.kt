package com.example.musik.ui.misc

import android.util.Log

fun Long.formatDuration(): String {
	val totalSeconds = this / 1000

	val hours = totalSeconds / 3600
	val minutes = totalSeconds / 60
	val seconds = totalSeconds % 60

	return "%d:%02d:%02d".format(hours, minutes, seconds)
}

fun String.unformatDuration(): Long {
	val sections = this.split(":")
	require(sections.size == 2) {
		val eMsg = "Duration format is NOT proper, must be 'H...H:MM:SS'!"

		Log.e("DurationConverter", eMsg)
		eMsg
	}

	val hours = sections[0].toLong()
	val minutes = sections[1].toLong()
	val seconds = sections[2].toLong()

	return (hours * 3600 + minutes * 60 + seconds) * 1000L
}