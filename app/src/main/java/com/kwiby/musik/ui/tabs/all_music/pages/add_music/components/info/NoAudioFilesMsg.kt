package com.kwiby.musik.ui.tabs.all_music.pages.add_music.components.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R

@Composable
fun NoAudioFilesMsg() {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.padding(vertical = dimensionResource(R.dimen.info_msg_vertical_padding))
	) {
		Text(
			text = stringResource(R.string.no_audio_files_msg),
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary
		)
	}
}