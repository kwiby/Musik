package com.example.musik.ui.main_container.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.musik.R
import com.example.musik.ui.components.AlbumArtImage
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.PlaybackViewModel
import com.example.musik.ui.view_models.Screen

@Composable
fun PlayerBar(
	sharedTransitionScope: SharedTransitionScope,
	playbackViewModel: PlaybackViewModel,
	navViewModel: NavViewModel
) {
	val interactionSource = remember { MutableInteractionSource() }
	val curMusic = playbackViewModel.currentTrack.value
	val isPlayerScreenClosed = navViewModel.curScreen == Screen.MAIN

	with(sharedTransitionScope) {
		AnimatedVisibility(
			visible = curMusic != null,
			enter = slideInVertically(initialOffsetY = { it }),
			exit = slideOutVertically(targetOffsetY = { it })
		) {
			if (curMusic != null) {
				val metadata = playbackViewModel.currentTrack.value!!.mediaMetadata

				Box(
					modifier = Modifier.fillMaxHeight()
				) {
					Surface(
						shape = RoundedCornerShape(
							topStartPercent = 50,
							topEndPercent = 50
						),
						color = colorResource(R.color.night),
						modifier = Modifier
							.height(dimensionResource(R.dimen.player_bar_height))
							.fillMaxSize()
							.align(Alignment.BottomCenter)
					) {
						Surface(
							shape = CircleShape,
							color = MaterialTheme.colorScheme.background,
							modifier = Modifier
								.height(dimensionResource(R.dimen.player_bar_height))
								.fillMaxSize()
								.align(Alignment.BottomCenter)
								.clickable(
									interactionSource = interactionSource,
									indication = null
								) { playbackViewModel.onPlayerScreenOpenChanged(true, navViewModel) }
						) {
							Row(
								horizontalArrangement = Arrangement.SpaceBetween,
								verticalAlignment = Alignment.CenterVertically
							) {
								// --===--  Music Quick View  --===--
								Row(
									horizontalArrangement = Arrangement.Start,
									verticalAlignment = Alignment.CenterVertically,
									modifier = Modifier.weight(1f)
								) {
									Spacer(modifier = Modifier.width(dimensionResource(R.dimen.player_bar_image_left_padding)))

									// --===--  Music Image  --===--
									Crossfade(
										targetState = metadata.artworkUri.toString(),
										modifier = Modifier.sharedElementWithCallerManagedVisibility(
											sharedContentState = rememberSharedContentState(key = "image"),
											visible = isPlayerScreenClosed
										)
									) { artworkUri ->
										AlbumArtImage(
											contentUri = artworkUri,
											size = dimensionResource(R.dimen.player_bar_image_size),
											shape = MaterialTheme.shapes.extraLarge
										)
									}

									Spacer(modifier = Modifier.width(dimensionResource(R.dimen.player_bar_image_right_padding)))

									// --===--  Music Info  --===--
									Column(
										verticalArrangement = Arrangement.Center
									) {
										// --===--  Music Title  --===--
										AnimatedContent(
											targetState = metadata.title.toString(),
											transitionSpec = { fadeIn() togetherWith fadeOut() },
											modifier = Modifier
												.sharedElementWithCallerManagedVisibility(
													sharedContentState = rememberSharedContentState(
														key = "title"
													),
													visible = isPlayerScreenClosed
												)
												.fillMaxWidth()
										) { title ->
											Text(
												text = title,
												style = MaterialTheme.typography.bodyLarge,
												color = MaterialTheme.colorScheme.onSecondary,
												maxLines = 1,
												softWrap = false,
												overflow = TextOverflow.Ellipsis
											)
										}

										Spacer(modifier = Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

										// --===--  Music Artist  --===--
										AnimatedContent(
											targetState = metadata.artist.toString(),
											transitionSpec = { fadeIn() togetherWith fadeOut() },
											modifier = Modifier
												.sharedElementWithCallerManagedVisibility(
													sharedContentState = rememberSharedContentState(
														key = "artist"
													),
													visible = isPlayerScreenClosed
												)
												.fillMaxWidth()
										) { artist ->
											Text(
												text = artist,
												style = MaterialTheme.typography.bodyMedium,
												color = MaterialTheme.colorScheme.onSurfaceVariant,
												maxLines = 1,
												softWrap = false,
												overflow = TextOverflow.Ellipsis
											)
										}
									}
								}

								// --===--  Playback Control Buttons  --===--
								Row(
									horizontalArrangement = Arrangement.End,
									verticalAlignment = Alignment.CenterVertically
								) {
									// --===--  Skip to Previous Button  --===--
									CustomIconButton(
										modifier = Modifier.sharedElementWithCallerManagedVisibility(
											sharedContentState = rememberSharedContentState(key = "skip_prev"),
											visible = isPlayerScreenClosed
										),
										iconImageVector = Icons.Rounded.SkipPrevious,
										contentDescription = stringResource(R.string.skip_prev),
										colour = if (playbackViewModel.hasPrevious.value) {
											MaterialTheme.colorScheme.onSecondary
										} else {
											MaterialTheme.colorScheme.secondary
										}
									) {
										playbackViewModel.skipPrev()
									}

									// --===--  Play/Pause Button  --===--
									CustomIconButton(
										modifier = Modifier.sharedElementWithCallerManagedVisibility(
											sharedContentState = rememberSharedContentState(key = "play/pause"),
											visible = isPlayerScreenClosed
										),
										iconImageVector = if (playbackViewModel.isPlaying.value) {
											Icons.Rounded.Pause
										} else {
											Icons.Rounded.PlayArrow
										},
										contentDescription = stringResource(R.string.play_pause)
									) {
										playbackViewModel.togglePlayPause()
									}

									// --===--  Skip to Next Button  --===--
									CustomIconButton(
										modifier = Modifier.sharedElementWithCallerManagedVisibility(
											sharedContentState = rememberSharedContentState(key = "skip_next"),
											visible = isPlayerScreenClosed
										),
										iconImageVector = Icons.Rounded.SkipNext,
										contentDescription = stringResource(R.string.skip_next),
										colour = if (playbackViewModel.hasNext.value) {
											MaterialTheme.colorScheme.onSecondary
										} else {
											MaterialTheme.colorScheme.secondary
										}
									) {
										playbackViewModel.skipNext()
									}

									Spacer(modifier = Modifier.width(dimensionResource(R.dimen.small_padding)))
								}
							}
						}
					}
				}
			}
		}
	}
}