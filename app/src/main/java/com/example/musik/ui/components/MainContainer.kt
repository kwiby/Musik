package com.example.musik.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.data.misc.rememberPermissionHandler
import com.example.musik.ui.components.info.NoPermsMsg
import com.example.musik.ui.tabs.all_music.AllMusicTab
import com.example.musik.ui.tabs.playlists.PlaylistsTab
import com.example.musik.ui.tabs.stats.StatsTab
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.PlaybackViewModel
import com.example.musik.ui.view_models.Tabs
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContainer(
	sharedTransitionScope: SharedTransitionScope,
	navViewModel: NavViewModel,
	playbackViewModel: PlaybackViewModel,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier.fillMaxSize()
	) {
		Column {
			Spacer(modifier = Modifier.height(dimensionResource(R.dimen.main_container_top_spacing)))

			Surface(
				shape = RoundedCornerShape(
					topStart = dimensionResource(R.dimen.main_container_top_corners_radius),
					topEnd = dimensionResource(R.dimen.main_container_top_corners_radius)
				),
				color = MaterialTheme.colorScheme.secondary,
				shadowElevation = dimensionResource(R.dimen.main_container_shadows),
				modifier = Modifier.fillMaxSize()
			) {
				val permissionStatus = rememberPermissionHandler()
				if (permissionStatus.status.isGranted) {
					// ---===---  Main Screens  ---===---
					when (navViewModel.curTab) {
						Tabs.ALL_MUSIC -> AllMusicTab(playbackViewModel = playbackViewModel)
						Tabs.PLAYLISTS -> PlaylistsTab()
						Tabs.STATS -> StatsTab()
					}
				} else {
					// ---===---  No Permissions Msg  ---===---
					NoPermsMsg()
				}
			}
		}

		// ---===---  Tabs  ---===---
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.Bottom, // To align the bottom of the tabs together
			modifier = Modifier
				.align(Alignment.TopCenter) // To actually position the tabs at the top
				.padding(dimensionResource(R.dimen.small_padding))
		) {
			TabButton(stringResource(R.string.all_music_tab), navViewModel.curTab == Tabs.ALL_MUSIC) {
				navViewModel.navTo(Tabs.ALL_MUSIC)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.playlists_tab),navViewModel.curTab == Tabs.PLAYLISTS) {
				navViewModel.navTo(Tabs.PLAYLISTS)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.stats_tab), navViewModel.curTab == Tabs.STATS) {
				navViewModel.navTo(Tabs.STATS)
			}
		}

		PlayerBar(
			sharedTransitionScope = sharedTransitionScope,
			playbackViewModel = playbackViewModel
		)
	}
}