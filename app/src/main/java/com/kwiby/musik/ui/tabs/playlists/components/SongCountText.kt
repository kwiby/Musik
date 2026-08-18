package com.kwiby.musik.ui.tabs.playlists.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R

@Composable
fun getSongCountText(count: Int): String {
	return if (count == 1) {
		stringResource(R.string.playlist_song_count_text)
	} else {
		stringResource(R.string.playlist_songs_count_text)
	}
}