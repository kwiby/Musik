package com.kwiby.musik.data.services.playback_service.components

import android.os.SystemClock
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.kwiby.musik.data.repositories.music_stats.OfflineMusicStatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val LOG_TAG = "PlaybackStatsTracker"

class PlaybackStatsTracker(
	private val player: Player,
	private val scope: CoroutineScope,
	private val musicStatsRepo: OfflineMusicStatsRepository
) : Player.Listener {
	private val flushDelay = 30_000.milliseconds
	private val playCountThreshold = 5 // Seconds (converted to milliseconds later)

	private var flushLoopJob: Job? = null
	private var sessionTrackId: Long? = null
	private var statsOwnerTrackId: Long? = null
	private var sessionStartTimeMs: Long? = null
	private var sessionTrackDurationMs: Long? = null

	private var wasPlayCountLogged: Boolean = false
	private var totalListenedMs: Long = 0L


	private fun currentTrackId(): Long? = player.currentMediaItem?.mediaId?.toLongOrNull()

	private fun startFlushLoop() {
		if (flushLoopJob?.isActive == true) {
			return
		}

		flushLoopJob = scope.launch {
			while (true) {
				delay(flushDelay)

				if (sessionStartTimeMs != null) {
					val trackId = sessionTrackId

					flush()
					startAccumulating(trackId, false)
				}
			}
		}
	}

	private fun stopFlushLoop() {
		flushLoopJob?.cancel()
		flushLoopJob = null
	}

	private fun ensureStatsFor(trackId: Long?) {
		if (trackId == null) {
			return
		}

		if (statsOwnerTrackId != trackId) {
			totalListenedMs = 0L
			wasPlayCountLogged = false
			statsOwnerTrackId = trackId
		}
	}

	private fun startAccumulating(trackId: Long?, doResetSession: Boolean) {
		if (trackId == null) {
			Log.w(LOG_TAG, "Cannot start listen time tracking, trackId is null")
			return
		}

		if (doResetSession) {
			totalListenedMs = 0L
			wasPlayCountLogged = false
		}

		if (sessionTrackId != trackId) {
			sessionTrackDurationMs = null
		}

		ensureStatsFor(trackId)
		sessionTrackId = trackId
		sessionStartTimeMs = SystemClock.elapsedRealtime()
		player.duration.takeIf { it > 0 }?.let { sessionTrackDurationMs = it }

		startFlushLoop()
	}

	private fun checkPlayCountThreshold(trackId: Long) {
		if (wasPlayCountLogged) {
			return
		}

		val duration = sessionTrackDurationMs
		val thresholdMs = playCountThreshold * 1000L
		val thresholdMet = if (duration != null && duration > 0) {
			totalListenedMs >= minOf(thresholdMs, duration)
		} else {
			totalListenedMs >= thresholdMs
		}

		if (thresholdMet) {
			wasPlayCountLogged = true
			scope.launch(Dispatchers.IO) {
				musicStatsRepo.incrementPlayCount(trackId)
			}
		}
	}

	fun flush() {
		val trackId = sessionTrackId ?: return
		val startTimeMs = sessionStartTimeMs ?: return

		sessionStartTimeMs = null

		val listenTimeMs = SystemClock.elapsedRealtime() - startTimeMs
		if (listenTimeMs > 0) {
			ensureStatsFor(trackId)
			totalListenedMs += listenTimeMs
			checkPlayCountThreshold(trackId)

			scope.launch(Dispatchers.IO) {
				musicStatsRepo.logMusicSession(trackId, listenTimeMs)
			}
		}
	}

	fun release() {
		flush()
		stopFlushLoop()
	}


	override fun onIsPlayingChanged(isPlaying: Boolean) {
		if (isPlaying) {
			startAccumulating(currentTrackId(), sessionTrackId != currentTrackId())
		} else {
			flush()
			stopFlushLoop()
		}
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		flush()

		val newTrackId = mediaItem?.mediaId?.toLongOrNull()
		ensureStatsFor(newTrackId)

		if (player.isPlaying) {
			startAccumulating(newTrackId, true)
		} else {
			stopFlushLoop()
			sessionTrackId = newTrackId
			sessionTrackDurationMs = player.duration.takeIf { it > 0 }
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		if (playbackState == Player.STATE_READY
			&& sessionTrackId != null
			&& sessionTrackId == currentTrackId()
			&& sessionTrackDurationMs == null
		) {
			player.duration.takeIf { it > 0 }?.let { sessionTrackDurationMs = it }
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		flush()
		stopFlushLoop()
	}
}