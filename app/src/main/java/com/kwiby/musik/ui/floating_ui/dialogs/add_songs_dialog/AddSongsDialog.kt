package com.kwiby.musik.ui.floating_ui.dialogs.add_songs_dialog

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.ListDivider
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.lazyVerticalScrollbar
import com.kwiby.musik.ui.floating_ui.dialogs.add_songs_dialog.components.info.NoMusicInAllMusicTabMsg
import com.kwiby.musik.ui.tabs.playlists.components.AddableSongListItem
import com.kwiby.musik.ui.view_models.PlaylistsViewModel

private const val LOG_TAG = "AddSongDialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongsDialog(
	playlistsViewModel: PlaylistsViewModel,
	onDismiss: () -> Unit
) {
	if (playlistsViewModel.openedPlaylistId.value == null) {
		Log.e(LOG_TAG, "Opened playlist id is null")
		return
	}

	val isLoading by playlistsViewModel.isLoadingAllMusic
	val allMusic by playlistsViewModel.allMusic.collectAsStateWithLifecycle()
	val selectedSongIds = remember { mutableStateListOf<Long>() }
	val isSelectedSongIdsNotEmpty = selectedSongIds.isNotEmpty()
	val lazyListState = rememberLazyListState()
	val interactionSource = remember { MutableInteractionSource() }


	BasicAlertDialog(onDismissRequest = onDismiss) {
		val view = LocalView.current
		SideEffect {
			(view.parent as? DialogWindowProvider)?.window?.setDimAmount(0.4f)
		}

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clickable(
					indication = null,
					interactionSource = interactionSource
				) { onDismiss() }
				.padding(vertical = dimensionResource(R.dimen.add_song_dialog_outer_vertical_padding)),
			contentAlignment = Alignment.Center
		) {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.clickable(
						indication = null,
						interactionSource = interactionSource
					) {},
				shape = MaterialTheme.shapes.medium,
				color = MaterialTheme.colorScheme.secondary,
				shadowElevation = dimensionResource(R.dimen.x_small_padding)
			) {
				Column(
					modifier = Modifier
						.fillMaxHeight()
						.padding(
							top = dimensionResource(R.dimen.medium_padding),
							start = dimensionResource(R.dimen.medium_padding),
							end = dimensionResource(R.dimen.medium_padding)
						)
				) {
					Text(
						text = stringResource(R.string.add_songs),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.bodyLarge
					)

					Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

					Box(
						modifier = Modifier.weight(1f).fillMaxWidth()
					) {
						when {
							isLoading -> LoadingIndicator(
								Modifier.padding(vertical = dimensionResource(R.dimen.info_msg_vertical_padding))
							)
							allMusic.isEmpty() -> NoMusicInAllMusicTabMsg()
							else -> {
								LazyColumn(
									state = lazyListState,
									modifier = Modifier
										.fillMaxSize()
										.lazyVerticalScrollbar(lazyListState)
								) {
									items(
										items = allMusic,
										key = { it.id }
									) { item ->
										val isSelected = item.id in selectedSongIds

										AddableSongListItem(
											musicDetails = item,
											isSelected = isSelected,
										) {
											if (isSelected) {
												selectedSongIds.remove(item.id)
											} else {
												selectedSongIds.add(item.id)
											}
										}

										if (item != allMusic.lastOrNull()) {
											ListDivider(
												widthFraction = 0.765f,
												horizontalPadding = dimensionResource(R.dimen.small_padding)
											)
										}
									}
								}
							}
						}
					}

					Spacer(Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End,
						verticalAlignment = Alignment.Bottom
					) {
						TextButton(
							onClick = {
								onDismiss()
							},
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.playlist_dialog_cancel),
								color = MaterialTheme.colorScheme.outline,
								style = MaterialTheme.typography.bodyLarge
							)
						}

						TextButton(
							onClick = {
								if (isSelectedSongIdsNotEmpty) {
									playlistsViewModel.addSongsToPlaylist(
										playlistsViewModel.openedPlaylistId.value!!,
										selectedSongIds
									)
									onDismiss()
								}
							},
							enabled = isSelectedSongIdsNotEmpty,
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.playlist_dialog_confirm),
								color = if (isSelectedSongIdsNotEmpty) {
									MaterialTheme.colorScheme.outline
								} else {
									MaterialTheme.colorScheme.onSurface
								},
								style = MaterialTheme.typography.bodyLarge
							)
						}
					}
				}
			}
		}
	}
}