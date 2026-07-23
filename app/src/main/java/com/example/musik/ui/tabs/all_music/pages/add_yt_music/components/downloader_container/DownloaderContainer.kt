package com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.components.LoadingIndicator
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.YouTubeLinkField
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container.components.VideoInfoCard
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container.components.info.InfoMsg
import com.example.musik.ui.view_models.AddYtMusicViewModel

@Composable
fun DownloadContainer(
	addYtMusicViewModel: AddYtMusicViewModel
) {
	val uiState by addYtMusicViewModel.uiState.collectAsStateWithLifecycle()
	val downloadPercent by addYtMusicViewModel.downloadPercent.collectAsStateWithLifecycle()
	val downloadSpeed by addYtMusicViewModel.downloadSpeed.collectAsStateWithLifecycle()
	val eta by addYtMusicViewModel.eta.collectAsStateWithLifecycle()


	Column(
		modifier = Modifier.fillMaxHeight()
	) {
		Spacer(Modifier.height(dimensionResource(R.dimen.yt_link_field_top_padding)))

		Row(
			verticalAlignment = Alignment.CenterVertically
		) {
			Spacer(Modifier.width(dimensionResource(R.dimen.yt_link_field_left_padding)))

			// --===--  YouTube Link Field  --===--
			YouTubeLinkField(
				addYtMusicViewModel = addYtMusicViewModel,
				modifier = Modifier.weight(1f)
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.yt_link_field_right_padding)))

			// --===--  Start Download Button  --===--
			CustomIconButton(
				iconImageVector = Icons.Rounded.Download,
				contentDescription = stringResource(R.string.yt_link_field_start_button),
				colour = when(uiState) {
					is AddYtMusicViewModel.DownloaderUiState.Loading -> MaterialTheme.colorScheme.onSurface
					is AddYtMusicViewModel.DownloaderUiState.Downloading -> MaterialTheme.colorScheme.onSurface
					else -> MaterialTheme.colorScheme.onSecondary
				}
			) {
				if (!addYtMusicViewModel.isProcessing()) {
					addYtMusicViewModel.startDownloadButton()
				}
			}

			// --===--  Stop Download Button  --===--
			CustomIconButton(
				iconImageVector = Icons.Rounded.Cancel,
				contentDescription = stringResource(R.string.yt_link_field_stop_button),
				colour = when(uiState) {
					is AddYtMusicViewModel.DownloaderUiState.Loading -> MaterialTheme.colorScheme.onSecondary
					is AddYtMusicViewModel.DownloaderUiState.Downloading -> MaterialTheme.colorScheme.onSecondary
					else -> MaterialTheme.colorScheme.onSurface
				}
			) {
				if (addYtMusicViewModel.isProcessing()) {
					addYtMusicViewModel.stopDownloadButton()
				}
			}

			Spacer(Modifier.width(dimensionResource(R.dimen.yt_link_field_submit_button_right_padding)))
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.yt_link_field_bottom_padding)))

		// --===--  Info Box  --===--
		Surface(
			modifier = Modifier
				.fillMaxSize()
				.padding(
					start = dimensionResource(R.dimen.info_box_horizontal_padding),
					end = dimensionResource(R.dimen.info_box_horizontal_padding),
					bottom = dimensionResource(R.dimen.info_box_bottom_padding)
				),
			shape = MaterialTheme.shapes.medium,
			color = MaterialTheme.colorScheme.secondaryContainer
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Spacer(Modifier.height(dimensionResource(R.dimen.downloader_info_msg_offset)))

				InfoMsg(
					text = when (uiState) {
						AddYtMusicViewModel.DownloaderUiState.Empty
							-> stringResource(R.string.downloader_info_msg_empty)
						AddYtMusicViewModel.DownloaderUiState.Loading
							-> stringResource(R.string.downloader_info_msg_processing)
						AddYtMusicViewModel.DownloaderUiState.Downloading
							-> when (downloadPercent) {
								100f -> stringResource(R.string.downloader_info_msg_finishing)
								0f, -1f -> stringResource(R.string.downloader_info_msg_processing)
								else -> stringResource(R.string.downloader_info_msg_downloading) +
										"\n(${downloadPercent}% - $downloadSpeed - $eta)"
							}
						AddYtMusicViewModel.DownloaderUiState.InvalidLink
							-> stringResource(R.string.downloader_info_msg_invalid_link)
						AddYtMusicViewModel.DownloaderUiState.OutdatedYtDlp
							-> stringResource(R.string.downloader_info_msg_outdated_ytdlp)
						AddYtMusicViewModel.DownloaderUiState.Success
							-> stringResource(R.string.downloader_info_msg_success)
						AddYtMusicViewModel.DownloaderUiState.Error
							-> stringResource(R.string.downloader_info_msg_error)
					}
				)

				if (uiState == AddYtMusicViewModel.DownloaderUiState.Loading
					|| uiState == AddYtMusicViewModel.DownloaderUiState.Downloading) {
					Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))
					LoadingIndicator(
						includeDefaultHeight = false,
						fillMaxSize = false
					)
				}

				if (uiState != AddYtMusicViewModel.DownloaderUiState.Empty
					&& uiState != AddYtMusicViewModel.DownloaderUiState.InvalidLink
					&& uiState != AddYtMusicViewModel.DownloaderUiState.Loading
				) {
					VideoInfoCard(addYtMusicViewModel)
				}
			}
		}
	}
}