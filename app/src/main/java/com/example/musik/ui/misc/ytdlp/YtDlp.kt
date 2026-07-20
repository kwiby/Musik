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
		"^(https?://)?(www\\.|m\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}(&\\S*)?(\\?\\S*)?$"
	)

	private val _downloadStatus = MutableStateFlow("")
	val downloadStatus: StateFlow<String> = _downloadStatus.asStateFlow()

	private val _progressValue = MutableStateFlow(0)
	val progressValue: StateFlow<Int> = _progressValue.asStateFlow()

	private val processId: String = "MusikProcess"
	private val callback = { progress: Float, _: Long, line: String ->
		CoroutineScope(Dispatchers.Main).launch {
			_progressValue.value = progress.toInt()
			_downloadStatus.value = line
		}
		Unit
	}

	private val _ytDlpVersion = MutableStateFlow("UNKNOWN")
	val ytDlpVersion: StateFlow<String> = _ytDlpVersion.asStateFlow()


	private fun isOutdatedYtDlpWarning(responseOutput: String): Boolean {
		return responseOutput.contains("is older than 90 days", ignoreCase = true)
	}
	private fun isVideoUnavailable(responseOutput: String): Boolean {
		return responseOutput.contains("Video unavailable", ignoreCase = true)
	}

	private fun moveFileToDownloadLocation(appContext: Context, sourceFile: File, treeUri: Uri): Boolean {
		try {
			val downloadLocation = DocumentFile.fromTreeUri(appContext, treeUri)
			val newFile = downloadLocation?.createFile("audio/mpeg", sourceFile.name)
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
		downloadLocationStr: String,
		link: String
	): DownloadResult {
		val tempDir = File(appContext.cacheDir, "musik_temp_dir")
		if (!tempDir.exists()) {
			tempDir.mkdirs()
		}

		val request = YoutubeDLRequest(link)
		/*
		 * "--no-mtime" prevents the file's "last modified" time to be set to the date the YouTube
		 * video was originally uploaded
		 */
		request.addOption("--no-mtime")
		request.addOption("--add-metadata")
		request.addOption("--embed-thumbnail")
		request.addOption("-f", "bestaudio/best")
		request.addOption("--extract-audio", "")
		request.addOption("--audio-format", "mp3")
		request.addOption("-o", "${tempDir.absolutePath}/%(title)s.%(ext)s")

		try {
			val response = withContext(Dispatchers.IO) {
				YoutubeDL.getInstance().execute(request, processId, callback)
			}

			if (response.exitCode == 0) {
				val downloadedFile = tempDir.listFiles { file ->
					file.extension == "mp3"
				}?.firstOrNull()
					?: run {
						Log.e("YtDlp", "No mp3 file found in temp directory after download")
						return DownloadResult.Error
					}
				val isMoveSuccess = withContext(Dispatchers.IO) {
					moveFileToDownloadLocation(
						appContext,
						downloadedFile,
						downloadLocationStr.toUri()
					)
				}
				_progressValue.value = 100

				return if (isMoveSuccess) {
					DownloadResult.Success
				} else {
					DownloadResult.Error
				}
			} else {
				Log.e("YtDlp", "UNEXPECTED ERROR")

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
		} finally {
			_downloadStatus.value = ""
		}
	}

	//private val idCheckRegex = Regex("v=([a-zA-Z0-9_-]{11})")
	fun checkValidLink(link: String): Boolean {
		return link.isNotBlank() && linkCheckRegex.containsMatchIn(link)
		/*
		if (link.isBlank()) {
			return false
		} else {
			val request = YoutubeDLRequest(link)
			/*
			 * Fetches the ID
			 */
			request.addOption("--get-id")

			try {
				val response = withContext(Dispatchers.IO) {
					YoutubeDL.getInstance().execute(request, null, null)
				}

				return response.exitCode == 0
			} catch (_: Exception) {
				return false
			}
		}
		 */
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

			if (status == YoutubeDL.UpdateStatus.DONE) {
				Log.d("debug", newVersionName)
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