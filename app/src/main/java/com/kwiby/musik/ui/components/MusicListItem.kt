package com.kwiby.musik.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Edit
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
import com.kwiby.musik.data.data_classes.MusicDetails
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun MusicListItem(
	modifier: Modifier = Modifier,
	musicDetails: MusicDetails,
	isSelected: Boolean,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {},
	isInMoveMode: Boolean = false,
	isInEditMetadataMode: Boolean = false,
	onEditMetadataButton: () -> Unit = {},
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
				if (!isInMoveMode && !isInEditMetadataMode) {
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

			if (isInEditMetadataMode) {
				CustomIconButton (
					iconImageVector = Icons.Rounded.Edit,
					contentDescription = stringResource(R.string.edit_metadata_button),
					colour = MaterialTheme.colorScheme.onSurfaceVariant
				) {
					onEditMetadataButton()
				}
			}
		}
	}
}