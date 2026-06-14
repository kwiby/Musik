package com.example.musik.ui.components

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
import com.example.musik.data.MusikViewModel
import com.example.musik.ui.screens.all_music.AllMusicScreen
import com.example.musik.ui.screens.all_music.screens.add_songs.AddSongsScreen
import com.example.musik.ui.screens.playlists.PlaylistsScreen
import com.example.musik.ui.screens.stats.StatsScreen

@Composable
fun MainContainer(
	viewModel: MusikViewModel,
	modifier: Modifier = Modifier
) {
	val allMusicScreenInt = viewModel.allMusicScreenInt
	val playlistsScreenInt = viewModel.playlistsScreenInt
	val statsScreenInt = viewModel.statsScreenInt
	val addSongsScreenInt = viewModel.addSongsScreenInt

	val currentScreen = viewModel.currentScreen

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
				when (currentScreen) {
					allMusicScreenInt -> AllMusicScreen()//onNavToAddSongsScreen = { viewModel.navTo(addSongsScreenInt) })
					playlistsScreenInt -> PlaylistsScreen()
					statsScreenInt -> StatsScreen()
					addSongsScreenInt -> AddSongsScreen()//onBack = { viewModel.navTo(allMusicScreenInt) })
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
			TabButton(stringResource(R.string.all_music_tab), currentScreen == allMusicScreenInt) { viewModel.navTo(allMusicScreenInt) }
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.playlists_tab), currentScreen == playlistsScreenInt) { viewModel.navTo(playlistsScreenInt) }
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.tabs_spacing)))
			TabButton(stringResource(R.string.stats_tab), currentScreen == statsScreenInt) { viewModel.navTo(statsScreenInt) }
		}
	}
}