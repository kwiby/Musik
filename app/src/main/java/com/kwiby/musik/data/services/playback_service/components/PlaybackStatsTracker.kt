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
	private val updateDelaySec = 30
	private val playCountThresholdSec = 3

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
			Log.e(LOG_TAG, "Cannot update the database, session track id is null (expected on app launch)")
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

		updateJob = scope.launch(Dispatchers.IO) {
			Log.d("debug", "Update job launched (wasSessionPlayCountLogged=$wasSessionPlayCountLogged - totalListenTime=$totalListenTime - playCountThresholdMs=$playCountThresholdMs - totalDuration=$totalDuration)")
			if (!wasSessionPlayCountLogged && totalListenTime >= minOf(playCountThresholdMs, totalDuration)) {
				Log.d("debug", "Incrementing play count")
				wasSessionPlayCountLogged = true
				musicStatsRepo.logPlayCount(curSessionTrackId)
			}

			musicStatsRepo.logListenTime(curSessionTrackId, timeElapsedSinceLastUpdate)
		}
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

				Log.d("debug", "Automatically updating database")
				if (player.isPlaying) {
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
				startSession()
			}
		} else {
			Log.d("debug", "Media has paused (updating database)")
			updateDB()
		}
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		if (sessionTrackId != null) {
			Log.d("debug", "Media has transitioned (updating database & starting session)")
			updateDB()
			startSession()
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		Log.e(LOG_TAG, "$error")
		stopSession()
	}
}