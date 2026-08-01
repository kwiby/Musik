package com.kwiby.musik.ui.screens.edit_metadata.components.options.artwork

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionButton
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.view_models.EditMetadataViewModel

private const val LOG_TAG = "ArtworkOption"

@Composable
fun ArtworkOption(
	editMetadataViewModel: EditMetadataViewModel
) {
	val metadata by editMetadataViewModel.metadata

	val context = LocalContext.current
	val density = LocalDensity.current
	val size = dimensionResource(R.dimen.edit_metadata_image_size)
	val sizePx = with(density) {
		size.roundToPx()
	}
	val request = ImageRequest.Builder(context)
		.data(metadata?.artwork)
		.size(Size(sizePx, sizePx))
		.crossfade(false)
		.build()

	val imagePicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri ->
		if (uri != null) {
			editMetadataViewModel.selectImageButton(context, uri)
		} else {
			Log.w(LOG_TAG, "No media selected")
		}
	}


	OptionHeader(stringResource(R.string.edit_metadata_artwork))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Row(
		verticalAlignment = Alignment.Top
	) {
		Box(
			modifier = Modifier
				.size(size)
				.clip(MaterialTheme.shapes.extraSmall),
			contentAlignment = Alignment.Center
		) {
			AsyncImage(
				model = request,
				contentDescription = stringResource(R.string.album_art),
				contentScale = ContentScale.Crop,
				error = painterResource(R.drawable.musik_pixel_icon_red),
				modifier = Modifier.fillMaxSize()
			)
		}

		Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			OptionButton(
				text = "Select image",
				modifier = Modifier.fillMaxWidth(),
				startPadding = dimensionResource(R.dimen.zero),
				endPadding = dimensionResource(R.dimen.zero),
				enableRippleAnimation = true
			) {
				imagePicker.launch(
					PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
				)
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

			OptionButton(
				text = "Remove image",
				modifier = Modifier.fillMaxWidth(),
				startPadding = dimensionResource(R.dimen.zero),
				endPadding = dimensionResource(R.dimen.zero),
				enableRippleAnimation = true
			) {
				editMetadataViewModel.removeImageButton()
			}
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}