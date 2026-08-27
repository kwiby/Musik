package com.kwiby.musik

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.crash_handling.CrashScreen
import com.kwiby.musik.ui.MusikApplication
import com.kwiby.musik.ui.floating_ui.dialogs.update.UpdateDialogController
import com.kwiby.musik.ui.misc.folder_manager.FolderManager
import com.kwiby.musik.ui.misc.folder_manager.LocalFolderManager
import com.kwiby.musik.ui.theme.AppTheme
import com.kwiby.musik.ui.theme.MusikTheme
import com.kwiby.musik.ui.theme.ThemeMode
import com.kwiby.musik.ui.theme.ThemeStyle
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.UpdateViewModel
import com.kwiby.musik.ui.view_models.ViewModelProvider

class MainActivity : ComponentActivity() {
	private val navViewModel: NavViewModel by viewModels { ViewModelProvider.Factory }
	private val updateViewModel: UpdateViewModel by viewModels { ViewModelProvider.Factory }

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0xFF080808.toInt()))

		updateViewModel.cleanupOldApks(applicationContext)
		updateViewModel.checkForUpdates(isManual = false)

		val dataStoreManager = (application as MusikApplication).container.dataStoreManager
		val folderManager = FolderManager(
			activity = this,
			dataStoreManager = dataStoreManager
		)

		val crashLog = intent.getStringExtra("crash_log")

		splashScreen.setKeepOnScreenCondition {
			!navViewModel.isReady.value
		}

		setContent {
			val dataStoreThemeMode by dataStoreManager.themeMode.collectAsStateWithLifecycle(
				ThemeMode.DEFAULT.name
			)
			val dataStoreThemeStyle by dataStoreManager.themeStyle.collectAsStateWithLifecycle(
				ThemeStyle.DEFAULT.name
			)
			val appTheme = AppTheme(
				mode = ThemeMode.fromString(dataStoreThemeMode),
				style = ThemeStyle.fromString(dataStoreThemeStyle)
			)

			MusikTheme(
				appTheme = appTheme
			) {
				if (crashLog != null) {
					CrashScreen(
						crashLog = crashLog,
						onRestart = {
							val restartIntent = Intent(this, MainActivity::class.java).apply {
								flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
							}
							startActivity(restartIntent)
							finish()
						}
					)
				} else {
					CompositionLocalProvider(LocalFolderManager provides folderManager) {
						Box {
							MusikApp(
								updateViewModel = updateViewModel,
								navViewModel = navViewModel
							)

							UpdateDialogController(updateViewModel)
						}
					}
				}
			}
		}
	}
}