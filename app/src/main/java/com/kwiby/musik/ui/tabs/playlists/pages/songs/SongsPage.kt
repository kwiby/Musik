package com.kwiby.musik.ui.tabs.playlists.pages.songs

import android.util.Log
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.music.MusicDetails
import com.kwiby.musik.data.data_classes.playlist.Playlist
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.ListDivider
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.MusicListItem
import com.kwiby.musik.ui.components.lazyVerticalScrollbar
import com.kwiby.musik.ui.tabs.playlists.components.getSongCountText
import com.kwiby.musik.ui.tabs.playlists.components.info.NoPlaylistMusicMsg
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.PlaylistsViewModel
import com.kwiby.musik.ui.view_models.toMediaItem
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val LOG_TAG = "SongsPage"

@Composable
fun SongsPage(
	playlistsViewModel: PlaylistsViewModel,
	playbackViewModel: PlaybackViewModel,
	navViewModel: NavViewModel,
	changeSelectedPlaylistToRename: (Playlist) -> Unit
) {
	val isLoading by playlistsViewModel.isLoadingSongs

	val openedPlaylistId by playlistsViewModel.openedPlaylistId
	val openedPlaylist by playlistsViewModel.openedPlaylist.collectAsStateWithLifecycle()
	val songsState = playlistsViewModel.songs?.collectAsStateWithLifecycle()
	if (openedPlaylistId == null || openedPlaylist == null || songsState == null) {
		Log.e(LOG_TAG, "The opened playlist id, opened playlist, and/or songs state is null")
		return
	}

	val songs = songsState.value
	val songCount = songs.size
	val isPlaylistEmpty = songCount == 0

	var localOrder by remember { mutableStateOf<List<MusicDetails>?>(null) }
	val currentSongs = localOrder ?: songs
	val lazyListState = rememberLazyListState()
	val scope = rememberCoroutineScope()
	val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
		localOrder = (localOrder ?: songs).toMutableList().apply {
			add(to.index, removeAt(from.index))
		}
	}

	val isInSelectionMode = playlistsViewModel.isInSongSelectionMode
	val isInMoveMode by playlistsViewModel.isInSongMoveMode
	val isInEditMetadataMode by playlistsViewModel.isInEditMetadataMode

	var selectedSongIds by playlistsViewModel.selectedSongIds

	LaunchedEffect(Unit) {
		if (lazyListState.firstVisibleItemIndex != 0) {
			lazyListState.scrollToItem(0)
		}
	}
	LaunchedEffect(songs) {
		if (!isInMoveMode) {
			localOrder = null
		}
	}
	LaunchedEffect(isInMoveMode) {
		if (!isInMoveMode) {
			localOrder = null
		}
	}
	DisposableEffect(Unit) {
		onDispose {
			playlistsViewModel.resetSongs()
		}
	}
	BackHandler(true) {
		if (isInSelectionMode || isInMoveMode || isInEditMetadataMode) {
			playlistsViewModel.disableAllModes()
		} else {
			playlistsViewModel.closePlaylist()
		}
	}


	if (isLoading) {
		Column {
			Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)
					+ dimensionResource(R.dimen.buttons_vertical_padding)
					+ dimensionResource(R.dimen.x_large_padding)
					+ dimensionResource(R.dimen.medium_padding)))
			LoadingIndicator()
		}
	} else {
		Column(
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

			// --===-- Playlist Overview --===--
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = dimensionResource(R.dimen.buttons_horizontal_padding)),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				// --===-- Playlist Interactions --===--
				Row(
					modifier = Modifier.weight(1f),
					verticalAlignment = Alignment.CenterVertically
				) {
					// --===-- Back Button --===--
					CustomIconButton(
						iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
						contentDescription = stringResource(R.string.back_button)
					) {
						playlistsViewModel.closePlaylist()
					}

					// --===-- Playlist Name & Song Count --===--
					Text(
						text = openedPlaylist?.playlist?.name
							?: stringResource(R.string.unknown_playlist),
						modifier = Modifier.weight(1f),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.bodyLarge,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
					Text(
						text = "(" + openedPlaylist!!.songCount.toString() + " "
								+ getSongCountText(openedPlaylist!!.songCount) + ")",
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						style = MaterialTheme.typography.bodyLarge,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
				}

				// --===-- Edit Playlist Button --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.EditNote,
					contentDescription = stringResource(R.string.edit_playlist_button)
				) {
					changeSelectedPlaylistToRename(openedPlaylist!!.playlist)
				}
			}

			// --===-- All Song Buttons --===--
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = dimensionResource(R.dimen.buttons_horizontal_padding)),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				// --===-- Editing Buttons --===--
				Row {
					// ---===---  Edit Metadata Button  ---===---
					if (isInEditMetadataMode) {
						CustomIconButton(
							iconImageVector = Icons.Rounded.Close,
							contentDescription = stringResource(R.string.exit_edit_metadata_mode_button)
						) {
							playlistsViewModel.exitEditMetadataModeButton()
						}
					} else {
						CustomIconButton(
							iconImageVector = Icons.Rounded.Edit,
							contentDescription = stringResource(R.string.enter_edit_metadata_mode_button)
						) {
							playlistsViewModel.enterEditMetadataModeButton()
						}
					}

					// --===-- Move Playlists Button --===--
					if (isInMoveMode) {
						// ---===---  Confirm Move Button  ---===---
						CustomIconButton(
							iconImageVector = Icons.Rounded.Check,
							contentDescription = stringResource(R.string.confirm_move_button)
						) {
							scope.launch {
								localOrder?.let { newOrder ->
									playlistsViewModel.reorderSongsInPlaylist(
										newOrder.map { it.id }
									)
									playlistsViewModel.confirmSongMoveButton(
										playbackViewModel,
										newOrder
									)
								}

								playlistsViewModel.setSongMoveMode(false)
							}
						}

						// ---===---  Exit Move Mode Button  ---===---
						CustomIconButton(
							iconImageVector = Icons.Rounded.Close,
							contentDescription = stringResource(R.string.exit_move_mode_button)
						) {
							playlistsViewModel.setSongMoveMode(false)
						}
					} else {
						// ---===---  Enter Move Mode Button  ---===---
						CustomIconButton(
							iconImageVector = Icons.Rounded.SwapVert,
							contentDescription = stringResource(R.string.move_playlists)
						) {
							playlistsViewModel.setSongMoveMode(true)
						}
					}

					// ---===---  Remove Music Button  ---===---
					if (isInSelectionMode) {
						CustomIconButton(
							iconImageVector = Icons.Rounded.DeleteOutline,
							contentDescription = stringResource(R.string.remove_music_button)
						) {
							playlistsViewModel.removeSongsFromPlaylist(
								playbackViewModel::removeFromQueue,
								openedPlaylist!!.playlist.id,
								selectedSongIds
							)
							selectedSongIds = emptyList()
						}
					}
				}

				// --===-- Add Music Button --===--
				CustomIconButton(
					iconImageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
					contentDescription = stringResource(R.string.add_music_button)
				) {
					playlistsViewModel.disableAllModes()
					playlistsViewModel.setIsAddingSongs(true)
				}
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

			when {
				isPlaylistEmpty -> NoPlaylistMusicMsg()
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
							items = currentSongs,
							key = { it.id }
						) { item ->
							ReorderableItem(reorderableLazyListState, item.id) { isDragging ->
								val elevation by animateDpAsState(
									if (isDragging) {
										dimensionResource(R.dimen.medium_padding)
									} else {
										dimensionResource(R.dimen.zero)
									}
								)

								MusicListItem(
									musicDetails = item,
									isSelected = item.id in selectedSongIds,
									onClick = {
										playlistsViewModel.handleSongTap(item.id) {
											playbackViewModel.start(
												id = item.id,
												items = currentSongs.map { it.toMediaItem() },
												queueSource = PlaybackViewModel.QueueSource(
													playbackSource = PlaybackViewModel.PlaybackSource.PLAYLIST,
													sourceId = openedPlaylistId!!
												)
											)
										}
									},
									onLongClick = {
										playlistsViewModel.handleSongHold(item.id)
									},
									isInMoveMode = isInMoveMode,
									isInEditMetadataMode = isInEditMetadataMode,
									onEditMetadataButton = {
										playlistsViewModel.editMetadataButton(
											navViewModel = navViewModel,
											contentUri = item.contentUri.toUri(),
											id = item.id
										)
									},
									reorderableScope = this,
									modifier = Modifier.shadow(elevation)
								)
							}

							if (item != currentSongs.lastOrNull()) {
								ListDivider()
							}
						}
					}
				}
			}
		}
	}
}