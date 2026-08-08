package com.kwiby.musik

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.kwiby.musik.data.misc.RequestPermissions
import com.kwiby.musik.data.misc.rememberPermissionHandler
import com.kwiby.musik.ui.components.MusikTopAppBar
import com.kwiby.musik.ui.main_container.MainContainer
import com.kwiby.musik.ui.screens.edit_metadata.EditMetadataScreen
import com.kwiby.musik.ui.screens.player.PlayerScreen
import com.kwiby.musik.ui.screens.settings.SettingsScreen
import com.kwiby.musik.ui.view_models.EditMetadataViewModel
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.Screen
import com.kwiby.musik.ui.view_models.SettingsViewModel
import com.kwiby.musik.ui.view_models.ViewModelProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusikApp(
	navViewModel: NavViewModel = viewModel(factory = ViewModelProvider.Factory),
	settingsViewModel: SettingsViewModel = viewModel(factory = ViewModelProvider.Factory),
	playbackViewModel: PlaybackViewModel = viewModel(factory = ViewModelProvider.Factory),
	editMetadataViewModel: EditMetadataViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	val permissionStatus = rememberPermissionHandler()
	if (!permissionStatus.status.isGranted) {
		RequestPermissions(permissionStatus)
	}

	SharedTransitionLayout {
		Box(modifier = Modifier.fillMaxSize()) {
			// --===--  Main  --===--
			Scaffold(
				containerColor = MaterialTheme.colorScheme.background,
				topBar = { MusikTopAppBar(navViewModel) }
			) { innerPadding ->
				MainContainer(
					sharedTransitionScope = this@SharedTransitionLayout,
					navViewModel = navViewModel,
					playbackViewModel = playbackViewModel,
					modifier = Modifier.padding(innerPadding)
				)
			}

			// --===--  All Screens  --===--
			AnimatedContent(
				targetState = navViewModel.curScreen,
				transitionSpec = {
					if (initialState == Screen.Player || targetState == Screen.Player) {
						fadeIn() togetherWith fadeOut()
					} else {
						EnterTransition.None togetherWith ExitTransition.None
					}
				},
				label = "screen_transition"
			) { curScreen ->
				when (curScreen) {
					Screen.Main -> {
						// Required for proper animation
						Box(Modifier.fillMaxSize())
					}
					Screen.Settings -> SettingsScreen(
						settingsViewModel = settingsViewModel,
						navViewModel = navViewModel
					)
					Screen.Player -> PlayerScreen(
						sharedTransitionScope = this@SharedTransitionLayout,
						playbackViewModel = playbackViewModel,
						navViewModel = navViewModel
					)
					is Screen.EditMetadata -> EditMetadataScreen(
						contentUri = curScreen.contentUri,
						id = curScreen.id,
						editMetadataViewModel = editMetadataViewModel,
						navViewModel = navViewModel,
						playbackViewModel = playbackViewModel
					)
				}
			}
		}
	}
}