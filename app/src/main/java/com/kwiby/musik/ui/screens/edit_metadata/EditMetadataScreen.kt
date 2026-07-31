package com.kwiby.musik.ui.screens.edit_metadata

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.verticalScrollbar
import com.kwiby.musik.ui.screens.edit_metadata.components.options.album.AlbumOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.album_artist.AlbumArtistOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.artist.ArtistOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.artwork.ArtworkOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.disc_number.DiscNumberOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.genre.GenreOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.info.InfoOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.title.TitleOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.track_number.TrackNumberOption
import com.kwiby.musik.ui.screens.edit_metadata.components.options.year.YearOption
import com.kwiby.musik.ui.view_models.EditMetadataViewModel
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.Screen

@Composable
fun EditMetadataScreen(
	editMetadataViewModel: EditMetadataViewModel,
	navViewModel: NavViewModel,
	contentUri: Uri,
	id: Long
) {
	val isLoading by editMetadataViewModel.isLoading

	val context = LocalContext.current
	LaunchedEffect(Unit) {
		editMetadataViewModel.setup(context, contentUri, id)
	}

	val scrollState = rememberScrollState()
	val hasScrolled by remember {
		derivedStateOf { scrollState.value > 0 }
	}
	val surfaceColour by animateColorAsState(
		targetValue = if (hasScrolled) {
			MaterialTheme.colorScheme.secondary
		} else {
			MaterialTheme.colorScheme.background
		},
		animationSpec = tween(durationMillis = 250),
		label = "edit_metadata_surface_colour"
	)

	BackHandler(true) {
		navViewModel.navToScreen(Screen.Main)
	}


	Surface(
		color = surfaceColour,
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(dimensionResource(R.dimen.screen_back_button_top_padding)))

			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = dimensionResource(R.dimen.medium_padding))
			) {
				Row {
					// --===--  Back Button  --===--
					CustomIconButton(
						iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
						contentDescription = stringResource(R.string.back_button)
					) {
						navViewModel.navToScreen(Screen.Main)
					}

					Spacer(Modifier.width(dimensionResource(R.dimen.screen_back_button_right_padding)))

					// --===--  Edit Metadata Screen Title  --===--
					Text(
						text = stringResource(R.string.edit_metadata_screen_title),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.headlineLarge
					)
				}

				CustomIconButton(
					iconImageVector = Icons.Rounded.Save,
					contentDescription = stringResource(R.string.edit_metadata_save),
				) {
					editMetadataViewModel.saveButton()
				}
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.screen_top_section_bottom_padding)))

			// --===--  Music Metadata  --===--
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.background)
					.navigationBarsPadding()
					.verticalScrollbar(scrollState)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(scrollState)
						.padding(horizontal = dimensionResource(R.dimen.screen_options_horizontal_padding)),
					horizontalAlignment = Alignment.Start
				) {
					Spacer(Modifier.height(dimensionResource(R.dimen.screen_options_top_padding)))

					if (isLoading) {
						LoadingIndicator()
					} else {
						InfoOption(editMetadataViewModel)
						ArtworkOption()
						TitleOption()
						ArtistOption()
						AlbumOption()
						AlbumArtistOption()
						TrackNumberOption()
						DiscNumberOption()
						GenreOption()
						YearOption()
					}

					Spacer(Modifier.height(dimensionResource(R.dimen.settings_options_bottom_padding)))
				}
			}
		}
	}
}