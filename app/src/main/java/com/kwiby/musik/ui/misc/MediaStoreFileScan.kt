package com.kwiby.musik.ui.misc

import android.content.Context
import android.media.MediaScannerConnection
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun scanFileAndAwait(context: Context, path: String) {
	suspendCancellableCoroutine { cont ->
		MediaScannerConnection.scanFile(context, arrayOf(path), null) { _, _ ->
			if (cont.isActive) {
				cont.resume(Unit) { _, _, _ ->
					/* onCancellation() */
				}
			}
		}
	}
}