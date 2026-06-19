package com.example.musik.ui.screens.all_music.screens.music_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.view_models.MusicEntryViewModel
import com.example.musik.ui.view_models.ViewModelProvider

@Composable
fun MusicListScreen(
	viewModel: MusicEntryViewModel = viewModel(factory = ViewModelProvider.Factory),
	onAddMusic: () -> Unit,
) {
	val dbCount by viewModel.audioFileCount.collectAsStateWithLifecycle()

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
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
					{ onAddMusic() },
					Icons.Rounded.Add,
					stringResource(R.string.add_songs_button)
				)
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		if (dbCount != 0) {
			// ---===---  Music List  ---===---
			LazyColumn {

			}
		} else {
			// ---===---  No Music Msg  ---===---
			Column(
				verticalArrangement = Arrangement.Top,
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier.offset(y = dimensionResource(R.dimen.no_music_added_offset))
			) {
				Text(
					text = stringResource(R.string.no_music_msg),
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSecondary
				)
			}
		}
	}
}