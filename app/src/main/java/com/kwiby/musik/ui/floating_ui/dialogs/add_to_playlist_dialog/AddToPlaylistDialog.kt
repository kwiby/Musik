package com.kwiby.musik.ui.floating_ui.dialogs.add_to_playlist_dialog

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
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import com.kwiby.musik.ui.components.ListDivider
import com.kwiby.musik.ui.components.lazyVerticalScrollbar
import com.kwiby.musik.ui.floating_ui.dialogs.add_to_playlist_dialog.components.AddablePlaylistItem
import com.kwiby.musik.ui.floating_ui.dialogs.add_to_playlist_dialog.components.info.NoPlaylistsToAddToMsg
import com.kwiby.musik.ui.view_models.MusicListViewModel
import com.kwiby.musik.ui.view_models.PlaylistsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistDialog(
	playlistsViewModel: PlaylistsViewModel,
	musicListViewModel: MusicListViewModel,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	val allPlaylists by playlistsViewModel.playlists.collectAsStateWithLifecycle()
	val selectedPlaylistsWithSongCounts = remember { mutableStateListOf<PlaylistWithSongCount>() }
	val selectedSongIds by musicListViewModel.selectedIds.collectAsStateWithLifecycle()
	val isValidForConfirm = allPlaylists.isNotEmpty() && selectedPlaylistsWithSongCounts.isNotEmpty()
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
						text = stringResource(R.string.add_selected_to_playlists),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.bodyLarge
					)

					Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

					Box(
						modifier = Modifier.weight(1f).fillMaxWidth()
					) {
						when {
							allPlaylists.isEmpty() -> NoPlaylistsToAddToMsg()
							else -> {
								LazyColumn(
									state = lazyListState,
									modifier = Modifier
										.fillMaxSize()
										.lazyVerticalScrollbar(lazyListState)
								) {
									items(
										items = allPlaylists,
										key = { it.playlist.id }
									) { item ->
										val isSelected = item in selectedPlaylistsWithSongCounts

										AddablePlaylistItem(
											playlistWithSongCount = item,
											isSelected = isSelected
										) {
											if (isSelected) {
												selectedPlaylistsWithSongCounts.remove(item)
											} else {
												selectedPlaylistsWithSongCounts.add(item)
											}
										}

										if (item != allPlaylists.lastOrNull()) {
											ListDivider(
												widthFraction = 1f,
												horizontalPadding = dimensionResource(R.dimen.x_small_padding)
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
								if (isValidForConfirm) {
									playlistsViewModel.addSongsToPlaylists(
										selectedPlaylistsWithSongCounts.map { playlistWithSongCount ->
											playlistWithSongCount.playlist.id
										},
										selectedSongIds
									)
									onConfirm()
									onDismiss()
								}
							},
							enabled = isValidForConfirm,
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.playlist_dialog_confirm),
								color = if (isValidForConfirm) {
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