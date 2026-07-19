package com.example.musik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.musik.ui.MusikApplication
import com.example.musik.ui.misc.FolderManager
import com.example.musik.ui.misc.LocalFolderManager
import com.example.musik.ui.theme.AppTheme
import com.example.musik.ui.theme.MusikTheme
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.ViewModelProvider

class MainActivity : ComponentActivity() {
	private val navViewModel: NavViewModel by viewModels { ViewModelProvider.Factory }

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0xFF080808.toInt()))

		val dataStoreManager = (application as MusikApplication).container.dataStoreManager
		val folderManager = FolderManager(
			activity = this,
			dataStoreManager = dataStoreManager
		)

		splashScreen.setKeepOnScreenCondition {
			!navViewModel.isReady.value
		}

		setContent {
			MusikTheme(
				appTheme = AppTheme.NIGHT
			) {
				CompositionLocalProvider(LocalFolderManager provides folderManager) {
					MusikApp(navViewModel = navViewModel)
				}
			}
		}
	}
}