package com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container.components.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

@Composable
fun InfoMsg(
	text: String
) {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary,
			textAlign = TextAlign.Center
		)
	}
}