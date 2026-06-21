package com.example.musik.ui.screens.all_music.screens.music_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.components.MusicListItem
import com.example.musik.ui.screens.all_music.components.ListDivider
import com.example.musik.ui.screens.all_music.components.LoadingIndicator
import com.example.musik.ui.view_models.MusicListViewModel
import kotlinx.coroutines.launch

@Composable
fun MusicListScreen(
	viewModel: MusicListViewModel,
	onAddMusic: () -> Unit,
) {
	val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
	val isInSelectionMode by viewModel.isInSelectionMode.collectAsStateWithLifecycle()

	val scope = rememberCoroutineScope()

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

				// ---===---  Move Music Button  ---===---
				CustomIconButton(
					{
						viewModel.moveMusicButton()
					},
					Icons.Rounded.UnfoldMore,
					stringResource(R.string.move_music_button)
				)

				// ---===---  Remove Music Button  ---===---
				if (isInSelectionMode) {
					CustomIconButton(
						{
							scope.launch {
								viewModel.removeMusicButton()
							}
						},
						Icons.Rounded.DeleteOutline,
						stringResource(R.string.remove_music_button)
					)
				}
			}

			// ---===---  Adding Buttons  ---===---
			Row {
				// ---===---  Add Music Button  ---===---
				CustomIconButton(
					{
						viewModel.addingButton()
						onAddMusic()
					},
					Icons.Rounded.Add,
					stringResource(R.string.add_music_button)
				)

				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		when(val state = viewModel.uiState.collectAsStateWithLifecycle().value) {
			is MusicListViewModel.MusicUiState.Loading -> {
				LoadingIndicator()
			}
			is MusicListViewModel.MusicUiState.Empty -> {
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
			is MusicListViewModel.MusicUiState.Success -> {
				// ---===---  Music List  ---===---
				LazyColumn(
					contentPadding = PaddingValues(
						bottom = dimensionResource(R.dimen.x_large_padding)
					),
					modifier = Modifier.fillMaxSize()
				) {
					items(
						count = state.musicList.size,
						key = { state.musicList[it].id }
					) { index ->
						val music = state.musicList[index]
						MusicListItem(
							musicDetails = music,
							isSelected = music.id in selectedIds,
							onClick = { viewModel.handleTap(music.id) },
							onLongClick = { viewModel.handleHold(music.id) }
						)

						ListDivider(index, state.musicList)
					}
				}
			}
		}
	}
}