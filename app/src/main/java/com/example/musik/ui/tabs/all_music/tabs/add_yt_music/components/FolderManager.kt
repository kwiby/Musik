package com.example.musik.ui.tabs.all_music.tabs.add_yt_music.components

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.example.musik.data.datastore.DataStoreManager
import kotlinx.coroutines.launch

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
				val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
				activity.contentResolver.takePersistableUriPermission(directoryUri, flags)

				activity.lifecycleScope.launch {
					dataStoreManager.setDownloadLocation(directoryUri.toString())
				}
				onFolderSelected(directoryUri)
			}
		}

	fun openFolderSelector() {
		launcher.launch(null)
	}

	fun validatePersistedUri(uriString: String?): Uri? {
		if (uriString.isNullOrEmpty()) {
			return null
		}

		val uri = uriString.toUri()
		val stillValid = activity.contentResolver.persistedUriPermissions.any {
			it.uri == uri && it.isReadPermission && it.isWritePermission
		}

		return if (stillValid) {
			uri
		} else {
			null
		}
	}

	fun releaseFolder(uri: Uri) {
		val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

		activity.contentResolver.releasePersistableUriPermission(uri, flags)
		activity.lifecycleScope.launch {
			dataStoreManager.setDownloadLocation("")
		}
	}

	fun listFiles(uri: Uri): List<DocumentFile> {
		return DocumentFile.fromTreeUri(activity, uri)?.listFiles()?.toList() ?: emptyList()
	}
}