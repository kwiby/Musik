package com.example.musik.ui.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.musik.R

@Composable
fun AlbumArtImage(contentUri: String) {
	val context = LocalContext.current

	Box(
		modifier = Modifier
			.size(56.dp)
			.clip(MaterialTheme.shapes.small),
		contentAlignment = Alignment.Center
	) {
		AsyncImage(
			model = ImageRequest.Builder(context)
				.data(contentUri.toUri())
				.crossfade(true)
				.build(),
			contentDescription = stringResource(R.string.album_art),
			contentScale = ContentScale.Crop,
			placeholder = painterResource(R.drawable.musik_pixel_icon),
			error = painterResource(R.drawable.musik_pixel_icon_red),
			modifier = Modifier.fillMaxSize()
		)
	}
}