package com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.musik.R
import com.example.musik.ui.view_models.AddYtMusicViewModel

@Composable
fun VideoInfoCard(
	addYtMusicViewModel: AddYtMusicViewModel
) {
	val videoInfo by addYtMusicViewModel.videoInfo.collectAsStateWithLifecycle()
	var lastVideoInfo by remember { mutableStateOf(videoInfo) }
	if (videoInfo != null) {
		lastVideoInfo = videoInfo
	}

	AnimatedVisibility(
		visible = videoInfo != null,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		AsyncImage(
			model = lastVideoInfo!!.thumbnailUrl,
			contentDescription = null,
			modifier = Modifier
				.padding(
					vertical = dimensionResource(R.dimen.medium_padding),
					horizontal = dimensionResource(R.dimen.medium_padding)
				)
				.aspectRatio(16f/9f)
				.clip(MaterialTheme.shapes.extraSmall),
			contentScale = ContentScale.Fit
		)
	}
}