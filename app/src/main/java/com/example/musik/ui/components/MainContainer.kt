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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.musik.data.MusikViewModel
import com.example.musik.data.openPermissionsSettings
import com.example.musik.data.rememberPermissionHandler
import com.example.musik.ui.screens.all_music.AllMusicScreen
import com.example.musik.ui.screens.all_music.screens.add_songs.AddSongsScreen
import com.example.musik.ui.screens.playlists.PlaylistsScreen
import com.example.musik.ui.screens.stats.StatsScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContainer(
	viewModel: MusikViewModel,
	modifier: Modifier = Modifier
) {
	val allMusicScreenInt = viewModel.allMusicScreenInt
	val playlistsScreenInt = viewModel.playlistsScreenInt
	val statsScreenInt = viewModel.statsScreenInt
	val addSongsScreenInt = viewModel.addSongsScreenInt

	val curScreen = viewModel.curScreen

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
					when (curScreen) {
						allMusicScreenInt -> AllMusicScreen(onNavToAddSongsScreen = { viewModel.navTo(addSongsScreenInt) })
						playlistsScreenInt -> PlaylistsScreen()
						statsScreenInt -> StatsScreen()
						addSongsScreenInt -> AddSongsScreen()//onBack = { viewModel.navTo(allMusicScreenInt) })
					}
				} else {
					// ---===---  No Permissions Screen  ---===---
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
						IconButton(
							onClick = { openPermissionsSettings(context) }
						) {
							Icon(Icons.Default.Settings, contentDescription = "Settings")
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
			TabButton(stringResource(R.string.all_music_tab), curScreen == allMusicScreenInt) { viewModel.navTo(allMusicScreenInt) }
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.playlists_tab), curScreen == playlistsScreenInt) { viewModel.navTo(playlistsScreenInt) }
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.stats_tab), curScreen == statsScreenInt) { viewModel.navTo(statsScreenInt) }
		}
	}
}