package com.kwiby.musik.data.updating

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class ApkInstaller(
	private val context: Context
) {
	suspend fun downloadApk(
		url: String,
		onProgress: (Float) -> Unit
	): File = withContext(Dispatchers.IO) {
		val client = OkHttpClient()
		val request = Request.Builder().url(url).build()
		val response = client.newCall(request).execute()
		val body = response.body ?: error("Empty response")

		val outputDir = File(context.cacheDir, "apk").apply { mkdirs() }
		val outputFile = File(outputDir, "update.apk")

		val totalBytes = body.contentLength()
		var downloadedBytes = 0L

		body.byteStream().use { input ->
			FileOutputStream(outputFile).use { output ->
				val buffer = ByteArray(8 * 1024)
				var bytes = input.read(buffer)

				while (bytes >= 0) {
					output.write(buffer, 0, bytes)
					downloadedBytes += bytes

					if (totalBytes > 0) {
						onProgress(downloadedBytes / totalBytes.toFloat())
					}

					bytes = input.read(buffer)
				}
			}
		}

		outputFile
	}

	fun installApk(apkFile: File) {
		val uri = FileProvider.getUriForFile(
			context,
			"${context.packageName}.fileProvider",
			apkFile
		)
		val intent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(uri, "application/vnd.android.package-archive")
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}

		context.startActivity(intent)
	}
}