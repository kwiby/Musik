package com.example.musik.ui.misc

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.musik.data.datastore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderManager(
	private val activity: ComponentActivity,
	private val dataStoreManager: DataStoreManager,
	private val onFolderSelected: (Uri) -> Unit = {}
) {
	private val launcher: ActivityResultLauncher<Uri?> =
		activity.registerForActivityResult(
			ActivityResultContracts.OpenDocumentTree()
		) { directoryUri ->
			if (directoryUri != null) {
				activity.lifecycleScope.launch {
					val previousUriString = dataStoreManager.downloadLocation.first()

					if (previousUriString.isNotEmpty()) {
						val previousUri = previousUriString.toUri()

						if (previousUri != directoryUri) {
							releaseFolder(previousUri)
						}
					}

					val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
					activity.contentResolver.takePersistableUriPermission(directoryUri, flags)

					activity.lifecycleScope.launch {
						dataStoreManager.setDownloadLocation(directoryUri.toString())
					}
					onFolderSelected(directoryUri)
				}
			}
		}


	suspend fun resetDownloadLocation() {
		dataStoreManager.setDownloadLocation("")
	}

	suspend fun hasValidPerms(uriString: String?): Boolean {
		if (uriString.isNullOrEmpty()) {
			return false
		}

		val uri = uriString.toUri()

		return withContext(Dispatchers.IO) {
			activity.contentResolver.persistedUriPermissions.any {
				it.uri == uri && it.isReadPermission && it.isWritePermission
			}
		}
	}

	fun getDisplayPath(uri: Uri): String {
		val docId = DocumentsContract.getTreeDocumentId(uri)

		return "./" + docId.substringAfter(':', docId)
	}

	fun openFolderSelector() {
		launcher.launch(null)
	}

	fun releaseFolder(uri: Uri) {
		val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

		activity.contentResolver.releasePersistableUriPermission(uri, flags)
		activity.lifecycleScope.launch {
			dataStoreManager.setDownloadLocation("")
		}
	}

	/*
	fun listFiles(uri: Uri): List<DocumentFile> {
		return DocumentFile.fromTreeUri(activity, uri)?.listFiles()?.toList() ?: emptyList()
	}
	 */
}