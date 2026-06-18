package com.example.musik.ui.misc

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.musik.R
import com.example.musik.data.models.MusicDetails

@Composable
fun AudioFileListItem(musicDetails: MusicDetails) {
	ListItem(
		colors = ListItemDefaults.colors(
			MaterialTheme.colorScheme.secondary
		),
		leadingContent = {
			AlbumArtImage(musicDetails.albumArtUri)
		},
		headlineContent = {
			Text(
				text = musicDetails.title,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		},
		supportingContent = {
			Text(
				text = musicDetails.artist,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		},
		trailingContent = {
			Text(
				text = musicDetails.duration,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		},
		modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.medium_padding))
	)
}