package com.example.musik.ui.misc

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class YtDlp(context: Context) {
	data class VideoInfo(
		val isValid: Boolean,
		val title: String? = null,
		val id: String? = null,
		val error: String? = null,
	)

	data class DownloadResult(
		val isSuccess: Boolean,
		val title: String? = null,
		val file: File? = null,
		val error: String? = null,
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

	/**
	 * Downloads audio to a temporary app-private file. Caller is responsible for
	 * moving/copying the result wherever it's ultimately needed (e.g. a content URI)
	 * and deleting the temp file afterward — see [downloadAudioToContentUri] for
	 * a version that does this automatically.
	 */
	private suspend fun downloadAudio(url: String): DownloadResult = withContext(Dispatchers.IO) {
		val module = getPythonModule()

		val result: PyObject = module.callAttr(
			"download_audio",
			url,
			tempDir.absolutePath,
			ffmpegPath,
			qjsPath,
		)
		val map = result.asMap()

		val successCheck = map[PyObject.fromJava("isSuccess")]?.toBoolean() ?: false
		if (successCheck) {
			val path = map[PyObject.fromJava("path")]?.toString()

			DownloadResult(
				isSuccess = true,
				file = path?.let { File(it) },
				title = map[PyObject.fromJava("title")]?.toString(),
			)
		} else {
			DownloadResult(
				isSuccess = false,
				error = map[PyObject.fromJava("error")]?.toString(),
			)
		}
	}

	private fun copyToContentUri(sourceFile: File, treeUri: Uri, mimeType: String) {
		val treeDoc = DocumentFile.fromTreeUri(appContext, treeUri)
			?: throw IllegalStateException("Invalid tree URI")

		val newFile = treeDoc.createFile(mimeType, sourceFile.nameWithoutExtension)
			?: throw IllegalStateException("Could not create file in target directory")

		appContext.contentResolver.openOutputStream(newFile.uri)?.use { output ->
			sourceFile.inputStream().use { input ->
				input.copyTo(output)
			}
		} ?: throw IllegalStateException("Could not open output stream for destination file")
	}

	/**
	 * Downloads audio and copies the result into a SAF tree URI (a directory the
	 * user picked via ACTION_OPEN_DOCUMENT_TREE). Cleans up the temp file afterward.
	 */
	suspend fun downloadAudioToContentUri(
		url: String,
		treeUri: Uri,
		mimeType: String = "audio/mpeg",
	): DownloadResult = withContext(Dispatchers.IO) {
		val result = downloadAudio(url)
		if (!result.isSuccess || result.file == null) {
			return@withContext result
		}

		try {
			copyToContentUri(result.file, treeUri, mimeType)
			DownloadResult(isSuccess = true, title = result.title)
		} catch (e: Exception) {
			DownloadResult(isSuccess = false, error = "Failed to copy to destination: ${e.message}")
		} finally {
			result.file.delete()
		}
	}

	// Checks whether a YouTube URL is valid/accessible (makes a network call)
	suspend fun checkValidLink(url: String): Boolean = withContext(Dispatchers.IO) {
		val t0 = System.currentTimeMillis()
		val t1 = System.currentTimeMillis()
		val module = getPythonModule()
		val t2 = System.currentTimeMillis()
		val result: PyObject = module.callAttr(
			"check_valid_link",
			url
		)
		val t3 = System.currentTimeMillis()
		Log.d("debug", "getInstance=${t1-t0}ms getModule=${t2-t1}ms callAttr=${t3-t2}ms")

		val map = result.asMap()

		Log.d("debug", "$result")

		val isValid = map[PyObject.fromJava("isValid")]?.toBoolean() ?: false
		if (!isValid) {
			Log.e("YtDlp", "checkValidLink failed: ${map[PyObject.fromJava("error")]}")
		}

		isValid
	}
	/*
	suspend fun checkValidLink(url: String): VideoInfo = withContext(Dispatchers.IO) {
		val py = Python.getInstance()
		val module = py.getModule("ytdlp")
		val result: PyObject = module.callAttr("check_video_valid", url)
		val map = result.asMap()

		val valid = map[PyObject.fromJava("isValid")]?.toBoolean() ?: false
		if (valid) {
			VideoInfo(
				isValid = true,
				title = map[PyObject.fromJava("title")]?.toString(),
				id = map[PyObject.fromJava("id")]?.toString(),
			)
		} else {
			VideoInfo(
				isValid = false,
				error = map[PyObject.fromJava("error")]?.toString(),
			)
		}
	}
	 */
}
