package com.example.musik.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.musik.R
import com.example.musik.data.models.MusicDetails
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun MusicListItem(
	modifier: Modifier = Modifier,
	musicDetails: MusicDetails,
	isSelected: Boolean,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {},
	isInMoveMode: Boolean = false,
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
			.clip(RoundedCornerShape(dimensionResource(R.dimen.list_item_corner_radius)))
			.then(
				if (!isInMoveMode) {
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
						contentDescription = stringResource(R.string.move_music),
						tint = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = with(scope) { Modifier.draggableHandle() }
					)
				}
			}

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
}