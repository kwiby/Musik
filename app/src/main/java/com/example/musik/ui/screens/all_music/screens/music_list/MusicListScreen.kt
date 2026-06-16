package com.example.musik.ui.screens.all_music.screens.music_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton

@Composable
fun MusicListScreen(
	onAddMusicButtonClick: () -> Unit
) {
	// ---===---  All Buttons  ---===---
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.fillMaxWidth()
	) {
		// ---===---  Editing Buttons  ---===---
		Row {
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			CustomIconButton(
				{  },
				Icons.Rounded.UnfoldMore,
				stringResource(R.string.move_songs_button)
			)
		}

		// ---===---  Adding Buttons  ---===---
		Row {
			CustomIconButton(
				{ onAddMusicButtonClick() },
				Icons.Rounded.Add,
				stringResource(R.string.add_songs_button)
			)
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
		}
	}

	Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

	// ---===---  Music List  ---===---
	LazyColumn {

	}
}