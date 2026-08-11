package com.kwiby.musik.ui.tabs.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.view_models.PlaylistsViewModel

@Composable
fun PlaylistsTab(
	playlistsViewModel: PlaylistsViewModel
) {
	Column {
		Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		// --===-- All Buttons --===--
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.buttons_horizontal_padding)),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			// --===-- Editing Buttons --===--
			Row {
				// --===-- Edit Playlist Button --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.EditNote,
					contentDescription = stringResource(R.string.move_playlists_content_desc)
				) {

				}

				// --===-- Move Playlists Button --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.SwapVert,
					contentDescription = stringResource(R.string.move_playlists_content_desc)
				) {

				}
			}

			// --===-- Adding Buttons --===--
			Row {
				// --===-- Add Playlist Button --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.PostAdd,
					contentDescription = stringResource(R.string.add_playlist_content_desc)
				) {

				}
			}
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		
	}
}