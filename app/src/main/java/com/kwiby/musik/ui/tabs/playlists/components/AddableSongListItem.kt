package com.kwiby.musik.ui.tabs.playlists.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.kwiby.musik.data.data_classes.music.MusicDetails
import com.kwiby.musik.ui.components.AlbumArtImage

@Composable
fun AddableSongListItem(
	musicDetails: MusicDetails,
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
			.clip(RoundedCornerShape(dimensionResource(R.dimen.list_item_corner_radius)))
			.clickable { onClick() }
	) {
		Row(
			modifier = Modifier.padding(dimensionResource(R.dimen.small_padding)),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically
			) {
				// --===--  Artwork  --===--
				AlbumArtImage(
					contentUri = musicDetails.contentUri,
					trackId = musicDetails.id.toString()
				)

				Spacer(Modifier.width(dimensionResource(R.dimen.medium_padding)))

				Column {
					// --===--  Title  --===--
					Text(
						text = musicDetails.title,
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.onSecondary,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)

					// --===--  Artist  --===--
					Text(
						text = musicDetails.artist,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
			}

			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

			// --===--  Duration  --===--
			Text(
				text = musicDetails.durationMs,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}
	}
}