package com.example.musik.ui.tabs.all_music.tabs.add_yt_music.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.view_models.AddYtMusicViewModel

@Composable
fun DownloadLocationContainer(
	addYtMusicViewModel: AddYtMusicViewModel
) {
	val interactionSource = remember { MutableInteractionSource() }
	val downloadLocation by addYtMusicViewModel.downloadLocation.collectAsStateWithLifecycle()

	Surface(
		modifier = Modifier
			.height(dimensionResource(R.dimen.download_location_container_height))
			.fillMaxSize()
			.padding(horizontal = dimensionResource(R.dimen.download_location_container_horizontal_padding))
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) {

			},
		shape = MaterialTheme.shapes.medium,
		color = MaterialTheme.colorScheme.secondaryContainer,
		border = BorderStroke(
			dimensionResource(R.dimen.download_location_container_border_stroke),
			MaterialTheme.colorScheme.secondaryFixed
		),
		shadowElevation = dimensionResource(R.dimen.download_location_container_shadow_elevation)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			// --===--  Download Location text  --===--
			Row(
				horizontalArrangement = Arrangement.Start
			) {
				Spacer(Modifier.width(dimensionResource(R.dimen.download_location_text_left_padding)))

				Text(
					text = downloadLocation.ifEmpty {
						stringResource(R.string.no_location_selected)
					},
					color = Color.Gray,
					style = MaterialTheme.typography.bodyLarge,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1
				)
			}

			// --===--  Select Location Button  --===--
			Row(
				horizontalArrangement = Arrangement.End
			) {
				CustomIconButton(
					iconImageVector = Icons.Rounded.Folder,
					contentDescription = stringResource(R.string.set_location_content_description),
					colour = Color.Gray
				) {
					// TODO: Add click action
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.download_location_button_right_padding)))
			}
		}
	}
}