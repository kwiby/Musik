package com.kwiby.musik.ui.floating_ui.dialogs.add_to_playlist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.playlist.PlaylistWithSongCount
import com.kwiby.musik.ui.tabs.playlists.components.getSongCountText

@Composable
fun AddablePlaylistItem(
	playlistWithSongCount: PlaylistWithSongCount,
	isSelected: Boolean,
	onClick: () -> Unit = {}
) {
	Surface(
		color = if (isSelected) {
			MaterialTheme.colorScheme.background
		} else {
			MaterialTheme.colorScheme.secondary
		},
		modifier = Modifier
			.height(dimensionResource(R.dimen.playlist_list_item_height))
			.clip(RoundedCornerShape(dimensionResource(R.dimen.list_item_corner_radius)))
			.clickable {
				onClick()
			}
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
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
					modifier = Modifier.weight(1f),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSecondary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)

				// --===--  Playlist Song Count  --===--
				Text(
					text = "(${playlistWithSongCount.songCount} "
							+ getSongCountText(playlistWithSongCount.songCount) + ")",
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}