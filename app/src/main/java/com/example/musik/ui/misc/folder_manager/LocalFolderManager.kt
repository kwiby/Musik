package com.example.musik.ui.misc.folder_manager

import androidx.compose.runtime.staticCompositionLocalOf

val LocalFolderManager = staticCompositionLocalOf<FolderManager> {
	error("FolderManager not provided -> wrap content in CompositionLocalProvider(LocalFolderManager provides ...)")
}