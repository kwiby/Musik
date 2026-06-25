package com.example.musik.ui.view_models

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musik.data.services.PlaybackService
import com.google.common.util.concurrent.ListenableFuture

class PlaybackViewModel(application: Application) : AndroidViewModel(application) {
	private var controllerFuture: ListenableFuture<MediaController>? = null
	private var mediaController: MediaController? = null
	private var skipInProgress = false

	val currentTrack = mutableStateOf<MediaItem?>(null)
	val isPlaying = mutableStateOf(false)
	val isShuffling = mutableStateOf(false)
	val loopMode = mutableIntStateOf(Player.REPEAT_MODE_OFF)

	val currentMusicId get() = mediaController?.currentMediaItem?.mediaId
	val hasPrevious = mutableStateOf(false)
	val hasNext = mutableStateOf(false)


	/*
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
	 */

	private fun observePlayer() {
		mediaController?.addListener(object : Player.Listener {
			override fun onIsPlayingChanged(playing: Boolean) {
				// Use this code to keep the pause button icon displaying between skips
				val state = mediaController?.playbackState
				if (skipInProgress && !playing && state == Player.STATE_BUFFERING) {
					return
				}
				skipInProgress = false
				//

				isPlaying.value = playing
			}

			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
				currentTrack.value = mediaItem
				updateSkipStates()
			}

			override fun onTimelineChanged(timeline: Timeline, reason: Int) {
				updateSkipStates()
			}

			override fun onShuffleModeEnabledChanged(isShuffleModeEnabled: Boolean) {
				isShuffling.value = isShuffleModeEnabled
				updateSkipStates()
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
			isPlaying.value = false
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

	// seekToPrevious() is alternative
	fun skipPrev() {
		skipInProgress = true
		mediaController?.seekToPreviousMediaItem()
	}

	// seekToNext() is alternative
	fun skipNext() {
		skipInProgress = true
		mediaController?.seekToNextMediaItem()
	}

	private fun updateSkipStates() {
		hasPrevious.value = mediaController?.hasPreviousMediaItem() ?: false
		hasNext.value = mediaController?.hasNextMediaItem() ?: false
	}


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

				updateSkipStates()
				observePlayer()
			}
		}, ContextCompat.getMainExecutor(application))
	}
}