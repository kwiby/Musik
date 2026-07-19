package com.example.musik

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
import com.example.musik.data.misc.RequestPermissions
import com.example.musik.data.misc.rememberPermissionHandler
import com.example.musik.ui.components.MusikTopAppBar
import com.example.musik.ui.main_container.MainContainer
import com.example.musik.ui.screens.player.PlayerScreen
import com.example.musik.ui.screens.settings.SettingsScreen
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.PlaybackViewModel
import com.example.musik.ui.view_models.Screen
import com.example.musik.ui.view_models.ViewModelProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusikApp(
	navViewModel: NavViewModel = viewModel(),
	playbackViewModel: PlaybackViewModel = viewModel(factory = ViewModelProvider.Factory)
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
					if (navViewModel.curScreen == Screen.PLAYER) {
						fadeIn() togetherWith fadeOut()
					} else {
						EnterTransition.None togetherWith ExitTransition.None
					}
				},
				label = "screen_transition"
			) { curScreen ->
				when (curScreen) {
					Screen.MAIN -> {
						// Required for proper animation
						Box(Modifier.fillMaxSize())
					}
					Screen.SETTINGS -> SettingsScreen(
						navViewModel = navViewModel
					)
					Screen.PLAYER -> PlayerScreen(
						sharedTransitionScope = this@SharedTransitionLayout,
						playbackViewModel = playbackViewModel,
						navViewModel = navViewModel
					)
				}
			}
		}
	}
}