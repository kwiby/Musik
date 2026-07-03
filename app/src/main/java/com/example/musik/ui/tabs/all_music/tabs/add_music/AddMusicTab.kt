package com.example.musik.ui.tabs.all_music.tabs.add_music

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.musik.ui.components.info.NoAudioFilesMsg
import com.example.musik.ui.components.verticalScrollbar
import com.example.musik.ui.tabs.all_music.components.ListDivider
import com.example.musik.ui.tabs.all_music.components.LoadingIndicator
import com.example.musik.ui.tabs.all_music.tabs.add_music.components.AddMusicSearchbar
import com.example.musik.ui.view_models.AddMusicViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMusicScreen(
	addMusicViewModel: AddMusicViewModel,
	onBackToMusicList: () -> Unit
) {
	val isLoading by addMusicViewModel.isLoading.collectAsStateWithLifecycle()
	val audioFiles by addMusicViewModel.audioFiles.collectAsStateWithLifecycle()
	val selectedIds by addMusicViewModel.selectedIds.collectAsStateWithLifecycle()

	val lazyListState = rememberLazyListState()
	val scope = rememberCoroutineScope()

	DisposableEffect(Unit) {
		onDispose {
			addMusicViewModel.resetMusicAdding()
		}
	}
	BackHandler(true) { // Always enabled in this screen
		onBackToMusicList()
	}

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// ---===---  All Buttons  ---===---
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			// ---===---  Back Button  ---===---
			Row {
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
				CustomIconButton(
					iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
					contentDescription = stringResource(R.string.back_button)
				) {
					onBackToMusicList()
				}
			}

			// ---===---  Search Bar  ---===---
			AddMusicSearchbar(addMusicViewModel, Modifier.weight(1f))

			// ---===---  Selection Buttons  ---===---
			Row {
				// ---===---  Refresh Button  ---===---
				CustomIconButton(
					iconImageVector = Icons.Rounded.Refresh,
					contentDescription = stringResource(R.string.refresh_button)
				) {
					addMusicViewModel.refreshButton()
				}

				// ---===---  Add Selected Music Button  ---===---
				CustomIconButton(
					iconImageVector = Icons.Outlined.AddBox,
					contentDescription = stringResource(R.string.add_selected_music_button)
				) {
					scope.launch {
						addMusicViewModel.addSelectedMusic()
						onBackToMusicList()
					}
				}

			}
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
		}

		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		// ---===---  Music List  ---===---
		when {
			isLoading -> {
				LoadingIndicator()
			} audioFiles.isEmpty() -> {
				// ---===---  No Audio Files Msg  ---===---
				NoAudioFilesMsg()
			} else -> {
			// ---===---  Audio File List  ---===---
				LazyColumn(
					state = lazyListState,
					contentPadding = PaddingValues(
						bottom = dimensionResource(R.dimen.x_large_padding)
					),
					modifier = Modifier.fillMaxSize().verticalScrollbar(lazyListState)
				) {
					items(
						count = audioFiles.size,
						key = { audioFiles[it].id })
					{ index ->
						val music = audioFiles[index]
						MusicListItem(
							musicDetails = music,
							isSelected = music.id in selectedIds,
							onClick = { addMusicViewModel.toggleSelection(music.id) },
							onLongClick = { addMusicViewModel.toggleSelection(music.id) }
						)

						ListDivider(index, audioFiles)
					}
				}
			}
		}
	}
}