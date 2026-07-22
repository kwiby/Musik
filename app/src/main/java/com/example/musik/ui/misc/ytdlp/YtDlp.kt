package com.example.musik.ui.misc.ytdlp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.musik.data.datastore.DataStoreManager
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class YtDlp(
	private val appContext: Context
) {
	sealed interface DownloadResult {
		data object Success: DownloadResult
		data object OutdatedYtDlp: DownloadResult
		data object VideoUnavailable: DownloadResult
		data object Error: DownloadResult
	}

	private val linkCheckRegex = Regex(
		"""^(https?://)?(www\.|m\.)?(youtube\.com/watch\?v=|youtu\.be/)[a-zA-Z0-9_-]{11}(&\S*)?(\?\S*)?$"""
	)
	private val downloadSpeedRegex = Regex(
		"""at\s+([\d.]+\s*\w+/s)"""
	)
	private val etaRegex = Regex(
		"""ETA\s+(\d+:\d+)"""
	)

	private val _downloadPercent = MutableStateFlow(0)
	val downloadPercent: StateFlow<Int> = _downloadPercent.asStateFlow()

	private val _downloadSpeed = MutableStateFlow("0.0KiB/s")
	val downloadSpeed = _downloadSpeed.asStateFlow()

	private val _eta = MutableStateFlow("00:00")
	val eta = _eta.asStateFlow()

	private val _ytDlpVersion = MutableStateFlow("UNKNOWN")
	val ytDlpVersion: StateFlow<String> = _ytDlpVersion.asStateFlow()

	private val processId: String = "MusikProcess"
	private val callback = { percent: Float, _: Long, line: String ->
		CoroutineScope(Dispatchers.Main).launch {
			_downloadPercent.value = percent.toInt()

			extractDownloadSpeed(line)?.let { _downloadSpeed.value = it }
			extractEta(line)?.let { _eta.value = it }
			Log.d("debug", line)
		}
		Unit
	}


	private fun extractDownloadSpeed(line: String): String? {
		return downloadSpeedRegex.find(line)?.groupValues?.get(1)
	}
	private fun extractEta(line: String): String? {
		return etaRegex.find(line)?.groupValues?.get(1)
	}


	private fun isOutdatedYtDlpWarning(responseOutput: String): Boolean {
		return responseOutput.contains("is older than 90 days", ignoreCase = true)
	}
	private fun isVideoUnavailable(responseOutput: String): Boolean {
		return responseOutput.contains("Video unavailable", ignoreCase = true)
	}

	private fun getMimeTypeFromExtension(extension: String): String {
		return when (extension.lowercase()) {
			"mp3" -> "audio/mpeg"
			"m4a" -> "audio/mp4"
			"opus" -> "audio/opus"
			"ogg", "oga" -> "audio/ogg"
			"wav" -> "audio/wav"
			"flac" -> "audio/flac"
			"webm" -> "audio/webm"
			"aac" -> "audio/aac"
			else -> "application/octet-stream"
		}
	}

	private fun moveFileToDownloadLocation(appContext: Context, sourceFile: File, treeUri: Uri): Boolean {
		try {
			val downloadLocation = DocumentFile.fromTreeUri(appContext, treeUri)
			val mimeType = getMimeTypeFromExtension(sourceFile.extension)
			val newFile = downloadLocation?.createFile(mimeType, sourceFile.name)
				?: throw Exception("Download location is null")

			newFile.uri.let { destUri ->
				appContext.contentResolver.openOutputStream(destUri)?.use { outputStream ->
					sourceFile.inputStream().use { inputStream ->
						inputStream.copyTo(outputStream)
					}
				}
			}
			sourceFile.delete()

			return true
		} catch (e: Exception) {
			Log.e("YtDlp", "Failed to move file", e)

			return false
		}
	}

	suspend fun startDownload(
		doConvertMp3: Boolean,
		downloadLocationStr: String,
		link: String
	): DownloadResult {
		val tempDir = File(appContext.cacheDir, "musik_temp_dir")
		if (!tempDir.exists()) {
			tempDir.mkdirs()
		}
		tempDir.listFiles()?.forEach { it.delete() }

		val request = YoutubeDLRequest(link)
		/*
		 * "--no-mtime" prevents the file's "last modified" time to be set to the date the YouTube
		 * video was originally uploaded
		 */
		request.addOption("-f", "bestaudio/best")
		request.addOption("--extract-audio", "")
		if (doConvertMp3) {
			request.addOption("--audio-format", "mp3")
		}
		request.addOption("--add-metadata")
		request.addOption("--no-mtime")
		request.addOption("--embed-thumbnail")
		request.addOption("-o", "${tempDir.absolutePath}/%(title)s.%(ext)s")

		try {
			val response = withContext(Dispatchers.IO) {
				YoutubeDL.getInstance().execute(request, processId, callback)
			}

			if (response.exitCode == 0) {
				val validAudioExtensions = setOf("mp3", "m4a", "opus", "ogg", "wav", "flac", "webm", "aac")

				val downloadedFile = tempDir.listFiles { file ->
					file.extension.lowercase() in validAudioExtensions
				}?.maxByOrNull { it.lastModified() }
					?: run {
						Log.e("YtDlp", "No audio file found in temp directory after download")
						return DownloadResult.Error
					}
				val isMoveSuccess = withContext(Dispatchers.IO) {
					moveFileToDownloadLocation(
						appContext,
						downloadedFile,
						downloadLocationStr.toUri()
					)
				}
				_downloadPercent.value = 0

				if (isMoveSuccess) {
					emitToast("Download completed")
					return DownloadResult.Success
				} else {
					emitToast("Download failed")
					return DownloadResult.Error
				}
			} else {
				Log.e("YtDlp", "UNEXPECTED ERROR")

				emitToast("Download failed")
				return DownloadResult.Error
			}
		} catch (e: Exception) {
			Log.e("YtDlp", "DOWNLOAD FAILED: ${e.message}")

			return if (e.message != null) {
				val msg = e.message!!

				// Outdated YtDlp warning should precede video unavailable warning
				if (isOutdatedYtDlpWarning(msg)) {
					DownloadResult.OutdatedYtDlp
				} else if (isVideoUnavailable(msg)) {
					DownloadResult.VideoUnavailable
				} else {
					DownloadResult.Error
				}
			} else {
				DownloadResult.Error
			}
		}
	}

	fun checkValidLink(link: String): Boolean {
		return link.isNotBlank() && linkCheckRegex.containsMatchIn(link)
	}

	// --===--  Update YtDlp  --===--
	private var isUpdating = false

	private fun emitToast(text: String) {
		Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
	}

	suspend fun updateYtDlp(
		channel: YoutubeDL.UpdateChannel,
		dataStoreManager: DataStoreManager
	) {
		if (isUpdating) {
			emitToast("Update already in progress")
			return
		}

		val selectedChannel = when (channel) {
			YoutubeDL.UpdateChannel.STABLE -> "stable"
			YoutubeDL.UpdateChannel.NIGHTLY -> "nightly"
			YoutubeDL.UpdateChannel.MASTER -> "master"
			else -> "UNKNOWN"
		}

		isUpdating = true
		emitToast("Updating from $selectedChannel channel")
		try {
			val status = withContext(Dispatchers.IO) {
				YoutubeDL.getInstance().updateYoutubeDL(appContext, channel)
			}

			val newVersionName = getFormattedVersionName(
				YoutubeDL.getInstance().versionName(appContext)
			)

			if (status == YoutubeDL.UpdateStatus.DONE || status == YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE) {
				dataStoreManager.setYtDlpVersion(newVersionName)
			}

			val msg = when (status) {
				YoutubeDL.UpdateStatus.DONE ->
					"Successfully updated to $newVersionName"
				YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE ->
					"Already up to date with $newVersionName"
				else -> status.toString()
			}
			emitToast(msg)

			_ytDlpVersion.value = newVersionName
		} catch (e: Exception) {
			Log.e("SettingsViewModel", "Update from $selectedChannel channel failed", e)
			emitToast("Failed to update")
		} finally {
			isUpdating = false
		}
	}
}