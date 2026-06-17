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
import com.example.musik.R
import com.example.musik.ui.screens.all_music.screens.add_music.AddMusicScreen
import com.example.musik.ui.screens.all_music.screens.music_list.MusicListScreen

@Composable
fun AllMusicScreen() {
	var isAddingMusic by remember { mutableStateOf(false) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		if (isAddingMusic) {
			AddMusicScreen() { isAddingMusic = false }
		} else {
			MusicListScreen() { isAddingMusic = true }
		}
	}
}