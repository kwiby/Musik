package com.example.musik.ui.misc

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri

private fun isFileAccessible(application: Application, contentUri: Uri): Boolean {
	return try {
		application.contentResolver
			.openFileDescriptor(contentUri, "r")?.use {
				it.statSize > 0
			} ?: false
	} catch (_: Exception) {
		false
	}
}

fun isFileValid(application: Application, contentUri: String): Boolean {
	return contentUri.isNotBlank() && isFileAccessible(application, contentUri.toUri())
}