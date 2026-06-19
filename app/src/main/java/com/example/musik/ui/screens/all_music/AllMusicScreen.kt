package com.example.musik.ui.screens.all_music

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
import com.example.musik.R
import com.example.musik.ui.screens.all_music.screens.add_music.AddMusicScreen
import com.example.musik.ui.screens.all_music.screens.music_list.MusicListScreen
import com.example.musik.ui.view_models.AddMusicViewModel
import com.example.musik.ui.view_models.ViewModelProvider

@Composable
fun AllMusicScreen(
	viewModel: AddMusicViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	var isAddingMusic by remember { mutableStateOf(false) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		if (isAddingMusic) {
			viewModel.musicAddingSetup()

			AddMusicScreen(viewModel) { isAddingMusic = false }
		} else {
			MusicListScreen { isAddingMusic = true }
		}
	}
}