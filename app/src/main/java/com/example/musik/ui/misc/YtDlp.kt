package com.example.musik.ui.misc

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
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
	context: Context
) {
	private val idCheckRegex = "v=([a-zA-Z0-9_-]{11})".toRegex()

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

	suspend fun startDownload(context: Context, downloadLocationStr: String, link: String): Boolean {
		val tempDir = File(context.cacheDir, "musik_temp_dir")
		if (!tempDir.exists()) {
			tempDir.mkdirs()
		}

		val downloadLocation = File(downloadLocationStr)

		val request = YoutubeDLRequest(link)
		/*
		 * Prevents the file's "last modified" time to be set to the date the YouTube video was
		 * originally uploaded
		 */
		request.addOption("--no-mtime")
		/*
		 * Embeds metadata as ID3 tags
		 */
		request.addOption("--add-metadata")
		/*
		 * Embeds the video thumbnail as the audio file thumbnail
		 */
		request.addOption("--embed-thumbnail")
		/*
		 * Prioritize the highest quality audio stream available.
		 */
		request.addOption("-f", "bestaudio/best")
		/*
		 * Ignore the video stream, and only maintain the audio track.
		 */
		request.addOption("--extract-audio", "")
		/*
		 * Convert the audio file to .mp3 (with ffmpeg)
		 */
		request.addOption("--audio-format", "mp3")
		/*
		 * Sets the audio file output name to be "audio_file_title_example.mp3"
		 */
		request.addOption("-o", "${tempDir.absolutePath}/%(title)s.%(ext)s")

		try {
			val response = withContext(Dispatchers.IO) {
				YoutubeDL.getInstance().execute(request, processId, callback)
			}
			if (response.exitCode == 0) {
				val downloadedFile = File(response.out)
				val isSuccess = withContext(Dispatchers.IO) {
					moveFileToDownloadLocation(context, downloadedFile, downloadLocationStr.toUri())
				}
				_progressValue.value = 100

				return isSuccess
			} else {
				Log.e("YtDlp", "UNEXPECTED ERROR")

				return false
			}
		} catch (e: Exception) {
			Log.e("YtDlp", "DOWNLOAD FAILED: ${e.message}")

			return false
		} finally {
			_downloadStatus.value = ""
		}
	}

	suspend fun checkValidLink(link: String): Boolean {
		if (link.isBlank() || !idCheckRegex.containsMatchIn(link)) {
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
	}

	fun moveFileToDownloadLocation(context: Context, sourceFile: File, treeUri: Uri): Boolean {
		try {
			val downloadLocation = DocumentFile.fromTreeUri(context, treeUri)
			val newFile = downloadLocation?.createFile("audio/mpeg", sourceFile.name)
				?: throw Exception("Download location is null")

			newFile.uri.let { destUri ->
				context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
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

	/*
	/*
	data class VideoInfo(
		val isValid: Boolean,
		val title: String? = null,
		val id: String? = null,
		val error: String? = null
	)
	 */

	data class DownloadResult(
		val isSuccess: Boolean,
		val title: String? = null,
		val file: File? = null,
		val error: String? = null
	)


	private val appContext = context.applicationContext
	private val tempDir: File get() = File(appContext.filesDir, "musik_temp_dir").apply {
		mkdirs()
	}

	val ffmpegPath: String by lazy {
		File(appContext.applicationInfo.nativeLibraryDir, "libffmpeg.so").absolutePath
	}
	val qjsPath: String by lazy {
		File(appContext.applicationInfo.nativeLibraryDir, "libqjs.so").absolutePath
	}


	private fun getPythonModule(): PyObject {
		return Python.getInstance().getModule("ytdlp")
	}

	private fun copyToContentUri(
		sourceFile: File,
		downloadLocationUri: Uri
	) {
		val treeDoc = DocumentFile.fromTreeUri(appContext, downloadLocationUri)
			?: throw IllegalStateException("Invalid tree URI")
		val newFile = treeDoc.createFile("audio/mp4", sourceFile.nameWithoutExtension)
			?: throw IllegalStateException("Could not create file in target directory")

		appContext.contentResolver.openOutputStream(newFile.uri)?.use { output ->
			sourceFile.inputStream().use { input ->
				input.copyTo(output)
			}
		} ?: throw IllegalStateException("Could not open output stream for destination file")
	}

	/*
	 * Downloads audio to a temporary app-private file. Caller is responsible for
	 * moving/copying the result wherever it's ultimately needed (e.g. a content URI)
	 * and deleting the temp file afterward — see [downloadAudioToContentUri] for
	 * a version that does this automatically.
	 */
	private suspend fun downloadAudio(
		link: String
	): DownloadResult = withContext(Dispatchers.IO) {
		val module = getPythonModule()
		val result: PyObject = module.callAttr(
			"download_audio",
			link,
			tempDir.absolutePath,
			ffmpegPath,
			qjsPath,
		)
		val map = result.asMap()

		val successCheck = map[PyObject.fromJava("isSuccess")]?.toBoolean() ?: false
		if (successCheck) {
			val path = map[PyObject.fromJava("path")]?.toString()

			return@withContext DownloadResult(
				isSuccess = true,
				title = map[PyObject.fromJava("title")]?.toString(),
				file = path?.let { File(it) },
				error = null
			)
		} else {
			return@withContext DownloadResult(
				isSuccess = false,
				title = null,
				file = null,
				error = map[PyObject.fromJava("error")]?.toString(),
			)
		}
	}

	/**
	 * Downloads audio and copies the result into a SAF tree URI (a directory the
	 * user picked via ACTION_OPEN_DOCUMENT_TREE). Cleans up the temp file afterward.
	 */
	suspend fun downloadAudioToContentUri(
		link: String,
		downloadLocationUri: Uri
	): DownloadResult = withContext(Dispatchers.IO) {
		val result = downloadAudio(link)
		if (!result.isSuccess || result.file == null) {
			return@withContext result
		}

		try {
			copyToContentUri(result.file, downloadLocationUri)

			return@withContext DownloadResult(
				isSuccess = true,
				title = result.title,
				file = null,
				error = null
			)
		} catch (e: Exception) {
			return@withContext DownloadResult(
				isSuccess = false,
				title = null,
				file = null,
				error = "Failed to copy to destination: ${e.message}"
			)
		} finally {
			result.file.delete()
		}
	}

	// Checks whether a YouTube link is valid/accessible (makes a network call)
	suspend fun checkValidLink(
		link: String
	): Boolean = withContext(Dispatchers.IO) {
		val module = getPythonModule()
		val result: PyObject = module.callAttr(
			"check_valid_link",
			link
		)

		val map = result.asMap()

		Log.d("debug", "$result")

		val isValid = map[PyObject.fromJava("isValid")]?.toBoolean() ?: false
		if (!isValid) {
			Log.d("YtDlp", "checkValidLink failed: ${map[PyObject.fromJava("error")]}")
		}

		return@withContext isValid
	}
	*/
}