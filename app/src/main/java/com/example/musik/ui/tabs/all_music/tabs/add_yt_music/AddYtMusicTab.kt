package com.example.musik.ui.tabs.all_music.tabs.add_yt_music

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.tabs.all_music.tabs.add_yt_music.components.DownloadLocationContainer
import com.example.musik.ui.view_models.AddYtMusicViewModel

@Composable
fun AddYtMusicTab(
	addYtMusicViewModel: AddYtMusicViewModel,
	onBackToMusicList: () -> Unit
) {
	BackHandler(true) { // Always enabled in this screen
		onBackToMusicList()
	}

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// --===--  Back Button  --===--
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Spacer(Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			CustomIconButton(
				iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
				contentDescription = stringResource(R.string.back_button)
			) {
				onBackToMusicList()
			}
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		// --===--  Download Location Container  --===--
		DownloadLocationContainer(addYtMusicViewModel = addYtMusicViewModel)

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		// --===--  PLACEHOLDER  --===--

	}
}