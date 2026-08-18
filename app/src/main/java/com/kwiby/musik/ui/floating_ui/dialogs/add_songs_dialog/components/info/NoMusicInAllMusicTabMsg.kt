package com.kwiby.musik.ui.floating_ui.dialogs.add_songs_dialog.components.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun NoMusicInAllMusicTabMsg() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = dimensionResource(R.dimen.info_msg_vertical_padding)),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = stringResource(R.string.no_music_in_all_music_tab_msg),
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary
		)
	}
}