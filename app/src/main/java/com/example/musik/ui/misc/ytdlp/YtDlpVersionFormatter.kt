package com.example.musik.ui.misc.ytdlp

fun getFormattedVersionName(versionName: String?): String {
	if (versionName == null) {
		return "UNKNOWN"
	} else {
		val noPrefix = versionName.removePrefix("yt-dlp ")
		val noPrefixLowercase = noPrefix.lowercase()

		return if (noPrefixLowercase.startsWith("nightly") || noPrefixLowercase.startsWith("master")) {
			noPrefixLowercase.replaceFirstChar { it.uppercase() }
		} else {
			"Stable $noPrefixLowercase"
		}
	}
}