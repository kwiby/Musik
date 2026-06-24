package com.example.musik.playback

import android.app.Application
import android.content.ComponentName
import android.util.Log
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
	
	fun setQueue(items: List<MediaItem>) {
		val controller = mediaController ?: return

		val currentIds = (0 until controller.mediaItemCount).map {
			controller.getMediaItemAt(it).mediaId
		}
		val newIds = items.map { it.mediaId }

		if (currentIds == newIds) {
			return
		}

		// Find items that are actually new and append them
		val toAdd = items.filter { it.mediaId !in currentIds }
		if (toAdd.isNotEmpty()) {
			controller.addMediaItems(toAdd)
			return
		}

		// Reorder (confirm or revert)
		val idToIndex = (0 until controller.mediaItemCount).associateBy {
			controller.getMediaItemAt(it).mediaId
		}.toMutableMap()

		newIds.forEachIndexed { targetIndex, id ->
			val currentIndex = idToIndex[id] ?: return@forEachIndexed
			if (currentIndex != targetIndex) {
				controller.moveMediaItem(currentIndex, targetIndex)
				idToIndex.entries.forEach { entry ->
					when {
						entry.key == id -> entry.setValue(targetIndex)
						entry.value in minOf(currentIndex, targetIndex)..maxOf(currentIndex, targetIndex) ->
							entry.setValue(if (currentIndex < targetIndex) entry.value - 1 else entry.value + 1)
					}
				}
			}
		}

		/* BACKUP CODE FOR REORDERING!!!
		newIds.forEachIndexed { targetIndex, id ->
			val currentIndex = (0 until controller.mediaItemCount).indexOfFirst {
				controller.getMediaItemAt(it).mediaId == id
			}
			if (currentIndex != targetIndex) {
				controller.moveMediaItem(currentIndex, targetIndex)
			}
		}*/
	}

	fun play(id: Long) {
		val controller = mediaController

		if (controller != null) {
			val targetIndex = (0 until controller.mediaItemCount).firstOrNull {
				controller.getMediaItemAt(it).mediaId == id.toString()
			}

			if (targetIndex != null) {
				controller.seekToDefaultPosition(targetIndex)
				controller.play()
			} else {
				Log.e("PlaybackViewModel", "Track $id not found in queue.")
			}
		} else {
			Log.e("PlaybackViewModel", "Cannot play music: mediaController is not ready.")
		}
	}

	fun removeFromQueue(ids: Set<Long>) {
		val controller = mediaController ?: return

		val indicesToRemove = (0 until controller.mediaItemCount).filter {
			controller.getMediaItemAt(it).mediaId.toLongOrNull() in ids
		}.sortedDescending()
		// Need descending index order to remove from end, preventing shifting issues

		val currentlyPlayingRemoved = controller.currentMediaItem?.mediaId?.toLongOrNull() in ids
		if (currentlyPlayingRemoved) {
			controller.stop()
		}

		indicesToRemove.forEach { controller.removeMediaItem(it) }
	}

	fun moveQueueItem(from: Int, to: Int) {
		mediaController?.moveMediaItem(from, to)
	}

	fun cycleLoopMode() {
		val nextLoopMode = when (mediaController?.repeatMode) {
			Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE // Loop current music
			Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL // Loop whole queue
			Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF // No loop
			else -> Player.REPEAT_MODE_OFF // Default value just in case
		}

		mediaController?.repeatMode = nextLoopMode
		loopMode.intValue = nextLoopMode
	}

	fun toggleShuffle() {
		val nextShuffleMode = !isShuffling.value

		mediaController?.shuffleModeEnabled = nextShuffleMode
		isShuffling.value = nextShuffleMode
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
		val sessionToken = SessionToken(
			application, ComponentName(application, PlaybackService::class.java)
		)

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