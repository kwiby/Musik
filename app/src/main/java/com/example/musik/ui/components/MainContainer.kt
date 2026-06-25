package com.example.musik.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.data.misc.openPermissionsSettings
import com.example.musik.data.misc.rememberPermissionHandler
import com.example.musik.ui.screens.all_music.AllMusicScreen
import com.example.musik.ui.screens.playlists.PlaylistsScreen
import com.example.musik.ui.screens.stats.StatsScreen
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContainer(
	navViewModel: NavViewModel,
	modifier: Modifier = Modifier
) {
	Box(modifier = modifier.fillMaxSize()) {
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
					when (navViewModel.curScreen) {
						Screen.ALL_MUSIC -> AllMusicScreen()
						Screen.PLAYLISTS -> PlaylistsScreen()
						Screen.STATS -> StatsScreen()
					}
				} else {
					// ---===---  No Permissions Msg  ---===---
					Column(
						verticalArrangement = Arrangement.Top,
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier.offset(y = dimensionResource(R.dimen.main_container_no_permissions_offset))
					) {
						Text(
							text = stringResource(R.string.no_permissions_msg),
							style = MaterialTheme.typography.titleSmall,
							color = MaterialTheme.colorScheme.onSecondary
						)

						val context = LocalContext.current
						CustomIconButton(
							Icons.Rounded.Settings,
							stringResource(R.string.settings_button)
						) {
							openPermissionsSettings(context)
						}
					}
				}
			}
		}

		// ---===---  TABS  ---===---
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.Bottom,
			modifier = Modifier
				.align(Alignment.TopCenter)
				.padding(dimensionResource(R.dimen.small_padding))
		) {
			TabButton(stringResource(R.string.all_music_tab), navViewModel.curScreen == Screen.ALL_MUSIC) {
				navViewModel.navTo(Screen.ALL_MUSIC)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.playlists_tab),navViewModel.curScreen == Screen.PLAYLISTS) {
				navViewModel.navTo(Screen.PLAYLISTS)
			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.stats_tab), navViewModel.curScreen == Screen.STATS) {
				navViewModel.navTo(Screen.STATS)
			}
		}
	}
}