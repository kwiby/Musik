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
import kotlinx.coroutines.isActive
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

	private val absMaxDeltaMsErrorGuard = updateDelaySec * 1000L * 3L

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
			Log.e(LOG_TAG, "Cannot update the database, update job is active")
			sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
			return
		}

		val playCountThresholdMs = playCountThresholdSec * 1000L
		val totalDuration = sessionTotalDurationMs!!
		val curElapsedRealtime = SystemClock.elapsedRealtime()
		var timeElapsedSinceLastUpdate = curElapsedRealtime - sessionLastUpdateTimeMs!!
		sessionLastUpdateTimeMs = curElapsedRealtime

		if (timeElapsedSinceLastUpdate < 0) {
			Log.e(LOG_TAG, "Skipping db update due to negative delta ($timeElapsedSinceLastUpdate)")
			return
		}
		if (timeElapsedSinceLastUpdate > absMaxDeltaMsErrorGuard) {
			Log.e(
				LOG_TAG,
				"Erroneous delta ($timeElapsedSinceLastUpdate) for track $curSessionTrackId, " +
						"clamping to $absMaxDeltaMsErrorGuard ms"
			)
			timeElapsedSinceLastUpdate = absMaxDeltaMsErrorGuard
		}

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
				Log.d(LOG_TAG, "WRITE: track=$curSessionTrackId delta=$timeElapsedSinceLastUpdate")
				musicStatsRepo.logListenTime(curSessionTrackId, timeElapsedSinceLastUpdate)
			} catch (e: Exception) {
				Log.e(LOG_TAG, "DB write FAILED for $curSessionTrackId, delta=$timeElapsedSinceLastUpdate", e)
			}
		}
	}

	private suspend fun resetInternal() {
		sessionTrackId = null
		sessionTotalDurationMs = null
		sessionTotalListenTime = null
		sessionLastUpdateTimeMs = null
		wasSessionPlayCountLogged = false

		stopSessionInternal()
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

	private suspend fun startSessionInternal() {
		val curTrackId = getCurTrackId()
		if (curTrackId == null) {
			Log.e(LOG_TAG, "Current track id is null (no music is playing)")
			return
		}

		stopSessionInternal()
		sessionTrackId = curTrackId
		sessionTotalDurationMs = player.duration
		sessionTotalListenTime = 0L
		sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
		wasSessionPlayCountLogged = false

		loopJob = scope.launch {
			while (isActive) {
				val updateDelayMs = (updateDelaySec * 1000).milliseconds
				delay(updateDelayMs)

				if (!isActive) break

				if (player.isPlaying && getCurTrackId() == curTrackId) {
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

	private suspend fun stopSessionInternal() {
		loopJob?.cancel()
		loopJob?.join()
		loopJob = null

		updateJob?.cancel()
		updateJob?.join()
		updateJob = null
	}


	override fun onIsPlayingChanged(isPlaying: Boolean) {
		scope.launch {
			sessionMutex.withLock {
				if (isPlaying) {
					if (getCurTrackId() == sessionTrackId && sessionTrackId != null) {
						Log.d(LOG_TAG, "onIsPlayingChanged() A")
						sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
					} else {
						Log.d(LOG_TAG, "onIsPlayingChanged() B")
						startSessionInternal()
					}
				} else {
					Log.d(LOG_TAG, "onIsPlayingChanged() C")
					updateDB()
				}
			}
		}
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		if (mediaItem == null) {
			scope.launch {
				sessionMutex.withLock {
					Log.d(LOG_TAG, "onMediaItemTransition() A")
					flushInternal()
					resetInternal()
				}
			}

			return
		}

		scope.launch {
			sessionMutex.withLock {
				Log.d(LOG_TAG, "onMediaItemTransition() B")
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
			val endedTrackId = sessionTrackId
			scope.launch {
				sessionMutex.withLock {
					if (sessionTrackId == endedTrackId) {
						Log.d(LOG_TAG, "C: STATE_ENDED flush for $endedTrackId")
						flushInternal()
					} else {
						Log.d(LOG_TAG, "C: STATE_ENDED skipped, session moved to $sessionTrackId")
					}
				}
			}
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		Log.e(LOG_TAG, "$error")
		scope.launch {
			sessionMutex.withLock {
				flushInternal()
				resetInternal()
			}
		}
	}
}