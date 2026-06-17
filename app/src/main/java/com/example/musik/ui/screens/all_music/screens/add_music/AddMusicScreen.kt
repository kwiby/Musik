package com.example.musik.ui.screens.all_music.screens.add_music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.misc.AudioFileListItem
import com.example.musik.ui.screens.all_music.components.LoadingIndicator
import com.example.musik.ui.view_models.AddMusicViewModel

@Composable
fun AddMusicScreen(
	viewModel: AddMusicViewModel,
	onBackToMusicListButtonClick: () -> Unit
) {
	val audioFiles by viewModel.audioFiles.collectAsState()
	val isLoading by viewModel.isLoading.collectAsState()

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// ---===---  All Buttons  ---===---
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier.fillMaxWidth()
		) {
			// ---===---  Back Button  ---===---
			Row {
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
				CustomIconButton(
					{ onBackToMusicListButtonClick() },
					Icons.AutoMirrored.Rounded.ArrowBack,
					stringResource(R.string.back_button)
				)
			}

			// ---===---  Selection Buttons  ---===---
			Row {
				// ---===---  Refresh Button  ---===---
				CustomIconButton(
					{ viewModel.loadAudioFiles() },
					Icons.Rounded.Refresh,
					stringResource(R.string.refresh_button)
				)
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.zero)))

				// ---===---  Add Selected Music Button  ---===---
				CustomIconButton(
					{  },
					Icons.Outlined.AddBox,
					stringResource(R.string.add_selected_music_button)
				)
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		// ---===---  Music List  ---===---
		when {
			isLoading -> {
				LoadingIndicator()
			} audioFiles.isEmpty() -> {
				// ---===---  No Audio Files Msg  ---===---
				Column(
					verticalArrangement = Arrangement.Top,
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = Modifier.offset(y = dimensionResource(R.dimen.no_music_added_offset))
				) {
					Text(
						text = stringResource(R.string.no_audio_files_msg),
						style = MaterialTheme.typography.titleSmall,
						color = MaterialTheme.colorScheme.onSecondary
					)
				}
			} else -> {
			// ---===---  Audio File List  ---===---
				LazyColumn(
					contentPadding = PaddingValues(
						bottom = dimensionResource(R.dimen.x_large_padding)
					),
					modifier = Modifier.fillMaxSize()
				) {
					items(audioFiles.size, key = { audioFiles[it].id }) { index ->
						AudioFileListItem(audioFiles[index])

						if (index < audioFiles.size - 1) {
							Box(modifier = Modifier.fillMaxWidth()) {
								HorizontalDivider(
									thickness = dimensionResource(R.dimen.horizontal_divider_thickness),
									modifier = Modifier
										.fillMaxWidth(0.8f)
										.align(Alignment.CenterEnd)
										.padding(horizontal = dimensionResource(R.dimen.horizontal_divider_padding))
								)
							}
						}
					}
				}
			}
		}
	}
}