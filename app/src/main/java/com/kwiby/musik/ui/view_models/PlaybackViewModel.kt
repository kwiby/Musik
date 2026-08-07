package com.kwiby.musik.ui.view_models

import android.app.Application
import android.content.ComponentName
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.kwiby.musik.data.services.playback_service.PlaybackService
import com.kwiby.musik.ui.MusikApplication
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val LOG_TAG = "PlaybackViewModel"

class PlaybackViewModel(
	application: MusikApplication
) : AndroidViewModel(application) {
	// ================================================================================================
	// --===--  Variables  --===--
	private var controllerFuture: ListenableFuture<MediaController>? = null
	private var mediaController: MediaController? = null
	private var skipInProgress = false
	private var pendingPlayId: Long? = null
	private var pendingQueue: List<MediaItem>? = null
	private var posJob: Job? = null

	val currentTrack = mutableStateOf<MediaItem?>(null)
	val isPlaying = mutableStateOf(false)
	val isShuffling = mutableStateOf(false)
	val loopMode = mutableIntStateOf(Player.REPEAT_MODE_ALL)

	val hasPrevious = mutableStateOf(false)
	val hasNext = mutableStateOf(false)

	val currentPos = mutableLongStateOf(0L)

	var onDeadTrackDetected: (suspend (Set<Long>) -> Unit)? = null
	// ================================================================================================


	// ================================================================================================
	// --===--  Player Observer  --===--
	private fun observePlayer() {
		mediaController?.addListener(object : Player.Listener {
			override fun onIsPlayingChanged(playing: Boolean) {
				// Use this code to keep the pause button icon displaying between skips ---
				val state = mediaController?.playbackState
				if ((skipInProgress || state == Player.STATE_BUFFERING) && !playing) {
					return
				}
				skipInProgress = false
				// ---

				isPlaying.value = playing
			}

			override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
				currentPos.longValue = 0L
				currentTrack.value = mediaItem
				updateSkipStates()
			}

			override fun onTimelineChanged(timeline: Timeline, reason: Int) {
				updateSkipStates()

				val id = pendingPlayId
				if (id != null) {
					val index = (0 until (mediaController?.mediaItemCount ?: 0)).firstOrNull {
						mediaController?.getMediaItemAt(it)?.mediaId == id.toString()
					}

					if (index != null) {
						pendingPlayId = null
						currentPos.longValue = 0L
						mediaController?.seekToDefaultPosition(index)
						mediaController?.play()
						isPlaying.value = true
					}
				} else {
					currentTrack.value = mediaController?.currentMediaItem
					currentPos.longValue = mediaController?.currentPosition ?: 0L
				}
			}

			override fun onPositionDiscontinuity(
				oldPosition: Player.PositionInfo,
				newPosition: Player.PositionInfo,
				reason: Int
			) {
				currentPos.longValue = newPosition.positionMs
			}

			override fun onShuffleModeEnabledChanged(isShuffleModeEnabled: Boolean) {
				isShuffling.value = isShuffleModeEnabled
				updateSkipStates()
			}

			override fun onRepeatModeChanged(newMode: Int) {
				loopMode.intValue = newMode
				updateSkipStates()
			}

			override fun onPlayerError(error: PlaybackException) {
				Log.e(LOG_TAG, "PLAYBACK ERROR: ${error.errorCodeName}", error)

				val controller = mediaController ?: return
				val failedIndex = controller.currentMediaItemIndex
				val failedItem = controller.currentMediaItem
				val failedId = failedItem?.mediaId?.toLongOrNull()

				if (failedIndex in 0 until controller.mediaItemCount) {
					controller.removeMediaItem(failedIndex)
				}

				// Remove the dead track from the db
				failedId?.let { id ->
					viewModelScope.launch {
						onDeadTrackDetected?.invoke(setOf(id))
					}
				}

				controller.pause()
				isPlaying.value = false
				if (controller.mediaItemCount == 0) {
					currentTrack.value = null
				}
			}
		})
	}

	override fun onCleared() {
		controllerFuture?.let { MediaController.releaseFuture(it) }
		mediaController?.release()
	}
	// ================================================================================================


	// ================================================================================================
	// --===--  Position Updating  --===--
	private fun startPosUpdates() {
		if (posJob?.isActive == true) {
			return
		}

		posJob = viewModelScope.launch {
			while (true) {
				mediaController?.let {
					currentPos.longValue = it.currentPosition
				}

				delay(100.milliseconds)
			}
		}
	}

	private fun stopPosUpdates() {
		posJob?.cancel()
		posJob = null
	}

	fun onPlayerScreenOpenChanged(isVisible: Boolean, navViewModel: NavViewModel) {
		if (isVisible) {
			navViewModel.navToScreen(Screen.Player)
		} else {
			navViewModel.navToScreen(Screen.Main)
		}

		if (isVisible) {
			mediaController?.let {
				currentPos.longValue = it.currentPosition
			}

			if (isPlaying.value) {
				startPosUpdates()
			}
		} else {
			stopPosUpdates()
		}
	}
	// ================================================================================================


	// ================================================================================================
	// --===--  Player Queue Controls  --===--
	fun setQueue(items: List<MediaItem>) {
		val controller = mediaController
		if (controller == null) {
			pendingQueue = items
			return
		}

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
			controller.prepare()

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

	fun removeFromQueue(ids: Set<Long>) {
		val controller = mediaController ?: return

		val indicesToRemove = (0 until controller.mediaItemCount).filter {
			controller.getMediaItemAt(it).mediaId.toLongOrNull() in ids
		}.sortedDescending()
		// Need descending index order to remove from end, preventing shifting issues

		val currentlyPlayingRemoved = controller.currentMediaItem?.mediaId?.toLongOrNull() in ids

		indicesToRemove.forEach { controller.removeMediaItem(it) }

		if (currentlyPlayingRemoved) {
			controller.pause()
			//isPlaying.value = false
		}
	}

	fun refreshMediaItem(id: Long) {
		val controller = mediaController ?: return
		val index = (0 until controller.mediaItemCount).firstOrNull {
			controller.getMediaItemAt(it).mediaId == id.toString()
		} ?: return

		val wasCurrent = controller.currentMediaItemIndex == index
		val wasPlaying = controller.isPlaying
		val item = controller.getMediaItemAt(index)

		controller.removeMediaItem(index)
		controller.addMediaItem(index, item)

		if (wasCurrent) {
			controller.seekTo(index, 0L)
			if (wasPlaying) controller.play()
		}
	}
	// ================================================================================================


	// ================================================================================================
	// --===--  Player Playback Controls  --===--
	private var toast: Toast? = null
	private fun emitToast(text: String) {
		toast?.cancel()
		toast = Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).also {
			it.show()
		}
	}

	private fun updateSkipStates() {
		hasPrevious.value = mediaController?.hasPreviousMediaItem() ?: false
		hasNext.value = mediaController?.hasNextMediaItem() ?: false
	}

	fun start(id: Long) {
		val controller = mediaController

		if (controller != null) {
			val targetIndex = (0 until controller.mediaItemCount).firstOrNull {
				controller.getMediaItemAt(it).mediaId == id.toString()
			}


			if (targetIndex != null) {
				currentPos.longValue = 0L
				controller.seekToDefaultPosition(targetIndex)
				controller.play()
				isPlaying.value = true
			} else {
				Log.e(LOG_TAG, "Track $id not found in queue.")
			}
		} else {
			pendingPlayId = id
			Log.e(LOG_TAG, "Cannot play music, mediaController is not ready.")
		}
	}

	fun togglePlayPause() {
		mediaController?.let {
			if (it.isPlaying) {
				it.pause()
				isPlaying.value = false
			} else {
				it.play()
				isPlaying.value = true
			}
		}
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

	fun seekTo(pos: Long) {
		val controller = mediaController ?: return

		currentPos.longValue = pos
		controller.seekTo(pos)
	}

	fun cycleLoopMode() {
		var nextLoopMode: Int
		when (mediaController?.repeatMode) {
			Player.REPEAT_MODE_ALL -> {
				nextLoopMode = Player.REPEAT_MODE_ONE
				emitToast("Looping current song")
			} // Switch to loop current music
			Player.REPEAT_MODE_ONE -> {
				nextLoopMode = Player.REPEAT_MODE_OFF
				emitToast("Looping disabled")
			}  // Switch to no loop
			Player.REPEAT_MODE_OFF -> {
				nextLoopMode = Player.REPEAT_MODE_ALL
				emitToast("Looping whole queue")
			} // Switch to loop whole queue
			else -> {
				nextLoopMode = Player.REPEAT_MODE_ALL
				emitToast("Looping disabled")
			} // Default value just in case
		}

		mediaController?.repeatMode = nextLoopMode
		loopMode.intValue = nextLoopMode
	}

	@OptIn(UnstableApi::class)
	fun toggleShuffle() {
		val controller = mediaController ?: return
		val nextShuffleMode = !isShuffling.value

		isShuffling.value = nextShuffleMode
		controller.shuffleModeEnabled = nextShuffleMode
	}
	// ================================================================================================


	// ================================================================================================
	// --===--  Init  --===--
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

				// TODO: Allow users to set default loop mode, or save loop mode for after relaunch
				if (player.repeatMode == Player.REPEAT_MODE_OFF) {
					player.repeatMode = Player.REPEAT_MODE_ALL
				}
				loopMode.intValue = player.repeatMode

				observePlayer()

				pendingQueue?.let { queue ->
					pendingQueue = null
					setQueue(queue)
				}

				if (player.mediaItemCount == 0) {
					player.prepare()
				}

				updateSkipStates()
			}
		}, ContextCompat.getMainExecutor(application))
	}
	// ================================================================================================
}