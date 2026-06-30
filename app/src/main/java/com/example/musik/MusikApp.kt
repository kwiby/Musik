package com.example.musik

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.musik.ui.components.MainContainer
import com.example.musik.ui.components.MusikTopAppBar
import com.example.musik.ui.screens.player.PlayerScreen
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.PlaybackViewModel
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
			Scaffold(
				containerColor = MaterialTheme.colorScheme.background,
				topBar = { MusikTopAppBar() }
			) { innerPadding ->
				MainContainer(
					sharedTransitionScope = this@SharedTransitionLayout,
					navViewModel = navViewModel,
					playbackViewModel = playbackViewModel,
					modifier = Modifier.padding(innerPadding)
				)
			}

			AnimatedVisibility(
				visible = playbackViewModel.isPlayerScreenOpen.value,
				enter = fadeIn(), //EnterTransition.None,
				exit = fadeOut() //ExitTransition.None
			) {
				PlayerScreen(
					sharedTransitionScope = this@SharedTransitionLayout,
					playbackViewModel = playbackViewModel
				)
			}
		}
	}
}