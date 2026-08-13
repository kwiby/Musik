package com.kwiby.musik.ui.tabs.playlists

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.ListDivider
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.lazyVerticalScrollbar
import com.kwiby.musik.ui.floating_ui.AddPlaylistDialog
import com.kwiby.musik.ui.tabs.playlists.components.PlaylistListItem
import com.kwiby.musik.ui.tabs.playlists.components.info.NoPlaylistsMsg
import com.kwiby.musik.ui.view_models.PlaylistsViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PlaylistsTab(
	playlistsViewModel: PlaylistsViewModel
) {
	val isLoading by playlistsViewModel.isLoading

	val playlists by playlistsViewModel.playlists.collectAsStateWithLifecycle()
	val playlistCount = playlists.size
	val isPlaylistEmpty = playlistCount == 0

	var localOrder by remember { mutableStateOf<List<PlaylistWithSongCount>?>(null) }
	val currentPlaylists = localOrder ?: playlists
	val lazyListState = rememberLazyListState()
	val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
		localOrder = (localOrder ?: playlists).toMutableList().apply {
			add(to.index, removeAt(from.index))
		}
	}

	val isAddingPlaylist by playlistsViewModel.isAddingPlaylist

	val isInSelectionMode = playlistsViewModel.isInPlaylistSelectionMode
	val isInMoveMode by playlistsViewModel.isInMoveMode
	val isInEditPlaylistMode by playlistsViewModel.isInEditPlaylistMode

	val selectedPlaylistIds by playlistsViewModel.selectedPlaylists

	DisposableEffect(Unit) {
		onDispose {
			playlistsViewModel.reset()
		}
	}
	LaunchedEffect(playlists) {
		if (!isInMoveMode) {
			localOrder = null
		}
	}
	LaunchedEffect(isInMoveMode) {
		if (!isInMoveMode) {
			localOrder = null
		}
	}
	BackHandler(isInSelectionMode || isInMoveMode || isInEditPlaylistMode) {
		playlistsViewModel.handleBack()
	}


	if (isAddingPlaylist) {
		AddPlaylistDialog(
			playlistsViewModel = playlistsViewModel,
			onDismiss = { playlistsViewModel.changeIsAddingPlaylist(false) }
		)
	}

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		// --===-- All Buttons --===--
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.buttons_horizontal_padding)),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			// --===-- Editing Buttons --===--
			Row {
				// --===-- Edit Playlist Button --===--
				CustomIconButton(
					iconImageVector = if (isInEditPlaylistMode) {
						Icons.Rounded.Close
					} else {
						Icons.Rounded.EditNote
					},
					contentDescription = stringResource(R.string.move_playlists_content_desc)
				) {
					playlistsViewModel.toggleEditPlaylistMode()
				}

				// --===-- Move Playlists Button --===--
				if (isInMoveMode) {
					// ---===---  Confirm Move Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.Check,
						contentDescription = stringResource(R.string.confirm_move_button)
					) {
						localOrder?.let { newOrder ->
							playlistsViewModel.reorderPlaylists(newOrder.map { it.playlist })
						}
						playlistsViewModel.changeMoveMode(false)
					}

					// ---===---  Exit Move Mode Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.Close,
						contentDescription = stringResource(R.string.exit_move_mode_button)
					) {
						playlistsViewModel.changeMoveMode(false)
					}
				} else {
					// ---===---  Enter Move Mode Button  ---===---
					CustomIconButton(
						iconImageVector = Icons.Rounded.SwapVert,
						contentDescription = stringResource(R.string.move_playlists)
					) {
						playlistsViewModel.changeMoveMode(true)
					}
				}

				// ---===---  Remove Playlists Button  ---===---
				if (isInSelectionMode) {
					CustomIconButton(
						iconImageVector = Icons.Rounded.DeleteOutline,
						contentDescription = stringResource(R.string.remove_playlists_button)
					) {
						playlistsViewModel.removePlaylistsButton()
					}
				}
			}

			// --===-- Add Playlist Button --===--
			CustomIconButton(
				iconImageVector = Icons.Rounded.PostAdd,
				contentDescription = stringResource(R.string.add_playlist_content_desc)
			) {
				playlistsViewModel.changeIsAddingPlaylist(true)
			}
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		when {
			isLoading -> LoadingIndicator()
			isPlaylistEmpty -> NoPlaylistsMsg()
			else -> {
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
						items = currentPlaylists,
						key = { it.playlist.id }
					) { item ->
						ReorderableItem(reorderableLazyListState, item.playlist.id) { isDragging ->
							val elevation by animateDpAsState(
								if (isDragging) {
									dimensionResource(R.dimen.medium_padding)
								} else {
									dimensionResource(R.dimen.zero)
								}
							)

							PlaylistListItem(
								playlistWithSongCount = item,
								isSelected = item.playlist in selectedPlaylistIds,
								onClick = {
									playlistsViewModel.handlePlaylistTap(item.playlist)
								},
								onLongClick = {
									playlistsViewModel.handlePlaylistHold(item.playlist)
								},
								isInMoveMode = isInMoveMode,
								isInEditPlaylistMode = isInEditPlaylistMode,
								onEditPlaylistButton = { },
								reorderableScope = this,
								modifier = Modifier.shadow(elevation)
							)
						}

						if (item != currentPlaylists.lastOrNull()) {
							ListDivider(
								widthFraction = 1f,
								horizontalPadding =
									dimensionResource(R.dimen.playlist_divider_horizontal_padding)
							)
						}
					}
				}
			}
		}
	}
}