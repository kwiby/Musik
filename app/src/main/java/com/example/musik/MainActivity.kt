package com.example.musik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.musik.ui.MusikApplication
import com.example.musik.ui.misc.FolderManager
import com.example.musik.ui.misc.LocalFolderManager
import com.example.musik.ui.theme.MusikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0xFF080808.toInt()))

		val dataStoreManager = (application as MusikApplication).container.dataStoreManager
		val folderManager = FolderManager(
			activity = this,
			dataStoreManager = dataStoreManager
		)

		setContent {
			MusikTheme {
				CompositionLocalProvider(LocalFolderManager provides folderManager) {
					MusikApp()
				}
			}
		}
	}
}