package com.example.musik.ui.tabs.all_music.tabs.music_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.components.MusicListItem
import com.example.musik.ui.components.info.NoMusicMsg
import com.example.musik.ui.components.verticalScrollbar
import com.example.musik.ui.tabs.all_music.components.ListDivider
import com.example.musik.ui.tabs.all_music.components.LoadingIndicator
import com.example.musik.ui.view_models.MusicListViewModel
import com.example.musik.ui.view_models.PlaybackViewModel
import com.example.musik.ui.view_models.toMediaItem
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MusicListScreen(
	musicListViewModel: MusicListViewModel,
	playbackViewModel: PlaybackViewModel,
	onAddMusic: () -> Unit
) {
	val selectedIds by musicListViewModel.selectedIds.collectAsStateWithLifecycle()
	val isInSelectionMode by musicListViewModel.isInSelectionMode.collectAsStateWithLifecycle()
	val isInMoveMode by musicListViewModel.isInMoveMode.collectAsStateWithLifecycle()

	val scope = rememberCoroutineScope()

	val lazyListState = rememberLazyListState()
	val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
		musicListViewModel.onMove(from.index, to.index)
	}

	val queueSyncEvent by musicListViewModel.queueSyncEvent.collectAsStateWithLifecycle()
	LaunchedEffect(queueSyncEvent) {
		if (!isInMoveMode) {
			queueSyncEvent?.let { q ->
				playbackViewModel.setQueue(q.map { it.toMediaItem() })
			}
		}
	}
	DisposableEffect(Unit) {
		onDispose {
			musicListViewModel.resetMusicList()
		}
	}
	BackHandler(isInSelectionMode || isInMoveMode) {
		musicListViewModel.handleBack()
	}

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
				if (isInMoveMode) {
					// ---===---  Confirm Move Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.Check,
						contentDescription = stringResource(R.string.confirm_move_button)
					) {
						musicListViewModel.confirmMoveButton(playbackViewModel)
					}
					// ---===---  Exit Move Mode Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.Close,
						contentDescription = stringResource(R.string.exit_move_mode_button)
					) {
						musicListViewModel.exitMoveModeButton()
					}
				} else {
					// ---===---  Enter Move Mode Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.UnfoldMore,
						contentDescription = stringResource(R.string.enter_move_mode_button)
					) {
						musicListViewModel.enterMoveModeButton()
					}
				}

				// ---===---  Remove Music Button  ---===---
				if (isInSelectionMode) {
					CustomIconButton(
						iconImageVector = Icons.Rounded.DeleteOutline,
						contentDescription = stringResource(R.string.remove_music_button)
					) {
						scope.launch {
							musicListViewModel.removeMusicButton(playbackViewModel)
						}
					}
				}
			}

			// ---===---  Adding Buttons  ---===---
			Row {
				// ---===---  Add to Playlist Button  ---===---
				if (isInSelectionMode) {
					CustomIconButton(
						iconImageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
						contentDescription = stringResource(R.string.add_to_playlist_button)
					) {
						musicListViewModel.addToPlaylistButton()
					}
				}

				// ---===---  Add YouTube Music Button  ---===---
				CustomIconButton(
					iconImageVector = Icons.Rounded.SmartDisplay,
					contentDescription = stringResource(R.string.add_yt_music_button)
				) {
					musicListViewModel.addYtMusicButton()
				}

				// ---===---  Add Music Button  ---===---
				CustomIconButton(
					iconImageVector = Icons.Rounded.Add,
					contentDescription = stringResource(R.string.add_music_button)
				) {
					musicListViewModel.addingButton { onAddMusic() }
				}

				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		when(val state = musicListViewModel.uiState.collectAsStateWithLifecycle().value) {
			is MusicListViewModel.MusicUiState.Loading -> {
				LoadingIndicator()
			}
			is MusicListViewModel.MusicUiState.Empty -> {
				// ---===---  No Music Msg  ---===---
				NoMusicMsg()
			}
			is MusicListViewModel.MusicUiState.Success -> {
				// ---===---  Music List  ---===---
				LazyColumn(
					state = lazyListState,
					contentPadding = PaddingValues(
						bottom = dimensionResource(R.dimen.x_large_padding)
					),
					modifier = Modifier.fillMaxSize().verticalScrollbar(lazyListState)
				) {
					items(
						count = state.musicList.size,
						key = { state.musicList[it].id }
					) { index ->
						val music = state.musicList[index]
						ReorderableItem(reorderableLazyListState, music.id) { isDragging ->
							val elevation by animateDpAsState(
								if (isDragging) {
									dimensionResource(R.dimen.medium_padding)
								} else {
									dimensionResource(R.dimen.zero)
								}
							)

							MusicListItem(
								musicDetails = music,
								isSelected = music.id in selectedIds,
								onClick = { musicListViewModel.handleTap(
									music.id
								) {
									playbackViewModel.start(music.id)
								}
										  },
								onLongClick = { musicListViewModel.handleHold(music.id) },
								isInMoveMode = isInMoveMode,
								reorderableScope = this,
								modifier = Modifier.shadow(elevation)
							)
						}

						ListDivider(index, state.musicList)
					}
				}
			}
		}
	}
}