package com.example.musik.ui.screens.player

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.musik.R
import com.example.musik.ui.components.AlbumArtImage
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.view_models.PlaybackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(playbackViewModel: PlaybackViewModel) {
	val musicInfo = playbackViewModel.currentTrack.value
	if (musicInfo == null) {
		Log.wtf("PlayerScreen", "How TF is 'musicInfo' null, WTF did you do?!?!")
	}
	val metadata = musicInfo!!.mediaMetadata
	val curPos = playbackViewModel.currentPos.longValue

	val isPlayerScreenOpen = playbackViewModel.isPlayerScreenOpen.value
	val isPlaying = playbackViewModel.isPlaying.value

	LaunchedEffect(isPlayerScreenOpen, isPlaying) {
		playbackViewModel.onPlayerScreenOpenChanged(isPlayerScreenOpen)
	}
	BackHandler(true) {
		playbackViewModel.onPlayerScreenOpenChanged(false)
	}

	Surface(
		modifier = Modifier.fillMaxSize(),
		color = MaterialTheme.colorScheme.background
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(dimensionResource(R.dimen.player_screen_back_button_top_padding)))

			// --===--  Back Button  --===--
			Row(
				horizontalArrangement = Arrangement.Start,
				modifier = Modifier.fillMaxWidth()
			) {
				Spacer(Modifier.width(dimensionResource(R.dimen.medium_padding)))

				CustomIconButton(
					iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
					contentDescription = stringResource(R.string.back_button)
				) {
					playbackViewModel.onPlayerScreenOpenChanged(false)
				}
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.player_screen_image_top_padding)))

			// --===--  Music Details  --===--
			// --===--  Music Image  --===--
			Crossfade(
				targetState = metadata.artworkUri.toString()
			) { artworkUri ->
				AlbumArtImage(
					artworkUri,
					size = dimensionResource(R.dimen.player_screen_image_size),
					shape = MaterialTheme.shapes.extraLarge
				)
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.player_screen_image_bottom_padding)))

			// --===--  Music Info  --===--
			Row {
				Spacer(Modifier.width(dimensionResource(R.dimen.large_padding)))

				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					modifier = Modifier.weight(1f)
				) {
					// --===--  Music Title  --===--
					AnimatedContent(
						targetState = metadata.title.toString(),
						transitionSpec = { fadeIn() togetherWith fadeOut() }
					) { title ->
						Text(
							text = title,
							style = MaterialTheme.typography.bodyLarge.copy(
								fontSize = 20.sp
							),
							color = MaterialTheme.colorScheme.onSecondary,
							maxLines = 1,
							softWrap = false,
							overflow = TextOverflow.Ellipsis,
							textAlign = TextAlign.Center
						)
					}

					// --===--  Music Artist  --===--
					AnimatedContent(
						targetState = metadata.artist.toString(),
						transitionSpec = { fadeIn() togetherWith fadeOut() }
					) { artist ->
						Text(
							text = artist,
							style = MaterialTheme.typography.bodyMedium.copy(
								fontSize = 18.sp
							),
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							maxLines = 1,
							softWrap = false,
							overflow = TextOverflow.Ellipsis,
							textAlign = TextAlign.Center
						)
					}
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.large_padding)))
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.player_screen_timeline_top_padding)))

			// --===--  Seekbar and Controls  --===--
			Column {
				// --===--  Duration  --===--
				Row(
					horizontalArrangement = Arrangement.SpaceAround,
					modifier = Modifier.fillMaxWidth()
				) {
					// --===--  Current Position  --===--
					Text(
						text = curPos.formatDuration(),
						style = MaterialTheme.typography.bodyMedium.copy(
							color = MaterialTheme.colorScheme.onSecondary,
							fontSize = 15.sp
						)
					)

					// --===--  Total Duration  --===--
					Text(
						text = metadata.durationMs?.formatDuration()
							?: stringResource(R.string.duration_null_value_default),
						style = MaterialTheme.typography.bodyMedium.copy(
							color = MaterialTheme.colorScheme.onSecondary,
							fontSize = 15.sp
						)
					)
				}

				// --===--  Seekbar  --===--
				Row(
					horizontalArrangement = Arrangement.Center
				) {
					Spacer(Modifier.width(dimensionResource(R.dimen.large_padding)))

					Slider(
						value = curPos.toFloat(),
						onValueChange = { playbackViewModel.seekTo(it.toLong()) },
						valueRange = 0f..(metadata.durationMs?.toFloat() ?: 0f),
						thumb = {
							Box(
								modifier = Modifier
									.shadow(
										elevation = dimensionResource(R.dimen.thumb_shadow_elevation),
										shape = CircleShape
									)
									.size(dimensionResource(R.dimen.thumb_size))
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.outlineVariant)
							)
						},
						track = { sliderState ->
							SliderDefaults.Track(
								sliderState = sliderState,
								drawStopIndicator = null,
								thumbTrackGapSize = dimensionResource(R.dimen.zero),
								colors = SliderDefaults.colors(
									activeTrackColor = MaterialTheme.colorScheme.outline,
									inactiveTrackColor = MaterialTheme.colorScheme.onSurface
								)
							)
						},
						modifier = Modifier.weight(1f)
					)

					Spacer(Modifier.width(dimensionResource(R.dimen.large_padding)))
				}
			}
		}
	}
}