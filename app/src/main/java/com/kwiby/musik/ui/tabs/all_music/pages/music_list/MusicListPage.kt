package com.kwiby.musik.ui.tabs.all_music.pages.music_list

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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.MusicDetails
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.ListDivider
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.MusicListItem
import com.kwiby.musik.ui.components.lazyVerticalScrollbar
import com.kwiby.musik.ui.floating_ui.dialogs.add_to_playlist_dialog.AddToPlaylistDialog
import com.kwiby.musik.ui.tabs.all_music.components.info.NoMusicMsg
import com.kwiby.musik.ui.view_models.MusicListViewModel
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.PlaylistsViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MusicListPage(
	musicListViewModel: MusicListViewModel,
	playbackViewModel: PlaybackViewModel,
	navViewModel: NavViewModel,
	playlistsViewModel: PlaylistsViewModel,
	onAddMusic: () -> Unit,
	onAddYtMusic: () -> Unit
) {
	val selectedIds by musicListViewModel.selectedIds.collectAsStateWithLifecycle()
	val isInMoveMode by musicListViewModel.isInMoveMode.collectAsStateWithLifecycle()
	val isInSelectionMode by musicListViewModel.isInSelectionMode.collectAsStateWithLifecycle()
	val isInEditMetadataMode by musicListViewModel.isInEditMetadataMode

	val scope = rememberCoroutineScope()

	val lazyListState = rememberLazyListState()
	val queueState by musicListViewModel.uiState.collectAsStateWithLifecycle()
	var localOrder by remember { mutableStateOf<List<MusicDetails>?>(null) }
	val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
		val current = localOrder
			?: (queueState as? MusicListViewModel.MusicUiState.Success)?.musicList
			?: return@rememberReorderableLazyListState
		val list = current.toMutableList()
		val moved = list.removeAt(from.index)
		list.add(to.index, moved)
		localOrder = list
	}

	val isAddingToPlaylist by musicListViewModel.isAddingToPlaylist

	DisposableEffect(Unit) {
		onDispose {
			musicListViewModel.resetMusicList()
		}
	}
	BackHandler(isInSelectionMode || isInMoveMode || isInEditMetadataMode) {
		musicListViewModel.handleBack()
	}
	LaunchedEffect(isInMoveMode) {
		if (!isInMoveMode) {
			localOrder = null
		}
	}


	// --===-- Add to Playlist Dialog --===--
	if (isAddingToPlaylist) {
		AddToPlaylistDialog(playlistsViewModel, musicListViewModel) {
			musicListViewModel.setIsAddingToPlaylist(false)
		}
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

				// ---===---  Edit Metadata Button  ---===---
				if (isInEditMetadataMode) {
					CustomIconButton(
						iconImageVector = Icons.Rounded.Close,
						contentDescription = stringResource(R.string.exit_edit_metadata_mode_button)
					) {
						musicListViewModel.exitEditMetadataModeButton()
					}
				} else {
					CustomIconButton(
						iconImageVector = Icons.Rounded.Edit,
						contentDescription = stringResource(R.string.enter_edit_metadata_mode_button)
					) {
						musicListViewModel.enterEditMetadataModeButton()
					}
				}

				// ---===---  Move Music Button  ---===---
				if (isInMoveMode) {
					// ---===---  Confirm Move Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.Check,
						contentDescription = stringResource(R.string.confirm_move_button)
					) {
						localOrder?.let {
							musicListViewModel.setQueueOrder(it) 
						}

						scope.launch {
							musicListViewModel.confirmMoveButton(playbackViewModel)
							if (lazyListState.firstVisibleItemIndex != 0) {
								lazyListState.scrollToItem(0)
							}
						}
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
						iconImageVector = Icons.Rounded.SwapVert,
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
					musicListViewModel.addYtMusicButton { onAddYtMusic() }
				}

				// ---===---  Add Music Button  ---===---
				CustomIconButton(
					iconImageVector = Icons.Rounded.Add,
					contentDescription = stringResource(R.string.add_music_button)
				) {
					musicListViewModel.addMusicButton { onAddMusic() }
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		when(val state = queueState) {
			is MusicListViewModel.MusicUiState.Loading -> {
				LoadingIndicator()
			}
			is MusicListViewModel.MusicUiState.Empty -> {
				// ---===---  No Music Msg  ---===---
				NoMusicMsg()
			}
			is MusicListViewModel.MusicUiState.Success -> {
				// ---===---  Music List  ---===---
				val displayList = localOrder ?: state.musicList

				LazyColumn(
					state = lazyListState,
					contentPadding = PaddingValues(
						bottom = dimensionResource(R.dimen.x_large_padding)
					),
					modifier = Modifier
						.fillMaxSize()
						.lazyVerticalScrollbar(lazyListState)
				) {
					items(
						count = displayList.size,
						key = { displayList[it].id }
					) { index ->
						val music = displayList[index]

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
								onClick = {
									musicListViewModel.handleTap(music.id) {
										playbackViewModel.start(music.id)
									}
							    },
								onLongClick = {
									musicListViewModel.handleHold(music.id)
							    },
								isInMoveMode = isInMoveMode,
								isInEditMetadataMode = isInEditMetadataMode,
								onEditMetadataButton = {
									musicListViewModel.editMetadataButton(
										navViewModel,
										music.contentUri.toUri(),
										music.id
									)
							   	},
								reorderableScope = this,
								modifier = Modifier.shadow(elevation)
							)
						}

						if (index != displayList.lastIndex) {
							ListDivider()
						}
					}
				}
			}
		}
	}
}