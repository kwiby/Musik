package com.kwiby.musik.ui.tabs.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.MusicStats
import com.kwiby.musik.ui.components.AlbumArtImage
import com.kwiby.musik.ui.misc.formatDuration

@Composable
fun StatsListItem(
	musicDetails: MusicStats
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(
			modifier = Modifier.weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			// --===--  Album Art  --===--
			AlbumArtImage(
				albumArtUri = musicDetails.albumArtUri,
				size = dimensionResource(R.dimen.stats_list_item_image_size),
				shape = MaterialTheme.shapes.extraSmall
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

			Column {
				// --===--  Title  --===--
				Text(
					text = musicDetails.title,
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.bodyLarge.copy(
						fontSize = 13.sp
					),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)

				// --===--  Artist  --===--
				Text(
					text = musicDetails.artist,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					style = MaterialTheme.typography.bodyMedium.copy(
						fontSize = 11.sp
					),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}

		Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

		// --===--  Stat Values  --===--
		Row {
			// --===--  Play Count  --===--
			Text(
				text = musicDetails.playCount.toString(),
				modifier = Modifier.width(dimensionResource(R.dimen.stats_play_count_width)),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.bodyLarge.copy(
					fontSize = 13.sp
				),
				textAlign = TextAlign.Center
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

			// --===--  Listen Time  --===--
			Text(
				text = musicDetails.totalListenTimeMs.formatDuration(),
				modifier = Modifier.width(dimensionResource(R.dimen.stats_listen_time_width)),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.bodyLarge.copy(
					fontSize = 13.sp
				),
				textAlign = TextAlign.Center
			)
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.stats_list_item_gap)))
}