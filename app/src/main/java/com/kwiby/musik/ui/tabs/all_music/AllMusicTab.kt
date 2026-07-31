package com.kwiby.musik.ui.tabs.all_music

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwiby.musik.R
import com.kwiby.musik.ui.tabs.all_music.pages.add_music.AddMusicPage
import com.kwiby.musik.ui.tabs.all_music.pages.add_yt_music.AddYtMusicPage
import com.kwiby.musik.ui.tabs.all_music.pages.music_list.MusicListScreen
import com.kwiby.musik.ui.view_models.AddMusicViewModel
import com.kwiby.musik.ui.view_models.AddYtMusicViewModel
import com.kwiby.musik.ui.view_models.MusicListViewModel
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.ViewModelProvider

@Composable
fun AllMusicTab(
	playbackViewModel: PlaybackViewModel,
	musicListViewModel: MusicListViewModel,
	navViewModel: NavViewModel,
	addMusicViewModel: AddMusicViewModel = viewModel(factory = ViewModelProvider.Factory),
	addYtMusicViewModel: AddYtMusicViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	var isAddingMusic by remember { mutableStateOf(false) }
	var isAddingYtMusic by remember { mutableStateOf(false) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		if (isAddingMusic) {
			addMusicViewModel.resetMusicAdding()
			AddMusicPage(
				addMusicViewModel
			) { isAddingMusic = false }
		} else if (isAddingYtMusic) {
			AddYtMusicPage(
				addYtMusicViewModel
			) { isAddingYtMusic = false }
		} else {
			musicListViewModel.resetMusicList()
			MusicListScreen(
				musicListViewModel,
				playbackViewModel,
				navViewModel,
				{ isAddingMusic = true },
				{ isAddingYtMusic = true }
			)
		}
	}
}