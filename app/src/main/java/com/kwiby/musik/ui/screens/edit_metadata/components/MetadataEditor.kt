package com.kwiby.musik.ui.screens.edit_metadata.components

import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import com.kwiby.musik.data.data_classes.Metadata
import com.kwiby.musik.data.datastore.DataStoreManager
import com.kwiby.musik.ui.misc.folder_manager.FolderManager
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import kotlinx.coroutines.flow.first

private const val LOG_TAG = "MetadataEditor"

class MetadataEditor(
	private val context: Context,
	private val dataStoreManager: DataStoreManager,
	private val folderManager: FolderManager
) {
	sealed class EditResult {
		object Success: EditResult()
		data class NeedsPermission(val intentSender: IntentSender): EditResult()
		data class NeedsFolderReselect(val msg: String): EditResult()
		data class Error(val error: Throwable): EditResult()
	}
	sealed class PermissionCheckResult {
		object Granted : PermissionCheckResult()
		data class NeedsPermission(val intentSender: IntentSender) : PermissionCheckResult()
	}

	private fun getMimeTypeFromByteArray(byteArray: ByteArray): String {
		return when {
			byteArray.size >= 2
					&& byteArray[0] == 'B'.code.toByte()
					&& byteArray[1] == 'M'.code.toByte()
				-> "image/bmp"
			byteArray.size >= 3
					&& byteArray[0] == 0xFF.toByte()
					&& byteArray[1] == 0xD8.toByte()
					&& byteArray[2] == 0xFF.toByte()
						 -> "image/jpeg"
			byteArray.size >= 4
					&& byteArray[0] == 0x89.toByte()
					&& byteArray[1] == 0x50.toByte()
					&& byteArray[2] == 0x4E.toByte()
					&& byteArray[3] == 0x47.toByte()
						-> "image/png"
			byteArray.size >= 4
					&& byteArray[0] == 'G'.code.toByte()
					&& byteArray[1] == 'I'.code.toByte()
					&& byteArray[2] == 'F'.code.toByte()
					&& byteArray[3] == '8'.code.toByte()
						-> "image/gif"
			byteArray.size >= 12
					&& byteArray[0] == 'R'.code.toByte()
					&& byteArray[1] == 'I'.code.toByte()
					&& byteArray[2] == 'F'.code.toByte()
					&& byteArray[3] == 'F'.code.toByte()
					&& byteArray[8] == 'W'.code.toByte()
					&& byteArray[9] == 'E'.code.toByte()
					&& byteArray[10] == 'B'.code.toByte()
					&& byteArray[11] == 'P'.code.toByte()
						-> "image/webp"
			byteArray.size >= 12
					&& byteArray[4] == 'f'.code.toByte()
					&& byteArray[5] == 't'.code.toByte()
					&& byteArray[6] == 'y'.code.toByte()
					&& byteArray[7] == 'p'.code.toByte()
					&& byteArray[8] == 'a'.code.toByte()
					&& byteArray[9] == 'v'.code.toByte()
					&& byteArray[10] == 'i'.code.toByte()
					&& byteArray[11] == 'f'.code.toByte()
						 -> "image/avif"
			else -> "application/octet-stream"
		}
	}

	private fun performEdit(
		uri: Uri,
		metadata: Metadata,
		wasArtworkChanged: Boolean
	) {
		val existing = context.contentResolver.openFileDescriptor(uri, "r")?.use { readPfd ->
			val readFd = readPfd.detachFd()
			TagLib.getMetadata(readFd)
		} ?: throw IllegalStateException("Could not read metadata for uri=$uri")
		val propertyMap = existing.propertyMap.toMutableMap()

		metadata.title?.let { propertyMap["TITLE"] = arrayOf(it) }
		metadata.artist?.let { propertyMap["ARTIST"] = arrayOf(it) }
		metadata.album?.let { propertyMap["ALBUM"] = arrayOf(it) }
		metadata.albumArtist?.let { propertyMap["ALBUMARTIST"] = arrayOf(it) }
		metadata.trackNumber?.let { propertyMap["TRACKNUMBER"] = arrayOf(it) }
		metadata.discNumber?.let { propertyMap["DISCNUMBER"] = arrayOf(it) }
		metadata.genre?.let { propertyMap["GENRE"] = arrayOf(it) }
		metadata.year?.let { propertyMap["YEAR"] = arrayOf(it) }

		if (wasArtworkChanged) {
			context.contentResolver.openFileDescriptor(uri, "rw")?.use { picPfd ->
				val picFd = picPfd.detachFd()
				val saved = if (metadata.artwork != null) {
					val mimeType = getMimeTypeFromByteArray(metadata.artwork)
					val picture = Picture(
						data = metadata.artwork,
						description = "Front Cover",
						pictureType = "Front Cover",
						mimeType = mimeType
					)

					TagLib.savePictures(picFd, arrayOf(picture))
				} else {
					TagLib.savePictures(picFd, arrayOf())
				}

				if (!saved) {
					Log.w(LOG_TAG, "TagLib failed to update picture (add or remove).")
				}
			}
		}

		context.contentResolver.openFileDescriptor(uri, "rw")?.use { propPfd ->
			val propFd = propPfd.detachFd()
			val saved = TagLib.savePropertyMap(propFd, HashMap(propertyMap))

			if (!saved) {
				throw IllegalStateException("TagLib failed to save metadata for uri=$uri")
			}
		} ?: throw IllegalStateException("Could not open file descriptor for uri=$uri")
	}

	suspend fun editMetadata(
		uri: Uri,
		metadata: Metadata,
		wasArtworkChanged: Boolean
	): EditResult {
		if (DocumentsContract.isDocumentUri(context, uri)) {
			val downloadLocation = dataStoreManager.downloadLocation.first()
			if (!folderManager.hasValidPerms(downloadLocation)) {
				return EditResult.NeedsFolderReselect(
					"Access to the download location was lost, please re-select the folder."
				)
			}
		}

		return try {
			performEdit(uri, metadata, wasArtworkChanged)
			EditResult.Success
		} catch (e: RecoverableSecurityException) {
			val intentSender = e.userAction.actionIntent.intentSender

			Log.w(LOG_TAG, "No permission for intentSender=$intentSender", e)
			EditResult.NeedsPermission(intentSender)
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Error editing metadata", e)
			EditResult.Error(e)
		}
	}

	fun checkWritePermission(uri: Uri): PermissionCheckResult {
		return try {
			context.contentResolver.openFileDescriptor(uri, "rw")?.use { }
			PermissionCheckResult.Granted
		} catch (e: RecoverableSecurityException) {
			PermissionCheckResult.NeedsPermission(e.userAction.actionIntent.intentSender)
		}
	}
}