package com.kwiby.musik.ui.tabs.playlists.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import com.kwiby.musik.ui.components.CustomIconButton
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun PlaylistListItem(
	modifier: Modifier = Modifier,
	playlistWithSongCount: PlaylistWithSongCount,
	isSelected: Boolean,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {},
	isInMoveMode: Boolean = false,
	isInEditPlaylistMode: Boolean = false,
	onEditPlaylistButton: () -> Unit = {},
	reorderableScope: ReorderableCollectionItemScope? = null
) {
	val interactionSource = remember { MutableInteractionSource() }

	Surface(
		color = if (isSelected) {
			MaterialTheme.colorScheme.background
		} else {
			MaterialTheme.colorScheme.secondary
		},
		modifier = modifier
			.padding(horizontal = dimensionResource(R.dimen.medium_padding))
			.height(dimensionResource(R.dimen.playlist_list_item_height))
			.clip(RoundedCornerShape(dimensionResource(R.dimen.list_item_corner_radius)))
			.then(
				if (!isInMoveMode && !isInEditPlaylistMode) {
					Modifier.combinedClickable(
						interactionSource = interactionSource,
						indication = ripple(
							bounded = true,
							color = MaterialTheme.colorScheme.onPrimary
						),
						onClick = onClick,
						onLongClick = onLongClick
					)
				} else {
					Modifier
				}
			)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			if (isInMoveMode) {
				reorderableScope?.let { scope ->
					Icon(
						imageVector = Icons.Rounded.DragIndicator,
						contentDescription = stringResource(R.string.move_playlists),
						tint = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = with(scope) { Modifier.draggableHandle() }
					)
				}
			}

			Row(
				modifier = Modifier
					.weight(1f)
					.padding(
						horizontal = dimensionResource(R.dimen.small_padding),
						vertical = dimensionResource(R.dimen.small_padding)
					),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				// --===-- Playlist Name --===--
				Text(
					text = playlistWithSongCount.playlist.name,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSecondary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)

				// --===--  Playlist Song Count  --===--
				Text(
					text = "${playlistWithSongCount.songCount} songs",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}

			if (isInEditPlaylistMode) {
				CustomIconButton (
					iconImageVector = Icons.Rounded.EditNote,
					contentDescription = stringResource(R.string.edit_playlist_button),
					colour = MaterialTheme.colorScheme.onSurfaceVariant
				) {
					onEditPlaylistButton()
				}
			}
		}
	}
}