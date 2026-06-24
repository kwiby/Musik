package com.example.musik.ui.view_models

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musik.data.services.PlaybackService
import com.example.musik.ui.misc.unformatDuration
import com.google.common.util.concurrent.ListenableFuture

class PlaybackViewModel(application: Application) : AndroidViewModel(application) {
	private var controllerFuture: ListenableFuture<MediaController>? = null
	private var mediaController: MediaController? = null

	val currentTrack = mutableStateOf<MediaItem?>(null)
	val isPlaying = mutableStateOf(false)
	val isShuffling = mutableStateOf(false)
	val loopMode = mutableIntStateOf(Player.REPEAT_MODE_OFF)

	val currentMusicId get() = mediaController?.currentMediaItem?.mediaId


	private fun createMediaItem(
		id: Long,
		contentUri: String,
		artworkUri: String,
		title: String,
		artist: String,
		duration: String
	): MediaItem {
		return MediaItem.Builder()
			.setMediaId(id.toString())
			.setUri(contentUri.toUri())
			.setMediaMetadata(
				MediaMetadata.Builder()
					.setTitle(title)
					.setArtist(artist)
					.setDurationMs(duration.unformatDuration())
					.setArtworkUri(artworkUri.toUri())
					.build()
			).build()
	}

	private fun observePlayer() {
		mediaController?.addListener(object : Player.Listener {
			override fun onIsPlayingChanged(playing: Boolean) {
				isPlaying.value = playing
			}

			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
				currentTrack.value = mediaItem
			}

			override fun onShuffleModeEnabledChanged(isShuffleModeEnabled: Boolean) {
				isShuffling.value = isShuffleModeEnabled
			}

			override fun onRepeatModeChanged(newMode: Int) {
				loopMode.intValue = newMode
			}
		})
	}

	override fun onCleared() {
		controllerFuture?.let { MediaController.releaseFuture(it) }
		mediaController?.release()

		super.onCleared()
	}

	fun play(id: Long, contentUri: String, artworkUri: String, title: String, artist: String, duration: String) {
		val mediaItem = createMediaItem(id, contentUri, artworkUri, title, artist, duration)

		val controller = mediaController
		if (controller != null) {
			controller.setMediaItem(mediaItem)
			controller.prepare()
			controller.play()
		} else {
			android.util.Log.e("PlaybackViewModel", "Cannot play music: mediaController is not ready.")
		}
	}

	fun cycleLoopMode() {
		val nextVal = when (mediaController?.repeatMode) {
			Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE // Loop current music
			Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL // Loop whole queue
			Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF // No loop
			else -> Player.REPEAT_MODE_OFF
		}

		mediaController?.repeatMode = nextVal
		loopMode.intValue = nextVal
	}

	fun toggleShuffle() {
		val nextVal = !isShuffling.value
		mediaController?.shuffleModeEnabled = nextVal
		isShuffling.value = nextVal
	}

	fun togglePlayPause() {
		mediaController?.let {
			if (it.isPlaying) {
				it.pause()
			} else {
				it.play()
			}
		}
	}

	fun addToQueue(items: List<MediaItem>) {
		mediaController?.addMediaItems(items)
	}

	fun removeCurrentMusic() {
		val controller = mediaController ?: return

		controller.stop()
		controller.removeMediaItem(controller.currentMediaItemIndex)
	}

	fun skipNext() = mediaController?.seekToNextMediaItem()

	fun skipPrev() = mediaController?.seekToPreviousMediaItem()



	init {
		val application: Application = getApplication()
		val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))

		controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
		controllerFuture?.addListener({
			mediaController = controllerFuture?.get()

			mediaController?.let { player ->
				isPlaying.value = player.isPlaying
				currentTrack.value = player.currentMediaItem
				isShuffling.value = player.shuffleModeEnabled
				loopMode.intValue = player.repeatMode

				observePlayer()
			}
		}, ContextCompat.getMainExecutor(application))
	}
}