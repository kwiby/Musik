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
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material.icons.rounded.SyncDisabled
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
import androidx.media3.common.Player
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
	val loopMode = playbackViewModel.loopMode.intValue

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

			// --===--  Playback Control Buttons  --===--
			Row {
				// --===--  Shuffle Mode Button  --===--
				CustomIconButton(
					iconImageVector = if (playbackViewModel.isShuffling.value) {
						Icons.Rounded.ShuffleOn
					} else {
						Icons.Rounded.Shuffle
					},
					contentDescription = stringResource(R.string.shuffle),
					size = dimensionResource(R.dimen.player_screen_playback_mode_button_size)
				) {
					playbackViewModel.toggleShuffle()
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.player_screen_playback_mode_button_padding)))

				// --===--  Skip to Previous Button  --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.SkipPrevious,
					contentDescription = stringResource(R.string.skip_prev),
					size = dimensionResource(R.dimen.player_screen_playback_control_button_size),
					colour = if (playbackViewModel.hasPrevious.value) {
						MaterialTheme.colorScheme.onSecondary
					} else {
						MaterialTheme.colorScheme.secondary
					}
				) {
					playbackViewModel.skipPrev()
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.player_screen_playback_control_button_padding)))

				// --===--  Play/Pause Button  --===--
				CustomIconButton(
					iconImageVector = if (playbackViewModel.isPlaying.value) {
						Icons.Rounded.Pause
					} else {
						Icons.Rounded.PlayArrow
					},
					contentDescription = stringResource(R.string.play_pause),
					size = dimensionResource(R.dimen.player_screen_playback_control_button_size)
				) {
					playbackViewModel.togglePlayPause()
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.player_screen_playback_control_button_padding)))

				// --===--  Skip to Next Button  --===--
				CustomIconButton(
					iconImageVector = Icons.Rounded.SkipNext,
					contentDescription = stringResource(R.string.skip_next),
					size = dimensionResource(R.dimen.player_screen_playback_control_button_size),
					colour = if (playbackViewModel.hasNext.value) {
						MaterialTheme.colorScheme.onSecondary
					} else {
						MaterialTheme.colorScheme.secondary
					}
				) {
					playbackViewModel.skipNext()
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.player_screen_playback_mode_button_padding)))

				// --===--  Loop Mode Button  --===--
				CustomIconButton(
					iconImageVector = when (loopMode) {
						Player.REPEAT_MODE_OFF -> Icons.Rounded.SyncDisabled
						Player.REPEAT_MODE_ALL -> Icons.Rounded.SyncAlt
						Player.REPEAT_MODE_ONE -> Icons.Rounded.Loop
						else -> Icons.Rounded.SyncDisabled
					},
					contentDescription = stringResource(R.string.loop_mode),
					size = dimensionResource(R.dimen.player_screen_playback_mode_button_size)
				) {
					playbackViewModel.cycleLoopMode()
				}
			}
		}
	}
}