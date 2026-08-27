package com.kwiby.musik.data.services.playback_service.components

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class SleepTimerController(
	private val player: ExoPlayer,
	parentScope: CoroutineScope
) {
	private val sleepTimerScope = CoroutineScope(
		SupervisorJob(parentScope.coroutineContext[Job]) + Dispatchers.Default
	)
	private var sleepTimerJob: Job? = null

	private val _remainingMs = MutableStateFlow(0L)
	val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

	private val _isSleepTimerSet = MutableStateFlow(false)
	val isSleepTimerSet: StateFlow<Boolean> = _isSleepTimerSet.asStateFlow()

	private val playerListener = object: Player.Listener {
		override fun onIsPlayingChanged(isPlaying: Boolean) {
			if (isPlaying) {
				resumeSleepTimer()
			} else {
				pauseSleepTimer()
			}
		}
	}


	private fun resumeSleepTimer() {
		if (!_isSleepTimerSet.value || sleepTimerJob?.isActive == true) return

		sleepTimerJob = sleepTimerScope.launch(Dispatchers.Default) {
			while (isActive) {
				delay(1.seconds)

				val nextVal = _remainingMs.value - 1000
				if (nextVal <= 0) {
					_remainingMs.value = 0L
					withContext(Dispatchers.Main.immediate) {
						onSleepTimerDone()
					}

					break
				}
				_remainingMs.value = nextVal
			}
		}
	}

	private fun pauseSleepTimer() {
		sleepTimerJob?.cancel()
		sleepTimerJob = null
	}

	private fun onSleepTimerDone() {
		pauseSleepTimer()
		_isSleepTimerSet.value = false
		player.pause()
	}

	fun setSleepTimer(durationMs: Long) {
		_isSleepTimerSet.value = true
		_remainingMs.value = durationMs

		sleepTimerScope.launch(Dispatchers.Main.immediate) {
			if (player.isPlaying) {
				resumeSleepTimer()
			}
		}
	}

	fun stopSleepTimer() {
		pauseSleepTimer()
		_isSleepTimerSet.value = false
		_remainingMs.value = 0L
	}

	fun release() {
		player.removeListener(playerListener)
		stopSleepTimer()
	}


	init {
		player.addListener(playerListener)
	}
}