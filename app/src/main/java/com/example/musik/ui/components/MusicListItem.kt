package com.example.musik.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.musik.R
import com.example.musik.data.models.MusicDetails

@Composable
fun MusicListItem(
	musicDetails: MusicDetails,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }

	Surface(
		color = if (isSelected) {
			MaterialTheme.colorScheme.background
		} else {
			MaterialTheme.colorScheme.secondary
		},
		modifier = Modifier
			.padding(horizontal = dimensionResource(R.dimen.medium_padding))
			.clip(RoundedCornerShape(dimensionResource(R.dimen.list_item_corner_radius)))
			.clickable(
				interactionSource = interactionSource,
				indication = ripple(
					bounded = true,
					color = MaterialTheme.colorScheme.onPrimary
				),
				onClick = onClick
			)
	) {
		ListItem(
			colors = ListItemDefaults.colors(
				containerColor = Color.Transparent
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
			}
		)
	}
}