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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

private const val LOG_TAG = "PlaybackStatsTracker"

class PlaybackStatsTracker(
	private val player: Player,
	private val scope: CoroutineScope,
	private val musicStatsRepo: OfflineMusicStatsRepository
) : Player.Listener {
	private val updateDelaySec = 30
	private val playCountThresholdSec = 3

	private val sessionMutex = Mutex()

	private var loopJob: Job? = null
	private var updateJob: Job? = null
	private var sessionTrackId: Long? = null
	private var sessionTotalDurationMs: Long? = null
	private var sessionTotalListenTime: Long? = null
	private var sessionLastUpdateTimeMs: Long? = null
	private var wasSessionPlayCountLogged: Boolean = false


	private fun getCurTrackId(): Long? {
		return player.currentMediaItem?.mediaId?.toLongOrNull()
	}

	private fun updateDB() {
		val curSessionTrackId = sessionTrackId
		if (curSessionTrackId == null) {
			Log.e(LOG_TAG, "Cannot update the database, session track id is null")
			return
		}
		if (sessionTotalDurationMs == null) {
			Log.e(LOG_TAG, "Cannot update the database, session total duration is null")
			return
		}
		if (sessionTotalListenTime == null) {
			Log.e(LOG_TAG, "Cannot update the database, session total listen time is null")
			return
		}
		if (sessionLastUpdateTimeMs == null) {
			Log.e(LOG_TAG, "Cannot update the database, session start time is null")
			return
		}
		if (player.currentMediaItem == null) {
			Log.e(LOG_TAG, "Cannot update the database, current media item is null")
			return
		}
		if (updateJob?.isActive == true) {
			Log.w(LOG_TAG, "Cannot update the database, update job is active")
			sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
			return
		}

		val playCountThresholdMs = playCountThresholdSec * 1000L
		val totalDuration = sessionTotalDurationMs!!
		val curElapsedRealtime = SystemClock.elapsedRealtime()
		val timeElapsedSinceLastUpdate = curElapsedRealtime - sessionLastUpdateTimeMs!!
		sessionLastUpdateTimeMs = curElapsedRealtime
		sessionTotalListenTime = sessionTotalListenTime!! + timeElapsedSinceLastUpdate
		val totalListenTime = sessionTotalListenTime!!
		val doLogPlayCount = !wasSessionPlayCountLogged && totalListenTime >= minOf(playCountThresholdMs, totalDuration)
		if (doLogPlayCount) {
			wasSessionPlayCountLogged = true
		}

		updateJob = scope.launch(Dispatchers.IO) {
			try {
				if (doLogPlayCount) {
					musicStatsRepo.logPlayCount(curSessionTrackId)
				}
				musicStatsRepo.logListenTime(curSessionTrackId, timeElapsedSinceLastUpdate)
			} catch (e: Exception) {
				Log.e(LOG_TAG, "DB write FAILED for $curSessionTrackId, delta=$timeElapsedSinceLastUpdate", e)
			}
		}
	}

	private fun resetInternal() {
		sessionTrackId = null
		sessionTotalDurationMs = null
		sessionTotalListenTime = null
		sessionLastUpdateTimeMs = null
		wasSessionPlayCountLogged = false

		stopSession()
	}

	private suspend fun flushInternal() {
		updateDB()
		updateJob?.join()
	}

	suspend fun reset() {
		sessionMutex.withLock {
			resetInternal()
		}
	}

	suspend fun flush() {
		sessionMutex.withLock {
			flushInternal()
		}
	}

	private fun startSessionInternal() {
		val curTrackId = getCurTrackId()
		if (curTrackId == null) {
			Log.e(LOG_TAG, "Current track id is null (no music is playing)")
			return
		}

		stopSession()
		sessionTrackId = curTrackId
		sessionTotalDurationMs = player.duration
		sessionTotalListenTime = 0L
		sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
		wasSessionPlayCountLogged = false

		loopJob = scope.launch {
			while (true) {
				val updateDelayMs = (updateDelaySec * 1000).milliseconds
				delay(updateDelayMs)

				if (player.isPlaying) {
					sessionMutex.withLock {
						updateDB()
					}
				}
			}
		}
	}

	/*
	fun startSession() {
		scope.launch {
			sessionMutex.withLock {
				startSessionInternal()
			}
		}
	}
	 */

	fun stopSession() {
		loopJob?.cancel()
		loopJob = null
		updateJob?.cancel()
		updateJob = null
	}


	override fun onIsPlayingChanged(isPlaying: Boolean) {
		if (isPlaying) {
			scope.launch {
				sessionMutex.withLock {
					if (getCurTrackId() == sessionTrackId) {
						sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
					} else {
						startSessionInternal()
					}
				}
			}
		} else {
			scope.launch {
				sessionMutex.withLock {
					updateDB()
				}
			}
		}
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		if (mediaItem == null) {
			scope.launch {
				sessionMutex.withLock {
					flushInternal()
					resetInternal()
				}
			}

			return
		}

		scope.launch {
			sessionMutex.withLock {
				flushInternal()
				resetInternal()

				if (player.isPlaying) {
					startSessionInternal()
				}
			}
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		if (playbackState == Player.STATE_ENDED && sessionTrackId != null) {
			scope.launch {
				sessionMutex.withLock {
					flushInternal()
				}
			}
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		Log.e(LOG_TAG, "$error")
		stopSession()
	}
}