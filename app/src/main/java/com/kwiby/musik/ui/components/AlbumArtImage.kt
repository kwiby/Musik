package com.kwiby.musik.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.kwiby.musik.R
import com.kwiby.musik.data.coil.ArtworkCacheKeys

@Composable
fun AlbumArtImage(
	contentUri: String,
	trackId: String,
	modifier: Modifier = Modifier,
	size: Dp = dimensionResource(R.dimen.album_art_image_size),
	shape: Shape = MaterialTheme.shapes.small
) {
	val context = LocalContext.current
	val density = LocalDensity.current
	val maxSizePx = with(density) {
		dimensionResource(R.dimen.player_screen_image_size).roundToPx()
	}
	val editedAt = ArtworkCacheKeys.getTime(trackId)

	val imageLoader = remember { SingletonImageLoader.get(context) }

	val request = remember(contentUri, editedAt) {
		ImageRequest.Builder(context)
			.data(contentUri.toUri())
			.memoryCacheKey("$trackId-$editedAt")
			.diskCacheKey("$trackId-$editedAt")
			.size(Size(maxSizePx, maxSizePx))
			.crossfade(true)
			.build()
	}


	Box(
		modifier = modifier
			.size(size)
			.clip(shape),
		contentAlignment = Alignment.Center
	) {
		AsyncImage(
			model = request,
			imageLoader = imageLoader,
			contentDescription = stringResource(R.string.album_art),
			contentScale = ContentScale.Crop,
			placeholder = painterResource(R.drawable.musik_pixel_icon),
			error = painterResource(R.drawable.musik_pixel_icon_white),
			modifier = Modifier.fillMaxSize()
		)
	}
}