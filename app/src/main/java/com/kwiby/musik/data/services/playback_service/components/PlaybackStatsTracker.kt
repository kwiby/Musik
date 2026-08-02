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
import kotlinx.coroutines.withContext
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

		updateJob = scope.launch {
			withContext(Dispatchers.IO) {
				Log.d("debug", "Update job launched (curSessionTrackId=$curSessionTrackId - wasSessionPlayCountLogged=$wasSessionPlayCountLogged - totalListenTime=$totalListenTime - playCountThresholdMs=$playCountThresholdMs - totalDuration=$totalDuration)")
				try {
					if (doLogPlayCount) {
						Log.d("debug", "Incrementing play count")
						musicStatsRepo.logPlayCount(curSessionTrackId)
					}
					musicStatsRepo.logListenTime(curSessionTrackId, timeElapsedSinceLastUpdate)
					Log.d("debug", "DB write SUCCEEDED for $curSessionTrackId, delta=$timeElapsedSinceLastUpdate")
				} catch (e: Exception) {
					Log.e("debug", "DB write FAILED for $curSessionTrackId, delta=$timeElapsedSinceLastUpdate", e)
				}
			}
		}
	}

	suspend fun flush() {
		updateDB()
		updateJob?.join()
	}

	fun startSession() {
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
					Log.d("debug", "Automatically updating database")
					updateDB()
				}
			}
		}
	}

	fun stopSession() {
		loopJob?.cancel()
		loopJob = null
		updateJob?.cancel()
		updateJob = null
	}

	fun reset() {
		sessionTrackId = null
		sessionTotalDurationMs = null
		sessionTotalListenTime = null
		sessionLastUpdateTimeMs = null
		wasSessionPlayCountLogged = false

		stopSession()
	}


	override fun onIsPlayingChanged(isPlaying: Boolean) {
		if (isPlaying) {
			if (getCurTrackId() == sessionTrackId) {
				Log.d("debug", "Media has played")
				sessionLastUpdateTimeMs = SystemClock.elapsedRealtime()
			} else {
				Log.d("debug", "Session has started (sesh=$sessionTrackId - new=${getCurTrackId()})")
				scope.launch {
					sessionMutex.withLock { startSession() }
				}
			}
		} else {
			Log.d("debug", "Media has paused (updating database)")
			updateDB()
		}
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		if (mediaItem == null) {
			Log.d("debug", "Media item is null, resetting session")
			scope.launch {
				flush()
				reset()
			}

			return
		}

		if (sessionTrackId != null) {
			Log.d("debug", "Media has transitioned (updating database & starting session)")
			scope.launch {
				sessionMutex.withLock {
					flush()
					startSession()
				}
			}
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		if (playbackState == Player.STATE_ENDED && sessionTrackId != null) {
			Log.d("debug", "Playback state changed (updating database)")
			scope.launch {
				flush()
			}
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		Log.e(LOG_TAG, "$error")
		stopSession()
	}
}