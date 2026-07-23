package com.example.musik.ui.tabs.all_music.pages.add_yt_music.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.misc.folder_manager.LocalFolderManager
import com.example.musik.ui.view_models.AddYtMusicViewModel

@Composable
fun DownloadLocationContainer(
	addYtMusicViewModel: AddYtMusicViewModel
) {
	val downloadLocation by addYtMusicViewModel.downloadLocation.collectAsStateWithLifecycle()
	val folderManager = LocalFolderManager.current

	Surface(
		modifier = Modifier
			.height(dimensionResource(R.dimen.download_location_container_height))
			.fillMaxSize()
			.padding(
				start = dimensionResource(R.dimen.download_location_container_left_padding),
				end = dimensionResource(R.dimen.download_location_container_right_padding)
			),
		shape = MaterialTheme.shapes.medium,
		color = MaterialTheme.colorScheme.secondaryContainer,
		border = BorderStroke(
			dimensionResource(R.dimen.container_border_stroke),
			MaterialTheme.colorScheme.secondaryFixed
		),
		shadowElevation = dimensionResource(R.dimen.container_shadow_elevation),
		onClick = {
			folderManager.openFolderSelector()
		}
	) {
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			Spacer(Modifier.width(dimensionResource(R.dimen.download_location_inner_padding)))

			// --===--  Download Location Icon  --===--
			Icon(
				imageVector = Icons.Rounded.Folder,
				contentDescription = stringResource(R.string.set_location_content_description),
				tint = Color.Gray
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

			// --===--  Download Location Text  --===--
			Text(
				text = if (downloadLocation == null) {
					""
				} else if (downloadLocation!!.isEmpty()) {
					stringResource(R.string.no_location_selected)
				} else {
					folderManager.getDisplayPath(downloadLocation!!.toUri())
				},
				modifier = Modifier.weight(1f),
				color = Color.White,
				style = MaterialTheme.typography.bodyLarge,
				overflow = TextOverflow.Ellipsis,
				maxLines = 1
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.download_location_inner_padding)))
		}
	}
}