package com.kwiby.musik.ui.view_models

import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.coil.ArtworkCacheKeys
import com.kwiby.musik.data.data_classes.Metadata
import com.kwiby.musik.data.datastore.DataStoreManager
import com.kwiby.musik.data.repositories.music_list.MusicListRepository
import com.kwiby.musik.ui.MusikApplication
import com.kwiby.musik.ui.misc.folder_manager.FolderManager
import com.kwiby.musik.ui.misc.scanFileAndAwait
import com.kwiby.musik.ui.screens.edit_metadata.components.MetadataEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Locale

private const val LOG_TAG = "EditMetadataViewModel"

class EditMetadataViewModel(
	application: MusikApplication,
	private val dataStoreManager: DataStoreManager,
	private val musicListRepo: MusicListRepository
) : AndroidViewModel(application) {
	private var defaultMetadata = mutableStateOf<Metadata?>(null)
	private var uri = mutableStateOf<Uri?>(null)

	private var wasArtworkChanged = mutableStateOf(false)
	var isLoading = mutableStateOf(true)
		private set
	var id = mutableStateOf<Long?>(null)
		private set
	var metadata = mutableStateOf<Metadata?>(null)
		private set

	var pendingWriteRequest = mutableStateOf<IntentSender?>(null)
		private set

	var artworkQuery = mutableStateOf<ByteArray?>(null)
	var titleQuery = mutableStateOf("")
	var artistQuery = mutableStateOf("")
	var albumQuery = mutableStateOf("")
	var albumArtistQuery = mutableStateOf("")
	var trackNumberQuery = mutableStateOf("")
	var discNumberQuery = mutableStateOf("")
	var genreQuery = mutableStateOf("")
	var yearQuery = mutableStateOf("")

	private fun getFilePath(context: Context, uri: Uri): String? {
		if (DocumentsContract.isDocumentUri(context, uri)) {
			val docId = DocumentsContract.getDocumentId(uri)
			val split = docId.split(":", limit = 2)
			if (split.size != 2 || !"primary".equals(split[0], ignoreCase = true)) {
				return null
			}

			return split[1]
		} else if ("media".equals(uri.authority, ignoreCase = true)) {
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
		}

		return null
	}

	private fun getFileSize(context: Context, contentUri: Uri): String? {
		val projection = arrayOf(MediaStore.Audio.Media.SIZE)
		context.contentResolver.query(contentUri, projection, null, null, null)?.use { cursor ->
			if (cursor.moveToFirst()) {
				val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
				val sizeBytes = cursor.getLong(sizeIndex)
				val sizeMB = sizeBytes / 1000000.0

				return String.format(Locale.US, "%.2f MB", sizeMB)
			}
		}

		return null
	}

	private fun getMetadata(context: Context, contentUri: Uri): Metadata? {
		val retriever = MediaMetadataRetriever()
		try {
			retriever.setDataSource(context, contentUri)
			return Metadata(
				filePath = getFilePath(context, contentUri),
				fileSizeMB = getFileSize(context, contentUri),
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

	suspend fun setup(uri: Uri, id: Long) {
		if (this.id.value != id) {
			this.metadata.value = null
			this.isLoading.value = true
		}
		this.id.value = id
		this.uri.value = uri

		val newMetadata = withContext(Dispatchers.IO) {
			getMetadata(getApplication(), uri)
		}
		metadata.value = newMetadata
		defaultMetadata.value = newMetadata

		val metadata = metadata.value
		artworkQuery.value = metadata?.artwork
		titleQuery.value = metadata?.title ?: ""
		artistQuery.value = metadata?.artist ?: ""
		albumQuery.value = metadata?.album ?: ""
		albumArtistQuery.value = metadata?.albumArtist ?: ""
		trackNumberQuery.value = metadata?.trackNumber ?: ""
		discNumberQuery.value = metadata?.discNumber ?: ""
		genreQuery.value = metadata?.genre ?: ""
		yearQuery.value = metadata?.year ?: ""
		wasArtworkChanged.value = false

		isLoading.value = false
	}

	fun onDispose() {
		isLoading.value = true
		id.value = null
		metadata.value = null
		wasArtworkChanged.value = false
	}

	fun resetButton() {
		val defaultMetadata = defaultMetadata.value
		metadata.value = defaultMetadata

		artworkQuery.value = defaultMetadata?.artwork
		titleQuery.value = defaultMetadata?.title ?: ""
		artistQuery.value = defaultMetadata?.artist ?: ""
		albumQuery.value = defaultMetadata?.album ?: ""
		albumArtistQuery.value = defaultMetadata?.albumArtist ?: ""
		trackNumberQuery.value = defaultMetadata?.trackNumber ?: ""
		discNumberQuery.value = defaultMetadata?.discNumber ?: ""
		genreQuery.value = defaultMetadata?.genre ?: ""
		yearQuery.value = defaultMetadata?.year ?: ""
		wasArtworkChanged.value = false
	}

	fun isBlankOrNumerical(str: String): Boolean {
		val trimmedStr = str.trim()
		return trimmedStr.isBlank() || (trimmedStr.toIntOrNull() != null && trimmedStr.toInt() >= 0)
	}

	fun isSavable(): Boolean {
		return isBlankOrNumerical(trackNumberQuery.value)
				&& isBlankOrNumerical(discNumberQuery.value)
				&& isBlankOrNumerical(yearQuery.value)
	}

	fun saveButton(
		folderManager: FolderManager,
		navToMainScreen: () -> Unit,
		refreshMediaItemFunc: (Long) -> Unit
	) {
		if (uri.value == null) {
			Log.e(LOG_TAG, "Uri is null")
			return
		}
		if (metadata.value == null) {
			Log.e(LOG_TAG, "Metadata is null")
			return
		}
		if (!isSavable()) {
			return
		}

		val editedMetadata = metadata.value!!.copy(
			artwork = if (!artworkQuery.value.contentEquals(defaultMetadata.value?.artwork))
				artworkQuery.value else null,
			title = if (titleQuery.value != defaultMetadata.value?.title)
				titleQuery.value else null,
			artist = if (artistQuery.value != defaultMetadata.value?.artist)
				artistQuery.value else null,
			album = if (albumQuery.value != defaultMetadata.value?.album)
				albumQuery.value else null,
			albumArtist = if (albumArtistQuery.value != defaultMetadata.value?.albumArtist)
				albumArtistQuery.value else null,
			trackNumber = if (trackNumberQuery.value != defaultMetadata.value?.trackNumber)
				trackNumberQuery.value else null,
			discNumber = if (discNumberQuery.value != defaultMetadata.value?.discNumber)
				discNumberQuery.value else null,
			genre = if (genreQuery.value != defaultMetadata.value?.genre)
				genreQuery.value else null,
			year = if (yearQuery.value != defaultMetadata.value?.year)
				yearQuery.value else null
		)

		val metadataEditor = MetadataEditor(
			context = getApplication(),
			dataStoreManager = dataStoreManager,
			folderManager = folderManager
		)

		viewModelScope.launch(Dispatchers.IO) {
			val result = metadataEditor.editMetadata(
				uri = uri.value!!,
				metadata = editedMetadata,
				wasArtworkChanged = wasArtworkChanged.value
			)

			when (result) {
				is MetadataEditor.EditResult.Success -> {
					metadata.value?.filePath?.let { path ->
						scanFileAndAwait(getApplication(), path)
					}

					id.value?.let { trackId ->
						ArtworkCacheKeys.markEdited(trackId.toString())

						withContext(Dispatchers.Main) {
							refreshMediaItemFunc(trackId)
						}
					}

					withContext(Dispatchers.Main) {
						navToMainScreen()
					}
				}
				is MetadataEditor.EditResult.NeedsPermission -> {
					pendingWriteRequest.value = result.intentSender
				}
				else -> {
					Log.e(LOG_TAG, "Save failed: $result")
				}
			}
		}
	}

	fun onWriteRequestGranted(
		folderManager: FolderManager,
		navToMainScreen: () -> Unit,
		refreshMediaItemFunc: (Long) -> Unit
	) {
		pendingWriteRequest.value = null
		saveButton(
			folderManager,
			navToMainScreen,
			refreshMediaItemFunc
		)
	}
	fun clearPendingWriteRequest() {
		pendingWriteRequest.value = null
	}

	// --===--  Artwork  --===--
	fun selectImageButton(context: Context, uri: Uri) {
		try {
			val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
				?: return

			val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

			// --===--  Orientation  --===--
			val orientation = ByteArrayInputStream(bytes).use { stream ->
				ExifInterface(stream).getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL
				)
			}

			val rotationDegrees = when (orientation) {
				ExifInterface.ORIENTATION_ROTATE_90 -> 90f
				ExifInterface.ORIENTATION_ROTATE_180 -> 180f
				ExifInterface.ORIENTATION_ROTATE_270 -> 270f
				else -> 0f
			}

			val rotated = if (rotationDegrees != 0f) {
				val matrix = Matrix().apply { postRotate(rotationDegrees) }
				Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
					.also { if (it !== original) original.recycle() }
			} else {
				original
			}

			// --===--  Cropping  --===--
			val cropSize = minOf(rotated.width, rotated.height)
			val xOffset = (rotated.width - cropSize) / 2
			val yOffset = (rotated.height - cropSize) / 2
			val cropped = Bitmap.createBitmap(rotated, xOffset, yOffset, cropSize, cropSize)
				.also { if (it !== rotated) rotated.recycle() }

			// --===--  Size  --===--
			val maxDimension = 1000
			val scale = minOf(1f, maxDimension.toFloat() / cropSize)
			val resized = if (scale < 1f) {
				cropped.scale((cropSize * scale).toInt(), (cropSize * scale).toInt()).also {
					if (it !== cropped) {
						cropped.recycle()
					}
				}
			} else {
				cropped
			}

			val outputStream = ByteArrayOutputStream()
			resized.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
			val compressedBytes = outputStream.toByteArray()

			artworkQuery.value = compressedBytes
			wasArtworkChanged.value = true

			resized.recycle()
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Failed to read bytes from uri=$uri", e)
		}
	}

	fun removeImageButton() {
		if (artworkQuery.value != null) {
			artworkQuery.value = null
			wasArtworkChanged.value = true
		}
	}
}