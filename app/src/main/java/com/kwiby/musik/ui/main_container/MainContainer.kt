package com.kwiby.musik.ui.main_container

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.kwiby.musik.R
import com.kwiby.musik.data.misc.rememberPermissionHandler
import com.kwiby.musik.ui.components.PlayerBar
import com.kwiby.musik.ui.main_container.components.TabButton
import com.kwiby.musik.ui.main_container.components.info.NoPermsMsg
import com.kwiby.musik.ui.tabs.all_music.AllMusicTab
import com.kwiby.musik.ui.tabs.playlists.PlaylistsTab
import com.kwiby.musik.ui.tabs.stats.StatsTab
import com.kwiby.musik.ui.view_models.MusicListViewModel
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.Tab
import com.kwiby.musik.ui.view_models.ViewModelProvider
import com.kwiby.musik.ui.view_models.toMediaItem

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContainer(
	sharedTransitionScope: SharedTransitionScope,
	navViewModel: NavViewModel,
	playbackViewModel: PlaybackViewModel,
	modifier: Modifier = Modifier,
	musicListViewModel: MusicListViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	val isInMoveMode by musicListViewModel.isInMoveMode.collectAsStateWithLifecycle()
	val queueSyncEvent by musicListViewModel.queueSyncEvent.collectAsStateWithLifecycle()
	LaunchedEffect(queueSyncEvent) {
		if (!isInMoveMode) {
			queueSyncEvent?.let { q ->
				playbackViewModel.setQueue(q.map { it.toMediaItem() })
			}
		}
	}
	LaunchedEffect(musicListViewModel, playbackViewModel) {
		playbackViewModel.onDeadTrackDetected = { ids ->
			musicListViewModel.deleteTracksByIds(ids)
		}
	}

	val permissionStatus = rememberPermissionHandler()

	Box(
		modifier = modifier.fillMaxSize()
	) {
		Column {
			Spacer(Modifier.height(dimensionResource(R.dimen.main_container_top_spacing)))

			Surface(
				shape = RoundedCornerShape(
					topStart = dimensionResource(R.dimen.main_container_top_corners_radius),
					topEnd = dimensionResource(R.dimen.main_container_top_corners_radius)
				),
				color = MaterialTheme.colorScheme.secondary,
				shadowElevation = dimensionResource(R.dimen.main_container_shadows),
				modifier = Modifier.fillMaxSize()
			) {
				if (permissionStatus.status.isGranted) {
					// --===--  Main Screens  --===--
					when (navViewModel.curTab) {
						Tab.ALL_MUSIC -> AllMusicTab(
							playbackViewModel,
							musicListViewModel,
							navViewModel
						)
						Tab.PLAYLISTS -> PlaylistsTab()
						Tab.STATS -> StatsTab()
					}
				} else {
					// --===--  No Permissions Msg  --===--
					NoPermsMsg()
				}
			}
		}

		// ---===--  Tabs  --===--
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.Bottom, // To align the bottom of the tabs together
			modifier = Modifier
				.align(Alignment.TopCenter) // To actually position the tabs at the top
				.padding(dimensionResource(R.dimen.small_padding))
		) {
			TabButton(
				stringResource(R.string.all_music),
				navViewModel.curTab == Tab.ALL_MUSIC
			) {
				navViewModel.navToTab(Tab.ALL_MUSIC)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(
				stringResource(R.string.playlists),
				navViewModel.curTab == Tab.PLAYLISTS
			) {
				navViewModel.navToTab(Tab.PLAYLISTS)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.stats), navViewModel.curTab == Tab.STATS) {
				navViewModel.navToTab(Tab.STATS)
			}
		}

		PlayerBar(
			sharedTransitionScope = sharedTransitionScope,
			playbackViewModel = playbackViewModel,
			navViewModel = navViewModel
		)
	}
}