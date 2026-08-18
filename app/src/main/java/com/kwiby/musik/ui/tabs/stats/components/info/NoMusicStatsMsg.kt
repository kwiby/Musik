package com.kwiby.musik.ui.tabs.stats.components.info

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
fun NoMusicStatsMsg() {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.padding(vertical = dimensionResource(R.dimen.info_msg_vertical_padding))
	) {
		Text(
			text = stringResource(R.string.stats_no_music_added_msg),
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary
		)
	}
}