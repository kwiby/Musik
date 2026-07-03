package com.example.musik.ui.tabs.all_music.tabs.add_yt_music.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun FolderPicker() {
	val context = LocalContext.current
	var folderUri by remember { mutableStateOf<Uri?>(null) }

	val folderPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocumentTree()
	) { uri: Uri? ->
		if (uri != null) {
			context.contentResolver.takePersistableUriPermission(
				uri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
			)
			folderUri = uri
		}
	}
}

fun writeFileToFolder(
	context: Context,
	folderUri: Uri,
	fileName: String,
	mimeType: String,
	content: ByteArray
) {
	val treeDocId = DocumentsContract.getTreeDocumentId(folderUri)
	val parentUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, treeDocId)

	val newFileUri = DocumentsContract.createDocument(
		context.contentResolver, parentUri, mimeType, fileName
	) ?: return

	context.contentResolver.openOutputStream(newFileUri)?.use { it.write(content) }
}