package com.kwiby.musik.ui.view_models

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kwiby.musik.data.data_classes.Metadata
import com.kwiby.musik.data.repositories.music_list.MusicListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val LOG_TAG = "EditMetadataViewModel"

class EditMetadataViewModel(
	private val musicListRepo: MusicListRepository
) : ViewModel() {
	var isLoading = mutableStateOf(true)
		private set
	var id = mutableStateOf<Long?>(null)
		private set
	var metadata = mutableStateOf<Metadata?>(null)
		private set

	fun getFilepath(context: Context, uri: Uri): String? {
		val projection = arrayOf(
			MediaStore.Audio.Media.RELATIVE_PATH,
			MediaStore.Audio.Media.DISPLAY_NAME
		)
		context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
			if (cursor.moveToFirst()) {
				val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH))
				val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))

				return "$relativePath$fileName"
			}
		}

		return null
	}

	private fun getMetadata(context: Context, contentUri: Uri): Metadata? {
		val retriever = MediaMetadataRetriever()
		try {
			retriever.setDataSource(context, contentUri)

			return Metadata(
				filePath = getFilepath(context, contentUri),
				bitRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
				durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
				sampleRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
				} else {
					null
				},
				mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
				artwork = retriever.embeddedPicture,
				title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
				artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
				album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
				albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
				trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER),
				discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER),
				genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
				year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
			)
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Failed to retrieve metadata for contentUri=$contentUri", e)
			return null
		} finally {
			retriever.release()
		}
	}

	suspend fun setup(context: Context, contentUri: Uri, id: Long) {
		if (this.id.value != id) {
			this.metadata.value = null
			this.isLoading.value = true
		}
		this.id.value = id

		val newMetadata = withContext(Dispatchers.IO) {
			getMetadata(context, contentUri)
		}
		metadata.value = newMetadata

		isLoading.value = false
	}

	fun onDispose() {
		isLoading.value = true
		id.value = null
		metadata.value = null
	}

	fun saveButton() {

	}
}